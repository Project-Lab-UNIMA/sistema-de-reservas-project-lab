# Dicionário de dados — MVP

Data: 19/10/2026

Versão: 1.0 — proposta da Etapa 3, associada ao
[modelo lógico](MODELO-LOGICO-MVP.md).

Este documento define as **13 tabelas propostas**, seus campos, obrigatoriedade,
chaves e domínios. Os tipos sugeridos consideram PostgreSQL 16; a sintaxe de DDL,
os nomes de constraints, as triggers e os mapeamentos JPA pertencem à próxima
etapa, após revisão. As [Regras do MVP](../requisitos/REGRAS-MVP.md) continuam
sendo a referência de comportamento.

## 1. Como ler

- **Nulo = Não**: a coluna será `NOT NULL`; isso não significa que o cliente
  fornece o valor. IDs, instantes, snapshots e autores são determinados pelo backend.
- **Nulo = Sim**: ausência é permitida nas situações indicadas. Condições entre
  campos podem tornar o preenchimento obrigatório em determinado estado.
- **PK**: chave primária; **FK**: referência; **UQ**: unicidade. Uma UQ em conjunto
  aplica-se à combinação, não a cada coluna isoladamente.
- `id`: `bigint` positivo gerado por identidade, imutável. Códigos de negócio não
  substituem o ID técnico, exceto a chave de `perfil`.
- Instantes usam `timestamptz(6)` e precisão de microssegundos; a aplicação usa
  `Clock` e `Instant`. Horários semanais usam `time(6) without time zone` e
  `LocalTime`. Fuso usa identificador IANA, por exemplo `America/Maceio`.
- Campos `versao` começam em `0` e são controlados por `@Version`; não são
  editáveis pelo cliente. Códigos de enum são mapeados como texto, nunca ordinal.
- Textos obrigatórios e justificativas preenchidas não podem ser vazios após
  `trim`. Comprimentos são limites técnicos propostos, sujeitos à revisão.
- Não usar `CURRENT_TIMESTAMP` como única fonte de prazos testáveis. Instantes
  de negócio são fornecidos pelo serviço com o relógio centralizado.
- Exclusões de registros históricos não fazem parte do fluxo. FKs históricas
  usam `RESTRICT`; detalhes estão no modelo lógico.

## 2. `campus`

Localização institucional. O MVP continua atendendo um campus.

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `codigo` | `varchar(30)` | Não | UQ; código estável, normalizado com `trim` e maiúsculas |
| `nome` | `varchar(120)` | Não | Nome de apresentação |
| `fuso_horario` | `varchar(60)` | Não | Identificador IANA validado pelo backend; não usar apenas deslocamento como `-03:00` |

Alterar o cadastro não altera as cópias de fuso já preservadas em reservas.
Mudança de fuso requer revisão técnica das agendas; não há tela de alteração
de fuso prevista no recorte.

## 3. `predio`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `campus_id` | `bigint` | Não | FK → `campus.id` |
| `codigo` | `varchar(30)` | Não | Código normalizado com `trim` e maiúsculas |
| `nome` | `varchar(120)` | Não | Nome de apresentação |

UQ: `(campus_id, codigo)`. Um prédio pertence a exatamente um campus. Não mover
um prédio ou ambiente entre localizações para reinterpretar reservas antigas;
alterações de estrutura precisam preservar sua rastreabilidade.

## 4. `ambiente`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `predio_id` | `bigint` | Não | FK → `predio.id` |
| `codigo` | `varchar(30)` | Não | Código normalizado com `trim` e maiúsculas |
| `nome` | `varchar(120)` | Não | Nome de apresentação |
| `tipo` | `varchar(20)` | Não | `SALA` ou `LABORATORIO`; somente ambientes sem habilitação especial podem ser cadastrados como reserváveis neste MVP |
| `capacidade` | `integer` | Não | Maior que zero; capacidade atual |
| `ativo` | `boolean` | Não | Inicialmente `true`; inativo não recebe novos pedidos |
| `antecedencia_minima_segundos` | `integer` | Não | Não negativa; inicial `7200` |
| `antecedencia_maxima_segundos` | `integer` | Não | Positiva e não inferior à mínima; inicial `2592000` |
| `duracao_maxima_segundos` | `integer` | Não | Positiva; inicial `14400` |
| `preparacao_segundos` | `integer` | Não | Não negativa; inicial `900` |
| `limpeza_segundos` | `integer` | Não | Não negativa; inicial `900` |
| `tolerancia_check_in_segundos` | `integer` | Não | Não negativa; inicial `900` |
| `prazo_pre_bloqueio_segundos` | `integer` | Não | Positivo; inicial `86400` |
| `versao` | `bigint` | Não | Controle de concorrência, inicialmente `0` |

