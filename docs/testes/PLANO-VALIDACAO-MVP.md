# Plano de validação do MVP

Data: 19/10/2026

Situação: cenários planejados; este documento não registra testes já executados.

Base: [Regras do MVP, versão 1.1](../requisitos/REGRAS-MVP.md), especialmente os
15 critérios da seção 10. O plano usa comportamentos de negócio, sem fixar IDs,
nomes de tabelas, endpoints, códigos HTTP, estrutura de senha ou estratégia de
bloqueio que ainda dependem da revisão da Etapa 3.

O [roteiro de dados e demonstração](../demonstracao/ROTEIRO-DADOS-MVP.md) oferece
um exemplo para a apresentação. Testes automatizados devem criar seus próprios
cenários, sem depender dos dados locais de demonstração.

## 1. Preparação da implementação dos testes

- Usar PostgreSQL 16 nos testes de integração, conforme o projeto.
- Ativar o perfil `teste` nos novos testes de integração: sem dados locais e sem
  agendamento concorrente. O teste de contexto atual ainda não faz essa ativação.
- Controlar o tempo pelo `Clock` já configurado e chamar rotinas diretamente.
  Não aguardar duas horas ou quinze minutos reais para validar uma fronteira.
- Criar um ambiente ativo e solicitantes/aprovadores distintos; conservar as
  demais regras válidas quando variar apenas uma condição de teste.
- Usar transações independentes nos testes de concorrência, com coordenação
  explícita de início e espera das duas conclusões. Uma mesma transação para as
  duas chamadas não demonstra disputa real. Não usar apenas atrasos arbitrários.
- Conferir o resultado retornado e o estado persistido, incluindo ocupação,
  decisão, histórico e avisos pertinentes; falha não pode deixar efeito parcial.

Prioridades: **P0** protege autorização, ocupação, prazos e histórico; **P1**
completa fluxos de cadastro e uso. A classificação orienta a ordem de escrita,
não remove cenários do MVP.

## 2. Convenções para cenários de tempo

`D` é uma data de teste escolhida pelo teste; horários são do fuso do campus.
O cenário habitual usa criação às `08h`, funcionamento fictício das `07h` às
`22h` e utilização das `14h` às `16h`. Isso satisfaz a antecedência mínima.
Cada cenário começa do zero; sucesso de uma variação não deve alterar a seguinte.

`ε` significa a menor diferença representável que o backend e o banco decidirem
suportar juntos. A precisão concreta será preenchida depois da revisão técnica.
O limite de 30 dias também deverá usar a semântica temporal aprovada nessa revisão.
Testes em torno de meia-noite precisam de funcionamento suficiente para isolar
a regra de data, em vez de falhar somente por ambiente fechado.

## 3. Matriz de cenários

A coluna **Critério** referencia a numeração da seção 10 das Regras do MVP.
Referências a outras seções cobrem comportamentos adicionais já acordados.

