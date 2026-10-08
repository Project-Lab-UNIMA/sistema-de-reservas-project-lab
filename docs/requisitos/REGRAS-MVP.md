# Regras do MVP — Sistema de Reservas de Salas e Laboratórios

Data: 08/10/2026

Versão: 1.1 — Etapa 1 consolidada; check-in limitado também pelo término da reserva.

Situação: proposta de trabalho acordada nesta conversa, para revisão com o grupo. Não representa homologação institucional nem substitui automaticamente o documento avaliado na primeira unidade.

Documento de referência: “Documento de Requisitos — Sistema de Reservas de Salas e Laboratórios.pdf”, utilizado na avaliação da primeira unidade. Este complemento registra o recorte e as decisões para orientar a modelagem e a implementação.

Para a proposta de MVP, as decisões explícitas deste complemento prevalecem sobre os pontos divergentes do documento original. A referência original permanece preservada; funcionalidades nela descritas não entram automaticamente no recorte desta entrega.

## 1. Objetivo e recorte

Permitir que professores, membros da coordenação e monitores encontrem um ambiente disponível, solicitem sua reserva, acompanhem a decisão e registrem a utilização e o encerramento.

O fluxo principal será:

**Consultar disponibilidade → solicitar → aguardar aprovação → obter aprovação → fazer check-in → encerrar.**

O MVP atenderá salas e laboratórios que não exigem habilitação especial. Laboratórios restritos não serão disponibilizados para reserva nessa versão.

Cada solicitação corresponde a um ambiente e a um intervalo em uma única data. Reservas recorrentes, reservas em cadeia, empréstimo de equipamentos, equipes de suporte, penalidades automáticas e recursos administrativos ficam para versões posteriores.

O núcleo inclui cadastro de usuários e ambientes, horários de funcionamento, disponibilidade, solicitação, aprovação ou recusa, cancelamento, expiração, check-in, registro de não comparecimento, checklist de encerramento e histórico das ações.

## 2. Pessoas e permissões

| Perfil | Solicitar reserva | Aprovar ou recusar | Cancelar reservas futuras de forma administrativa | Regularizar encerramento pendente | Administrar usuários e ambientes |
|---|---|---|---|---|---|
| Professor | Sim | Não | Não | Não | Não |
| Monitor | Sim | Não | Não | Não | Não |
| Coordenação | Sim | Sim | Sim | Sim | Não |
| Direção | Apenas se também tiver perfil solicitante | Sim | Sim | Sim | Sim |

Todos os solicitantes podem consultar disponibilidade, acompanhar suas reservas, realizar seu próprio check-in e encerramento e cancelar suas reservas nas condições da seção 6.

Regras de autorização:

- Uma pessoa pode acumular perfis.
- Nenhuma pessoa pode aprovar a própria solicitação, mesmo que acumule perfis de professor, coordenação ou direção.
- Solicitação feita por alguém da coordenação deve ser decidida por outra pessoa autorizada.
- Qualquer membro da coordenação ou direção pode analisar pedidos de qualquer ambiente do campus. Não haverá divisão de aprovação por setor ou unidade no MVP.
- Basta uma decisão de um aprovador autorizado. Não há aprovação em duas etapas.
- A direção cadastra os usuários e atribui os perfis; ninguém pode se declarar monitor pelo fluxo de solicitação.
- A direção cadastra e configura os ambientes e seus horários de funcionamento.
- A regra do documento original que impede a atribuição de perfil a si mesmo deve ser preservada. A criação da primeira conta da direção é uma providência técnica de implantação, fora do fluxo de autocadastro.

## 3. Ambientes, funcionamento e duração

Cada ambiente possui identificação, localização, capacidade e situação ativo/inativo, além de grade semanal de funcionamento configurável.

Os horários reais serão preenchidos pela direção. Não há valores de abertura e fechamento presumidos neste documento.

Uma solicitação precisa atender às seguintes condições:

- O ambiente está ativo e pertence ao recorte de ambientes permitido no MVP.
- O início é anterior ao término.
- O início está entre 2 horas e 30 dias à frente do momento da solicitação.
- A duração de utilização é de até 4 horas.
- A utilização não atravessa a meia-noite.
- O intervalo completo de ocupação, incluindo preparação e limpeza, cabe no funcionamento do ambiente.
- Não existe sobreposição com outra ocupação válida do mesmo ambiente.