UQ: `(predio_id, codigo)`. Todos os parâmetros são configuração atual; a reserva
guarda os valores aplicados. Não criar uma opção de laboratório restrito que
possa ser reservada por simples concessão de perfil neste MVP.

Redução de capacidade/funcionamento ou inativação depende da verificação
transacional de pedidos futuros afetados. Permissão para editar parâmetros
não deve ser inferida apenas pela existência destas colunas.

## 5. `faixa_funcionamento`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `ambiente_id` | `bigint` | Não | FK → `ambiente.id` |
| `dia_semana` | `smallint` | Não | ISO: `1` a `7`, de segunda a domingo |
| `abertura` | `time(6) without time zone` | Não | Horário local do início da faixa |
| `fechamento` | `time(6) without time zone` | Não | Horário local do fim da faixa |
| `fecha_meia_noite` | `boolean` | Não | Inicialmente `false`; `true` significa fechamento às `00:00` do dia seguinte |

Se `fecha_meia_noite = false`, exigir `abertura < fechamento`. Se for `true`,
exigir `fechamento = 00:00`; o fim é a meia-noite seguinte, inclusive no caso
de abertura às `00:00` para representar 24 horas de funcionamento.

Não permitir faixas sobrepostas no mesmo ambiente/dia. Adjacentemente podem
compor funcionamento contínuo; a etapa física deverá definir essa exclusão e
testar a cobertura dos buffers em dias vizinhos, incluindo domingo/segunda.
Faixas podem ser substituídas na configuração atual sob bloqueio do ambiente,
com auditoria e verificação de reservas futuras. O histórico não depende de
manter uma FK da reserva para a faixa atual.

## 6. `usuario`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica; identidade usada na proibição de autoaprovação |
| `nome` | `varchar(150)` | Não | Nome de apresentação |
| `identificacao_institucional` | `varchar(80)` | Não | UQ após `trim`; não presumir formato numérico |
| `identificacao_acesso` | `varchar(254)` | Não | UQ; persistida com `trim` e minúsculas, na mesma forma usada no login |
| `senha_hash` | `varchar(255)` | Sim | Hash para autenticação local; nulo impede esse modo de login. Nunca senha em texto |
| `ativo` | `boolean` | Não | Inicialmente `true`; inativar bloqueia acesso e novas ações do usuário |
| `criado_em` | `timestamptz(6)` | Não | Instante de criação, imutável |
| `versao` | `bigint` | Não | Controle de concorrência, inicialmente `0` |

A identificação não é reciclada para outra pessoa. Inativar preserva autoria,
reservas e decisões; não é cancelamento implícito de todos os pedidos antigos.
O grupo deverá revisar o tratamento operacional desses pedidos se necessário.
`senha_hash` não entra em DTOs, logs, notificações ou JSON de auditoria.

## 7. `perfil`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `codigo` | `varchar(20)` | Não | PK textual: `PROFESSOR`, `MONITOR`, `COORDENACAO` ou `DIRECAO` |

Catálogo técnico fechado, inserido na migração estrutural. Os rótulos de exibição
são definidos no código. Não há criação arbitrária de novos perfis no MVP.

## 8. `usuario_perfil`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `usuario_id` | `bigint` | Não | PK parcial e FK → `usuario.id` |
| `perfil_codigo` | `varchar(20)` | Não | PK parcial e FK → `perfil.codigo` |

PK composta `(usuario_id, perfil_codigo)`: um vínculo de cada perfil por pessoa.
Zero vínculos é permitido, mas não habilita solicitação nem aprovação. Exclusão
de vínculo remove uma permissão atual; o evento de remoção preserva autor,
destinatário, código de perfil e instante. Atribuir e remover exigem autorização;
o fluxo normal não permite autoatribuição.

## 9. `reserva`