| ID | Prioridade | Preparação e ação | Resultado esperado | Critério |
| --- | --- | --- | --- | --- |
| VAL-01 | P0 | Professor, monitor e coordenação enviam pedidos válidos, em variações separadas | Cada perfil pode solicitar e recebe pedido pendente | 1 |
| VAL-02 | P0 | Direção sem perfil solicitante ou usuário sem perfil tenta solicitar | Negado, sem pedido nem bloqueio criado; direção com Professor pode solicitar | 1 |
| VAL-03 | P0 | Coordenação e direção analisam pedido de outra pessoa, em ambientes diferentes do mesmo campus | Podem decidir sem depender de setor; uma decisão basta | 2 |
| VAL-04 | P0 | Pessoa solicitante também tem perfil aprovador e tenta decidir o próprio pedido | Autoaprovação negada; solicitação da coordenação é analisada por outra pessoa | 2 |
| VAL-05 | P1 | Direção cadastra usuário/ambiente e atribui perfil; professor ou coordenação tenta administrar os cadastros; direção tenta atribuir perfil a si mesma | Cadastro/atribuição permitidos apenas nos fluxos autorizados; autoatribuição negada | Seção 2 |
| VAL-06 | P0 | Dois solicitantes enviam simultaneamente pedidos com ocupações sobrepostas no mesmo ambiente | Só uma ocupação válida; a tentativa rejeitada não cria pré-bloqueio residual | 3 |
| VAL-07 | P0 | Reserva 14h–16h e outro pedido 16h15–17h, com preparação/limpeza de 15 minutos | Conflito identificado pelos buffers, apesar de a utilização não se sobrepor | 4 |
| VAL-08 | P0 | Reserva 14h–16h e outro pedido 16h30–17h30, com os mesmos buffers | Ocupações adjacentes permitidas se as demais regras forem válidas | 4 |
| VAL-09 | P0 | Variar duração: zero, negativa, exatamente 4h e 4h + ε | Só a duração positiva até 4h pode passar; não há exceção por justificativa | 5 |
| VAL-10 | P0 | Pedido 23h30–00h30 com funcionamento suficiente para todo o intervalo | Rejeitado porque a utilização atravessa a meia-noite | 5 |
| VAL-11 | P0 | Variar início: criação + 2h − ε, + 2h, + 30 dias e + 30 dias + ε | Limites exatos aceitos; valores externos rejeitados, mantendo funcionamento e duração válidos | 6 |
| VAL-12 | P0 | Utilização cabe no expediente, mas preparação ou limpeza ultrapassa o funcionamento; depois testar encaixe exato | Primeiro pedido rejeitado; encaixe exato do intervalo completo permitido | 7 |
| VAL-13 | P0 | Criar pendente; testar início antes de 24h e início depois de 24h; consultar em torno do primeiro prazo | Bloqueio vale apenas antes do menor prazo entre início e criação + 24h | 8 |
| VAL-14 | P0 | Aprovador recusa pedido sem motivo e depois com motivo | Sem motivo é negado; recusa válida libera o horário e preserva autor, motivo e instante | 15 e seção 5 |
| VAL-15 | P0 | Deixar prazo de pendente chegar, manter tarefa atrasada e tentar aprovar ou reservar novamente | Aprovação tardia negada; pedido vencido não impede nova solicitação válida; histórico é preservado | 8, 15 |
| VAL-16 | P0 | Solicitante cancela sua pendente/aprovada antes do início, sem/com motivo; repetir no início e após ele; outra pessoa sem permissão tenta cancelar | Exigir motivo e titularidade; aceitar antes do início nos estados previstos; negar a partir do início e a pessoa não autorizada | 9 |
| VAL-17 | P0 | Coordenação/direção cancela administrativamente reserva futura com justificativa | Cancelamento, autor, justificativa e aviso ao solicitante persistidos juntos | Seção 6 |
| VAL-18 | P1 | Solicitante tenta trocar ambiente, data ou horário após envio | Alteração direta negada; mudança exige cancelar no prazo e enviar novo pedido sujeito a validação/aprovação | Seção 6 |
| VAL-19 | P0 | Reserva aprovada 14h–16h; testar check-in às 14h − ε, 14h, 14h15 e 14h15 + ε, em cenários independentes | Negar antes do início, aceitar nos dois limites permitidos e negar após a tolerância; somente o solicitante pode entrar | 10 |
| VAL-20 | P0 | Reservas aprovadas 14h–14h10 e 14h–14h15; testar pouco antes, no término e após ele | Antes do término ainda pode entrar dentro da tolerância; no término e depois dele é negado, mesmo com tarefa atrasada | 10 |
| VAL-21 | P0 | Deixar janela de entrada fechar sem check-in; executar a rotina e tentar entrar depois | Registrar não comparecimento, liberar o restante do horário e negar entrada; sem penalidade automática | 10 |
| VAL-22 | P1 | Solicitante tenta encerrar com item obrigatório não confirmado e depois com todos confirmados | Primeiro encerramento negado; segundo registra encerramento e preserva as respostas | 11 |
| VAL-23 | P0 | Reserva 14h–18h em uso é encerrada às 14h30; consultar ocupação e tentar novo pedido 16h30–17h | Intervalo original e limpeza continuam protegidos; pedido sobreposto rejeitado, embora já respeite as 2h de antecedência | 11 |
| VAL-24 | P0 | Uso chega ao término sem encerramento; depois coordenação/direção regulariza sem/com justificativa | Pendência visível, sem encerramento ou extensão automática; regularização exige justificativa e autor distinguíveis do solicitante | 12 |
| VAL-25 | P0 | Após criar reserva, alterar buffers/tolerância e texto/obrigatoriedade do checklist; criar outro pedido válido | Reserva anterior conserva regras, prazos e checklist aplicados; novos pedidos usam a configuração nova | 13 e seção 8 |
| VAL-26 | P0 | Tentar inativar ambiente, reduzir capacidade ou funcionamento inviabilizando reservas futuras pendentes/aprovadas | Alteração bloqueada; após tratar as reservas afetadas com justificativa/aviso, alteração válida pode prosseguir | 14 |
| VAL-27 | P0 | Recusar, cancelar ou expirar e consultar o histórico | Pedido e eventos continuam disponíveis a pessoas autorizadas, com instante e autor humano ou Sistema | 15 |
| VAL-28 | P0 | Disputar aprovação e expiração da mesma pendente, controlando os instantes de cada tentativa | Apenas resultado compatível com prazo e ordem das operações; nunca decisão contraditória nem aprovação posterior à expiração | 8, 15 |
| VAL-29 | P0 | Disputar check-in e marcação de não comparecimento, incluindo tolerância exata e término exato | Uma situação final coerente com a janela; nunca uso e não comparecimento válidos simultaneamente | 10 |
| VAL-30 | P0 | Repetir expiração, não comparecimento e processamento do aviso de cancelamento administrativo | Repetição não duplica transição, evento ou aviso, nem altera o resultado já consolidado | 15 e seção 7.1 |
| VAL-31 | P0 | Navegador apresenta outro fuso/relógio; backend avalia o mesmo instante real no fuso do campus | Regras e prazos mantêm o resultado; apresentação respeita o fuso do campus | Seção 3 |
| VAL-32 | P0 | Provocar falha durante operação que combina decisão/cancelamento, histórico e aviso exigido | Nenhuma operação de negócio parcialmente confirmada; repetição segura da tentativa | 15 e seção 5 |