Pedidos acima de 4 horas são recusados na validação. Não haverá exceção por justificativa ou aprovação superior no MVP.

Datas e horários devem seguir o RNF12 do documento original: instantes armazenados no banco e apresentação no fuso do campus. Prazos não devem depender do relógio do navegador.

## 4. Ocupação da agenda e pré-bloqueio

### 4.1 Preparação e limpeza

Os valores iniciais acordados são 15 minutos de preparação antes do início e 15 minutos de limpeza depois do término.

Esses intervalos ocupam a agenda, mas não fazem parte do limite de 4 horas de utilização.

Exemplo:

- Utilização solicitada: 14h às 16h.
- Ocupação da agenda: 13h45 às 16h15.
- Próximo início de utilização possível, considerando também os 15 minutos de preparação da próxima reserva: 16h30.

A comparação deve considerar os buffers das duas reservas. Como convenção técnica para implementar a regra, intervalos que apenas se encostam no limite não se sobrepõem: uma ocupação pode terminar exatamente quando a seguinte começa.

### 4.2 Pedido pendente

O envio válido cria a reserva pendente de aprovação e segura temporariamente seu intervalo completo de ocupação.

Enquanto o pré-bloqueio estiver válido, outro pedido sobreposto para o mesmo ambiente não poderá ser gravado. A consulta anterior de disponibilidade não garante o horário: é necessário verificar novamente ao salvar.

O prazo do pré-bloqueio será o primeiro destes dois instantes:

1. 24 horas corridas após a solicitação;
2. início previsto da utilização.

Se nenhuma decisão ocorrer antes desse prazo, a reserva passa para **Expirada**, libera o horário e permanece no histórico. Para tentar reservar novamente, o interessado deve criar outra solicitação.

Exemplo: pedido enviado às 10h para utilização às 16h do mesmo dia precisa ser aprovado antes das 16h.

## 5. Aprovação e recusa

- A aprovação é realizada em uma única etapa pela coordenação ou direção.
- O sistema verifica a autorização, o impedimento de autoaprovação, o estado da reserva, a validade do prazo e os conflitos antes de confirmar a decisão.
- A aprovação transforma a ocupação provisória em confirmada.
- A recusa exige motivo, libera o horário e preserva a solicitação no histórico.
- A decisão registra quem decidiu e quando.
- Não haverá devolução para ajuste no MVP.
- Aprovação, expiração e outras ações concorrentes não podem produzir decisões contraditórias sobre o mesmo pedido.

## 6. Cancelamento e alteração de pedidos

O solicitante pode cancelar a própria reserva se ela estiver **Pendente de aprovação** ou **Aprovada**, e somente antes do início previsto da utilização.

O cancelamento exige motivo, libera imediatamente o horário e preserva o histórico.

Coordenação e direção podem cancelar administrativamente reservas futuras, registrando justificativa e avisando o solicitante. Essa possibilidade permite tratar reservas afetadas antes de alterar um ambiente.

Após o envio, o solicitante não pode editar ambiente, data ou horários. Para mudar esses dados, deve cancelar dentro do prazo permitido e enviar uma nova solicitação. O novo pedido passa novamente pela validação de disponibilidade e pela aprovação.

Este recorte não define um procedimento de cancelamento administrativo de utilização já iniciada. Isso não deve ser confundido com regularização de encerramento pendente, que está prevista na seção 7.

## 7. Check-in e encerramento

### 7.1 Entrada e não comparecimento

- O próprio solicitante realiza o check-in por botão.
- O check-in exige reserva aprovada.
- A janela abre no início previsto e inclui o instante exato de 15 minutos depois, desde que a utilização ainda não tenha terminado.
- O check-in deve ocorrer antes do término previsto. No instante do término ou depois dele, não é permitido, mesmo que a tolerância de 15 minutos ainda não tenha acabado.
- Assim, as duas condições precisam ser atendidas: `início <= agora < fim` e `agora <= início + tolerância aplicada`.
- Exemplo: para uma reserva das 14h às 16h, o limite é 14h15, inclusive. Para uma reserva das 14h às 14h10, o check-in precisa ocorrer antes das 14h10.
- Check-in antes do início ou depois da tolerância não é permitido.
- O check-in muda a reserva para **Em uso** e registra o instante efetivo da entrada.
- Sem check-in dentro da janela, o sistema marca **Não compareceu**, libera o restante do horário e mantém o registro no histórico.
- Não haverá penalidade automática por não comparecimento no MVP.

