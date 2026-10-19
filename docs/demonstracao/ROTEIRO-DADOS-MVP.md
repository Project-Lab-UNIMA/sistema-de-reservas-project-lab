# Dados e roteiro de demonstração do MVP

Data: 19/10/2026

Situação: preparação para a futura implementação; não há carga SQL nem contas
criadas por este documento. Os exemplos são fictícios e precisam ser ajustados
com o grupo antes da apresentação.

Referências: [Regras do MVP](../requisitos/REGRAS-MVP.md) e
[plano de validação](../testes/PLANO-VALIDACAO-MVP.md).

## 1. Como preparar sem depender do esquema

Os nomes abaixo são **apelidos de cenário**, não IDs, nomes de tabela ou logins
definitivos. A equipe pode decidir agora quais pessoas fictícias, ambientes,
horários e itens deseja demonstrar. A transformação em registros, credenciais
locais e migrações de dados fica para depois da revisão da Etapa 3.

## 2. Pessoas fictícias

| Apelido | Perfis | Finalidade |
| --- | --- | --- |
| PROF_A | Professor | Solicitar, cancelar a própria reserva, entrar e encerrar |
| PROF_B | Professor | Disputar horário com outro solicitante |
| MON_A | Monitor | Mostrar que monitor também pode solicitar |
| COORD_A | Coordenação | Solicitar e decidir pedidos de outras pessoas |
| DIRECAO_A | Direção | Administrar cadastros e regularizar encerramento |
| PROF_DIRECAO_A | Professor e Direção | Demonstrar vários perfis sem permitir autoaprovação |
| SEM_PERFIL_A | Nenhum | Verificar tentativa de solicitação sem permissão |

Todas são contas fictícias. Definir a forma de login conforme o contrato do
backend; não publicar senhas pessoais, tokens ou credenciais na documentação.
Direção sem outro perfil solicitante não solicita. A primeira conta da direção
é provisionada tecnicamente, sem autocadastro nem autoatribuição pelo uso normal.

## 3. Estrutura e ambientes fictícios

Proposta de cenário: um campus, um prédio e fuso `America/Maceio`. A demonstração
não precisa de vários campi ou de regras de aprovação por departamento.

| Apelido | Tipo | Capacidade de exemplo | Situação | Funcionamento de exemplo |
| --- | --- | --- | --- | --- |
| SALA_A | Sala | 20 | Ativo | Segunda a sexta, 07h–22h |
| LAB_B | Laboratório sem habilitação especial | 12 | Ativo | Segunda a sexta, 07h–22h |
| SALA_INATIVA | Sala | 20 | Inativo | Indisponível para novos pedidos |

Os horários e capacidades são exemplos para ensaio, não dados reais aprovados
da universidade. O grupo deve preencher/confirmar a estrutura real da apresentação.
Não incluir laboratório restrito como ambiente reservável.

Parâmetros do ensaio seguem as regras acordadas: antecedência de 2h a 30 dias,
duração máxima de 4h, preparação e limpeza de 15 minutos, tolerância de entrada
de 15 minutos e pré-bloqueio de até 24h limitado pelo início.

Checklist de exemplo para SALA_A e LAB_B:

| Item proposto | Obrigatório no ensaio? |
| --- | --- |
| Espaço organizado | Sim |
| Equipamentos utilizados desligados | Sim |
| Materiais recolhidos | Sim |

A lista precisa ser validada pelo grupo. Não exige fotos ou anexos. Ao testar
alterações posteriores, guardar o texto original em uma reserva antiga e usar
o texto novo somente nas reservas criadas depois da mudança.

## 4. Planejamento dos horários da apresentação

Uma reserva criada durante a apresentação só pode começar pelo menos **duas
horas depois**. Portanto, solicitar e fazer check-in imediatamente na mesma
reserva não é um roteiro válido com o relógio real.

Separar dois conjuntos:

1. **Pedidos futuros:** criados ao vivo, com início pelo menos 2h à frente, para
   demonstrar solicitação, aprovação, conflito, recusa e cancelamento.
2. **Uso no momento da apresentação:** reservas preparadas anteriormente, com
   criação válida e início dentro do horário da apresentação, para demonstrar
   check-in e checklist.