## 4. Cobertura dos 15 critérios

| Critério das Regras do MVP | Cenários |
| --- | --- |
| 1 — Perfis solicitantes | VAL-01, VAL-02 |
| 2 — Aprovação e pessoa solicitante | VAL-03, VAL-04 |
| 3 — Pedidos simultâneos | VAL-06 |
| 4 — Buffers e adjacência | VAL-07, VAL-08 |
| 5 — Duração e data única | VAL-09, VAL-10 |
| 6 — Antecedência | VAL-11 |
| 7 — Funcionamento completo | VAL-12 |
| 8 — Expiração | VAL-13, VAL-15, VAL-28 |
| 9 — Cancelamento pelo titular | VAL-16 |
| 10 — Check-in e não comparecimento | VAL-19, VAL-20, VAL-21, VAL-29 |
| 11 — Checklist e encerramento antecipado | VAL-22, VAL-23 |
| 12 — Encerramento pendente | VAL-24 |
| 13 — Regras preservadas | VAL-25 |
| 14 — Alterações do ambiente | VAL-26 |
| 15 — Histórico e repetição segura | VAL-14, VAL-15, VAL-27, VAL-28, VAL-30, VAL-32 |

## 5. Evidência e acompanhamento

Todos os cenários acima começam como **Planejado**. Ao implementá-los, associar
cada ID a um teste ou roteiro manual verificável, sem marcar aprovação apenas
porque a aplicação iniciou. Para cada execução relevante registrar:

| Informação | O que registrar |
| --- | --- |
| Referência | ID `VAL-xx`, commit testado e link do CI ou da evidência manual |
| Preparação | Perfis, ambiente, funcionamento, estado inicial e relógio usado |
| Ação | Operação e variação de fronteira ou concorrência aplicada |
| Verificação | Resultado e efeitos persistidos esperados/observados |
| Situação | Planejado, Implementado, Aprovado ou Reprovado |
| Pendência | Defeito ou decisão que impeça a verificação, quando houver |

Começar pela solicitação válida e pelas garantias P0 de autorização, sobreposição
e tempo. Em seguida completar decisões, uso, encerramento e preservação. O CI
deverá executar os testes de integração implementados; o teste `contextLoads`
existente não cobre sozinho esta matriz.

## 6. O que continua dependendo da revisão técnica

Este plano pode ser revisado e dividido entre os membros agora. A implementação
dos testes que tocam banco/API aguarda os contratos correspondentes: chaves e
tipos, precisão, semântica dos 30 dias, autenticação, formato de erro, mapeamento
de estados e mecanismo de concorrência. A obrigatoriedade e o limite de
participantes pela capacidade são propostas da Etapa 3 e não viraram critério
aprovado por aparecerem neste plano.