### 9.1 Identificação e pedido

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `ambiente_id` | `bigint` | Não | FK → `ambiente.id`; imutável após envio |
| `solicitante_id` | `bigint` | Não | FK → `usuario.id`; imutável |
| `finalidade` | `varchar(1000)` | Não | Objetivo informado no pedido; preservado após envio |
| `participantes_previstos` | `integer` | Não | Positivo e até `capacidade_aplicada`; proposta para revisão com o grupo |
| `inicio_previsto` | `timestamptz(6)` | Não | Início da utilização, imutável após envio |
| `fim_previsto` | `timestamptz(6)` | Não | Fim da utilização, estritamente posterior ao início e imutável |
| `estado` | `varchar(20)` | Não | Domínio da seção 15; inicialmente `PENDENTE` |
| `criada_em` | `timestamptz(6)` | Não | Momento do envio válido; imutável |
| `versao` | `bigint` | Não | Controle de concorrência, inicialmente `0` |

### 9.2 Configuração aplicada e limites preservados

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `fuso_horario_aplicado` | `varchar(60)` | Não | Cópia do fuso do campus usada nas regras locais de data e funcionamento |
| `capacidade_aplicada` | `integer` | Não | Positiva; capacidade no envio |
| `antecedencia_minima_segundos_aplicada` | `integer` | Não | Não negativa; cópia da configuração usada |
| `antecedencia_maxima_segundos_aplicada` | `integer` | Não | Positiva e não inferior à mínima; cópia da configuração usada |
| `duracao_maxima_segundos_aplicada` | `integer` | Não | Positiva; limite de utilização aplicado |
| `preparacao_segundos_aplicada` | `integer` | Não | Não negativa; duração antes da utilização |
| `limpeza_segundos_aplicada` | `integer` | Não | Não negativa; duração após a utilização |
| `tolerancia_check_in_segundos_aplicada` | `integer` | Não | Não negativa; tolerância inclusiva, sem superar o término |
| `prazo_pre_bloqueio_segundos_aplicado` | `integer` | Não | Positivo; duração máxima provisória aplicada |
| `expira_em` | `timestamptz(6)` | Não | Menor instante entre criação + prazo aplicado e início previsto |
| `ocupacao_inicio` | `timestamptz(6)` | Não | Início previsto menos preparação aplicada |
| `ocupacao_fim` | `timestamptz(6)` | Não | Fim previsto mais limpeza aplicada; limite exclusivo |

Os 12 campos desta subseção são imutáveis após criação. O fuso e a capacidade
também são snapshots; não dependem de consultas posteriores ao cadastro atual.
O intervalo de ocupação não é recalculado em encerramento antecipado.

### 9.3 Entrada, cancelamento e encerramento

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `check_in_em` | `timestamptz(6)` | Sim | Instante do check-in válido do próprio solicitante |
| `cancelada_em` | `timestamptz(6)` | Sim | Preenchido somente em `CANCELADA`, antes do início previsto |
| `cancelada_por_id` | `bigint` | Sim | FK → `usuario.id`; obrigatório em `CANCELADA` |
| `tipo_cancelamento` | `varchar(20)` | Sim | `SOLICITANTE` ou `ADMINISTRATIVO`; obrigatório em `CANCELADA` |
| `motivo_cancelamento` | `varchar(1000)` | Sim | Obrigatório e não vazio em `CANCELADA` |
| `encerrada_em` | `timestamptz(6)` | Sim | Momento do registro de encerramento, somente em `ENCERRADA` |
| `encerrada_por_id` | `bigint` | Sim | FK → `usuario.id`; obrigatório em `ENCERRADA` |
| `tipo_encerramento` | `varchar(20)` | Sim | `SOLICITANTE` ou `ADMINISTRATIVO`; obrigatório em `ENCERRADA` |
| `justificativa_encerramento` | `varchar(1000)` | Sim | Obrigatória e não vazia no encerramento administrativo; nula no normal |

### 9.4 Invariantes locais e transacionais

As migrações deverão distinguir verificações sobre a própria linha de condições
que exigem outras tabelas. O serviço deve garantir, no mínimo:

1. Instantes finitos, `inicio_previsto < fim_previsto`, antecedência inclusiva
   entre mínima e máxima aplicada, duração até o máximo aplicado e utilização
   em uma única data no fuso aplicado.