A marcação automática de não comparecimento ocorre quando a janela estiver fechada e não houver check-in válido: a tolerância foi ultrapassada ou o término previsto já chegou. A marcação e o check-in devem ser coordenados para que a mesma reserva não termine simultaneamente como “Em uso” e “Não compareceu”. A automação deve suportar repetição e reinício sem duplicar efeitos, conforme RNF11. Atraso na execução da tarefa não aumenta a janela de check-in.

### 7.2 Encerramento normal

O solicitante encerra o uso preenchendo os itens obrigatórios do checklist do ambiente, inicialmente sem fotos ou anexos.

Organização do espaço, equipamentos desligados e materiais recolhidos são exemplos de itens. A lista real deverá ser definida para os ambientes da demonstração.

Com todos os obrigatórios preenchidos, o sistema registra o encerramento e muda a reserva para **Encerrada**. A presença de itens obrigatórios pendentes impede o encerramento normal.

### 7.3 Encerramento antecipado ou pendente

- O encerramento antecipado mantém na agenda o intervalo originalmente reservado, incluindo a limpeza.
- Se o término previsto chegar sem encerramento, surge uma pendência visível para coordenação e direção.
- Essa pendência não encerra automaticamente a reserva nem estende automaticamente o intervalo agendado.
- Coordenação ou direção poderá regularizar o encerramento com justificativa registrada.
- O histórico deve distinguir o encerramento feito pelo solicitante da regularização administrativa.

A forma de representar essa pendência no banco será definida na modelagem. Não é necessário tratá-la antecipadamente como um novo estado independente da reserva.

## 8. Mudanças de configuração e preservação do histórico

Cada reserva guardará os valores das regras aplicadas no momento da solicitação, como buffers e tolerância de check-in. Mudanças desses valores valerão para novos pedidos.

Os prazos já determinados para uma reserva também precisam continuar rastreáveis. Uma alteração posterior de configuração não pode silenciosamente recalcular sua ocupação ou seu prazo.

Antes de inativar um ambiente ou reduzir sua capacidade ou funcionamento, o sistema verificará as reservas futuras pendentes e aprovadas afetadas. Se houver alguma, impedirá a alteração.

Coordenação ou direção poderá cancelar essas reservas com justificativa e aviso ao solicitante. Depois de tratar os impedimentos, a direção poderá alterar o cadastro.

O histórico preservará solicitação, decisões, cancelamentos, expiração, entrada, não comparecimento e encerramento, com autor ou identificação do sistema e data-hora. Registros necessários ao histórico não serão apagados para liberar a agenda.

## 9. Estados e transições

Os nomes abaixo são rótulos de negócio. Os identificadores usados no banco e no código serão definidos na modelagem.

| Estado atual | Ação ou evento | Responsável | Próximo estado | Efeito sobre a agenda |
|---|---|---|---|---|
| Pedido ainda não enviado | Enviar solicitação válida | Solicitante | Pendente de aprovação | Cria ocupação provisória com expiração |
| Pendente de aprovação | Aprovar antes da expiração | Coordenação ou direção, sem autoaprovação | Aprovada | Confirma a ocupação |
| Pendente de aprovação | Recusar com motivo | Coordenação ou direção | Recusada | Libera o horário |
| Pendente de aprovação | Prazo expirar | Sistema | Expirada | Libera o horário |
| Pendente de aprovação ou Aprovada | Cancelar antes do início | Solicitante; ou coordenação/direção em cancelamento administrativo de reserva futura | Cancelada | Libera o horário |
| Aprovada | Fazer check-in dentro da janela | Solicitante | Em uso | Mantém a ocupação programada |
| Aprovada | Janela de check-in fechar sem entrada registrada | Sistema | Não compareceu | Libera o restante do horário |
| Em uso | Concluir checklist e encerrar | Solicitante | Encerrada | Mantém o intervalo originalmente reservado, incluindo limpeza |
| Em uso com encerramento pendente | Regularizar com justificativa | Coordenação ou direção | Encerrada | Não cria extensão automática |

Não há retorno de uma reserva recusada, cancelada, expirada ou marcada como não comparecimento para aprovação. O interessado deve enviar outro pedido.