Expiração de 24h e não comparecimento podem ser apresentados por evidências dos
testes com `Clock` controlado. Se a equipe implementar um ambiente específico
de simulação, ele deve ser identificado como tal. Este roteiro não pressupõe
que já exista um controle de relógio na interface ou um endpoint para alterá-lo.

## 5. Sequência sugerida para o ensaio

Exemplo de sessão: `D`, em dia com funcionamento, começando às **14h**. O grupo
ajusta esse horário à apresentação real. Para os passos de uso, preparar uma
reserva de LAB_B das 14h às 15h, criada às 11h e aprovada antes do início. Fazer
o check-in até 14h15, inclusive. Cada ramificação usa sua própria reserva, sem
introduzir sobreposição que inviabilize os passos seguintes.

| Passo | Preparação e ação | Evidência esperada | Casos de validação |
| --- | --- | --- | --- |
| DEMO-01 | Às 14h de D, PROF_A solicita SALA_A das 17h às 19h | Pedido pendente; agenda protege 16h45–19h15 | VAL-01, VAL-13 |
| DEMO-02 | PROF_B tenta SALA_A 19h15–20h; depois tenta 19h30–20h30 | Primeiro conflita por buffers; segundo pode ser enviado | VAL-07, VAL-08 |
| DEMO-03 | COORD_A aprova pedido de PROF_A; PROF_DIRECAO_A tenta aprovar um pedido próprio separado | Aprovação por outra pessoa funciona; autoaprovação é negada | VAL-03, VAL-04 |
| DEMO-04 | Em pedido separado, testar recusa sem/com motivo e cancelamento pelo titular antes do início | Motivos obrigatórios; histórico preservado e horário liberado | VAL-14, VAL-16 |
| DEMO-05 | Antes de 14h15 ou exatamente às 14h15, o solicitante faz check-in na reserva de LAB_B preparada para 14h–15h | Entrada válida muda para Em uso e registra o instante | VAL-19 |
| DEMO-06 | Tentar encerrar esse uso com item obrigatório pendente; depois confirmar todos | Primeira tentativa é negada; segunda encerra preservando o checklist | VAL-22 |
| DEMO-07 | Apresentar evidência de VAL-23, em cenário isolado: reserva 14h–18h encerrada às 14h30 e tentativa de novo pedido 16h30–17h | Ocupação original continua protegida mesmo com encerramento antecipado | VAL-23 |
| DEMO-08 | Cancelar administrativamente uma reserva futura afetada e consultar o aviso como solicitante | Justificativa, autoria e aviso interno preservados | VAL-17, VAL-27 |
| DEMO-09 | Apresentar resultados dos testes de expiração, ausência de check-in e regularização administrativa | Sem espera real de 24h; evidências identificam teste e commit executados | VAL-15, VAL-20, VAL-21, VAL-24, VAL-30 |

Se a apresentação acontecer fora dos horários de exemplo, redefinir o conjunto
inteiro mantendo as relações temporais, inclusive antecedentes de cada pedido.
Não apresentar uma reserva fabricada com prazo inválido como se tivesse sido
criada normalmente pela aplicação.

## 6. Preenchimentos que o grupo pode adiantar agora

- [ ] Data, duração e horário de início da apresentação.
- [ ] Ambiente de execução e responsável pelo ensaio.
- [ ] Apelidos das contas fictícias e combinação de perfis que será mostrada.
- [ ] Identificação, capacidade, funcionamento e checklist dos ambientes escolhidos.
- [ ] Quais reservas serão criadas ao vivo e quais precisam existir previamente.
- [ ] Quem apresenta cada trecho e quem realiza as ações dos outros perfis.
- [ ] Quais evidências de testes serão usadas para fluxos que exigem espera.

## 7. Critério de prontidão

O roteiro só estará pronto para executar quando houver backend e interface ou
cliente de API compatível, dados carregados, autenticação implementada e um
ensaio concluído. Guardar junto da evidência a versão/commit executado, os
apelidos usados e o resultado dos passos. Datas absolutas de um ensaio antigo
não devem ser reaproveitadas automaticamente numa apresentação posterior.

O perfil `local` já aponta para `db/dados-local`, mas essa carga ainda não existe
na base usada para este documento. Quando for implementada, deve ficar separada
das migrações estruturais e não ser carregada pelo perfil `teste`. Os testes
criam seus próprios dados, como descrito no plano de validação.