2. `criada_em < expira_em <= inicio_previsto`; expiração exatamente no menor
   prazo definido, sem alteração posterior. A janela de antecedência garante
   que o pedido válido inicial tem início futuro.
3. `ocupacao_inicio <= inicio_previsto < fim_previsto <= ocupacao_fim` e
   correspondência exata dos limites com os buffers, além da cobertura pelo
   funcionamento e proteção contra sobreposição.
4. `check_in_em` nulo nos estados anteriores ao uso e preenchido somente em
   `EM_USO` ou `ENCERRADA`; quando presente, respeita as duas fronteiras do
   modelo lógico, inclusive a tolerância e exclusive o término.
5. Todos os quatro campos de cancelamento preenchidos em `CANCELADA` e nulos
   nos demais estados; `criada_em <= cancelada_em < inicio_previsto`.
   Cancelamento pelo solicitante exige `cancelada_por_id = solicitante_id`.
6. Encerramento exige instante, autor e tipo e preserva o check-in;
   `encerrada_em >= check_in_em`. Nos demais estados, os campos de encerramento
   são nulos. Encerramento normal exige autor igual ao solicitante e checklist
   completo; administrativo exige perfil autorizado e justificativa.
7. Nenhuma decisão em `PENDENTE` ou `EXPIRADA`; decisão recusada em `RECUSADA`;
   decisão aprovada em `APROVADA`, `EM_USO`, `ENCERRADA` e `NAO_COMPARECEU`.
   Em `CANCELADA`, decisão ausente se cancelou pendente ou aprovada se já
   existia aprovação. Cancelamento nunca apaga essa decisão.
8. Criação e cada mudança de estado têm evento correspondente na mesma
   transação. Estados terminais não retornam à aprovação.

UQ adicional `(id, ambiente_id)`: alvo da FK composta do checklist. Não é um
segundo identificador da reserva nem permite repetir o ID.

## 10. `decisao_aprovacao`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `reserva_id` | `bigint` | Não | FK → `reserva.id`; UQ garante decisão única |
| `autor_id` | `bigint` | Não | FK → `usuario.id`; pessoa que decidiu |
| `resultado` | `varchar(10)` | Não | `APROVADA` ou `RECUSADA` |
| `decidida_em` | `timestamptz(6)` | Não | Momento da decisão; antes de `reserva.expira_em` e não anterior à criação |
| `motivo_recusa` | `varchar(1000)` | Sim | Obrigatório e não vazio em recusa; nulo em aprovação |

Somente coordenação/direção, sem decidir o próprio pedido. A proposta aplica
essa separação de autor também à recusa, em acordo com a matriz da Etapa 1.
Todos os campos ficam imutáveis após inserção. Cancelamento, expiração e
regularização não são decisões adicionais nesta tabela.

## 11. `item_checklist_ambiente`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica; preservada quando o item for inativado |
| `ambiente_id` | `bigint` | Não | FK → `ambiente.id`; imutável |
| `descricao` | `varchar(500)` | Não | Texto atual do item |
| `ordem` | `integer` | Não | Positiva; posição de apresentação |
| `obrigatorio` | `boolean` | Não | Se o item precisa de confirmação no fechamento normal |
| `ativo` | `boolean` | Não | Inicialmente `true`; somente itens ativos são copiados para novos pedidos |

UQ adicional `(id, ambiente_id)` para a FK composta histórica. Ordem única entre
itens ativos do mesmo ambiente: candidato a índice único parcial
`(ambiente_id, ordem)` condicionado a `ativo`. A inativação permite reutilizar a
posição sem apagar o item de origem. Alteração e cópia usam o bloqueio do ambiente.

## 12. `item_checklist_reserva`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `reserva_id` | `bigint` | Não | Parte da FK composta → `reserva(id, ambiente_id)` |
| `item_origem_id` | `bigint` | Não | Parte da FK composta → `item_checklist_ambiente(id, ambiente_id)` |
| `ambiente_id` | `bigint` | Não | Participa das duas FKs compostas e deve coincidir com o ambiente de ambas |
| `descricao_aplicada` | `varchar(500)` | Não | Cópia histórica do texto |
| `ordem_aplicada` | `integer` | Não | Cópia histórica da ordem positiva |
| `obrigatorio_aplicado` | `boolean` | Não | Cópia histórica da obrigatoriedade |
| `resposta` | `boolean` | Sim | Inicialmente nula; `true` confirma, `false` não confirma |
| `respondida_em` | `timestamptz(6)` | Sim | Preenchida se e somente se houver resposta |