## 10. Critérios de validação do núcleo

Estes cenários devem orientar os testes de integração e a demonstração:

1. Professor, monitor e coordenação conseguem solicitar; direção sem perfil solicitante e usuário sem perfil autorizado não conseguem.
2. Coordenação e direção conseguem decidir pedidos de qualquer ambiente do campus, mas nunca os próprios.
3. Dois pedidos simultâneos e sobrepostos para o mesmo ambiente resultam em apenas uma ocupação válida.
4. Um conflito provocado somente pelos buffers impede a reserva; intervalos completos adjacentes são permitidos.
5. Reserva de até 4 horas é aceita quando atende às demais regras; duração maior ou utilização atravessando a meia-noite é rejeitada.
6. A antecedência de 2 horas e de 30 dias é aceita nos respectivos limites; solicitações fora da janela são rejeitadas.
7. O intervalo completo, incluindo preparação e limpeza, é rejeitado se ultrapassar o funcionamento do ambiente.
8. A expiração ocorre no menor prazo entre 24 horas e o início previsto; uma decisão tardia não reativa o pedido.
9. Cancelamento pelo solicitante é permitido antes do início, nos estados previstos; é rejeitado a partir do início.
10. Check-in dentro da janela registra o uso uma única vez. O instante exato de início mais 15 minutos é aceito somente se ainda for anterior ao término previsto. No término ou depois dele, ou após a tolerância, o check-in é rejeitado, mesmo se a tarefa automática ainda não executou. Validar também reservas de 10 e de 15 minutos. O não comparecimento não gera penalidade automática.
11. Checklist obrigatório incompleto impede encerramento normal; encerramento antecipado não antecipa a disponibilidade.
12. Uso não encerrado gera pendência, sem extensão automática; a regularização administrativa registra autor e justificativa.
13. Mudanças de buffers e tolerância não alteram retroativamente reservas existentes.
14. Uma alteração de ambiente que inviabilize reservas futuras pendentes ou aprovadas é bloqueada até o tratamento dessas reservas.
15. Cancelar, recusar ou expirar não apaga o histórico. Repetir uma tarefa automática não duplica seus efeitos.

## 11. Relação com o documento original e próximos passos

### Ajustes explícitos do recorte

| Tema | Decisão para o MVP |
|---|---|
| Solicitantes | Professores, coordenação e monitores; alunos sem perfil de monitor não solicitam |
| Aprovação | Coordenação ou direção, em etapa única e com alcance sobre todo o campus |
| Administração | Direção assume os cadastros de usuários, perfis, ambientes e funcionamento |
| Ambientes restritos | Indisponíveis para reserva no MVP |
| Duração excedida | Rejeitada; sem justificativa para aprovação superior |
| Expiração de pré-bloqueio | Estado Expirada; nova tentativa exige nova solicitação |
| Devolução para ajuste | Adiada; mudança de ambiente/data/horário exige novo pedido |
| Cancelamento pendente | Permitido ao titular antes do início |
| Check-in | Botão; QR code não faz parte da primeira implementação |
| Checklist | Itens obrigatórios, inicialmente sem fotos ou anexos |
| Não comparecimento | Registro e liberação do horário, sem penalidade automática |

As garantias de concorrência, autorização, privacidade, auditoria e tratamento de datas do documento original continuam sendo referência. A revisão com o grupo deve atualizar os requisitos e as telas afetados por este recorte.

### Detalhes para a modelagem e implantação

O núcleo funcional está definido para iniciar o DER. Ainda será necessário detalhar, sem presumir novas funcionalidades:

- Os campos, tipos, relacionamentos, restrições e identificadores técnicos dos estados.
- A representação de valores de política preservados por reserva e dos itens de checklist, incluindo como preservar respostas antigas se um modelo mudar.
- O preenchimento dos horários reais, usuários e checklists da demonstração.
- A configuração inicial da primeira conta da direção e das contas locais de teste previstas como alternativa no documento original.
- O canal do aviso ao solicitante, usando como referência as notificações internas do RF03; envio por e-mail não foi decidido nesta conversa.
- A implementação transacional da coordenação entre check-in e tarefa automática, respeitando a tolerância inclusiva e o término exclusivo definidos na seção 7.1.

Entrega seguinte: DER e dicionário de dados do recorte acima, revisados com quem implementará o backend antes das primeiras migrações.