UQ: `(reserva_id, item_origem_id)` e `(reserva_id, ordem_aplicada)`.
Identificadores, descrição, ordem e obrigatoriedade copiados são imutáveis.
O solicitante da reserva é a pessoa autorizada a responder, durante `EM_USO`;
o momento não antecede o check-in. Alterações de resposta durante o uso são
auditadas. Após encerramento, nenhuma resposta pode ser alterada.

## 13. `evento_auditoria`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `reserva_id` | `bigint` | Sim | FK → `reserva.id`; obrigatória para o ciclo de uma reserva |
| `origem` | `varchar(10)` | Não | `HUMANA` ou `SISTEMA` |
| `autor_usuario_id` | `bigint` | Sim | FK → `usuario.id`; obrigatório em origem humana e nulo na origem sistema |
| `acao` | `varchar(50)` | Não | Código estável do catálogo de ações da seção 15 |
| `ocorrido_em` | `timestamptz(6)` | Não | Instante em que a ação foi processada; não substituir pelo prazo passado de uma rotina atrasada |
| `alvo_tipo` | `varchar(40)` | Não | Tipo de objeto permitido, conforme seção 15 |
| `alvo_chave` | `varchar(200)` | Não | Chave histórica canônica do objeto; não é FK genérica |
| `motivo` | `varchar(1000)` | Sim | Justificativa quando a ação a exigir, incluindo recusa, cancelamento e regularização |
| `dados_antes` | `jsonb` | Sim | Objeto JSON com campos anteriores permitidos; nulo na criação |
| `dados_depois` | `jsonb` | Sim | Objeto JSON com campos posteriores permitidos |
| `chave_idempotencia` | `varchar(160)` | Sim | UQ entre valores não nulos; chave estável para efeito automático que só pode ocorrer uma vez |

Todos os campos são imutáveis. Exigir origem/autor coerentes. Quando presentes,
os JSONs devem ser objetos; nunca serializar entidades indiscriminadamente.
Para ações com alvo `RESERVA`, conferir `alvo_chave` igual ao ID decimal canônico
e `reserva_id` correspondente. Para `CHECKLIST_RESPONDIDO`, conferir que o item
alvo pertence à reserva referenciada. Nos demais eventos, a chave é construída
pelo serviço a partir do objeto afetado.

Chaves de alvo: ID decimal nas tabelas com ID técnico; código textual em
`PERFIL`; `<usuario_id>:<perfil_codigo>` em `USUARIO_PERFIL`. Uma resposta de
checklist pode usar alvo `ITEM_CHECKLIST_RESERVA` e conservar a FK de reserva.
Não há FK genérica para esses alvos; a validação transacional deve ser explícita.

Exemplos de idempotência: `reserva:123:expiracao` e
`reserva:123:nao_comparecimento`. Não gerar uma chave aleatória nova a cada
tentativa da mesma tarefa. O detalhe de prazo efetivo fica nos dados permitidos
do evento e nos campos preservados da reserva.

## 14. `notificacao`

| Campo | Tipo proposto | Nulo | Regra e significado |
| --- | --- | --- | --- |
| `id` | `bigint` | Não | PK técnica |
| `evento_id` | `bigint` | Não | FK → `evento_auditoria.id` |
| `destinatario_id` | `bigint` | Não | FK → `usuario.id` |
| `titulo` | `varchar(150)` | Não | Título do aviso interno |
| `mensagem` | `varchar(1000)` | Não | Conteúdo do aviso, sem credenciais ou dados desnecessários |
| `criada_em` | `timestamptz(6)` | Não | Instante de criação, não anterior ao evento |
| `lida_em` | `timestamptz(6)` | Sim | Nula até leitura pelo destinatário; preenchida uma única vez e não anterior à criação |

UQ: `(evento_id, destinatario_id)`. Conteúdo, evento e destinatário são imutáveis;
repetir leitura preserva o primeiro instante. Não expor uma notificação a outra
pessoa apenas porque conhece o ID. Cancelamento administrativo exige aviso ao
solicitante; os demais eventos notificáveis continuam para revisão de escopo.

## 15. Domínios e catálogo de ações

| Campo ou grupo | Valores permitidos |
| --- | --- |
| `ambiente.tipo` | `SALA`, `LABORATORIO` |
| `perfil.codigo` | `PROFESSOR`, `MONITOR`, `COORDENACAO`, `DIRECAO` |
| `reserva.estado` | `PENDENTE`, `APROVADA`, `RECUSADA`, `CANCELADA`, `EXPIRADA`, `EM_USO`, `NAO_COMPARECEU`, `ENCERRADA` |
| `decisao_aprovacao.resultado` | `APROVADA`, `RECUSADA` |
| Tipos de cancelamento e encerramento | `SOLICITANTE`, `ADMINISTRATIVO` |
| `evento_auditoria.origem` | `HUMANA`, `SISTEMA` |
| `evento_auditoria.alvo_tipo` | `CAMPUS`, `PREDIO`, `AMBIENTE`, `FAIXA_FUNCIONAMENTO`, `USUARIO`, `PERFIL`, `USUARIO_PERFIL`, `RESERVA`, `ITEM_CHECKLIST_AMBIENTE`, `ITEM_CHECKLIST_RESERVA` |

Catálogo inicial proposto para `evento_auditoria.acao`:

| Ações | Alvo e uso |
| --- | --- |
| `RESERVA_CRIADA`, `RESERVA_APROVADA`, `RESERVA_RECUSADA`, `RESERVA_CANCELADA`, `RESERVA_EXPIRADA` | Alvo `RESERVA` e FK obrigatória; justificativa conforme a ação |
| `CHECK_IN`, `NAO_COMPARECEU`, `RESERVA_ENCERRADA`, `ENCERRAMENTO_REGULARIZADO` | Alvo `RESERVA` e FK obrigatória; regularização exige justificativa |
| `CHECKLIST_RESPONDIDO` | Alvo `ITEM_CHECKLIST_RESERVA`, FK de reserva e autor solicitante |
| `USUARIO_CRIADO`, `USUARIO_ALTERADO` | Alvo `USUARIO`; jamais copiar hash ou segredo |
| `PERFIL_ATRIBUIDO`, `PERFIL_REMOVIDO` | Alvo `USUARIO_PERFIL`, chave composta e código preservado nos dados |
| `AMBIENTE_CRIADO`, `AMBIENTE_ALTERADO` | Alvo `AMBIENTE`, incluindo situação e parâmetros alterados |
| `FUNCIONAMENTO_ALTERADO`, `CHECKLIST_CONFIGURADO` | Alvo `AMBIENTE`, com configuração anterior/posterior permitida |

Os tipos de alvo `CAMPUS`, `PREDIO`, `FAIXA_FUNCIONAMENTO` e `PERFIL` identificam
conceitos existentes, mas não autorizam novos fluxos de edição. Se houver
provisionamento auditado dessas estruturas, definir suas ações explicitamente
na etapa física. O catálogo não é campo livre fornecido pelo cliente.

## 16. Relação com Java/JPA e revisão

| Representação proposta | Diretriz para o backend |
| --- | --- |
| `bigint` | `Long` para IDs, geração por identidade; `long`/`Long` para versão conforme o mapeamento |
| PK de `usuario_perfil` | Chave composta explícita; não introduzir ID substituto sem atualizar este contrato |
| `timestamptz(6)` | `Instant`, com normalização da precisão da operação |
| Horários locais e flag de meia-noite | `LocalTime` e `boolean`; fechar no dia seguinte quando a flag indicar |
| Domínios textuais | Enum Java persistido por nome; alinhar os códigos ao `CHECK` SQL |
| JSON histórico | Mapeamento JSONB escolhido com o backend; objetos permitidos por ação |
| Relações | Carregamento explícito para DTOs; `open-in-view` já está desativado |

As unicidades, FKs compostas, colunas imutáveis e regras condicionais deste
dicionário precisam aparecer na revisão das migrações e dos testes. Apenas
anotações JPA e o `ddl-auto: validate` não comprovam essas garantias.

Antes do primeiro SQL, revisar os itens da seção 11 do
[modelo lógico](MODELO-LOGICO-MVP.md), especialmente autenticação, participantes,
fuso/precisão, snapshots, concorrência e responsabilidade pelas migrações.
