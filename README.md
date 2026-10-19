# Sistema de Reservas de Salas e Laboratórios

Projeto do Project Lab — UNIMA - Afya.

Permite que professores, coordenação e monitores encontrem um ambiente
disponível, solicitem sua reserva, acompanhem a decisão e registrem a utilização
e o encerramento.

## Onde está o quê

| Caminho | Conteúdo |
| --- | --- |
| [`docs/requisitos/REGRAS-MVP.md`](docs/requisitos/REGRAS-MVP.md) | O recorte e as regras acordadas (Etapa 1) |
| [`docs/modelagem/MODELO-CONCEITUAL-MVP.md`](docs/modelagem/MODELO-CONCEITUAL-MVP.md) | DER e decisões de modelagem (Etapa 2) |
| [`docs/modelagem/MODELO-LOGICO-MVP.md`](docs/modelagem/MODELO-LOGICO-MVP.md) | Tabelas, integridade e decisões para revisão (Etapa 3) |
| [`docs/modelagem/DICIONARIO-DADOS-MVP.md`](docs/modelagem/DICIONARIO-DADOS-MVP.md) | Campos, tipos propostos, chaves e domínios (Etapa 3) |
| [`docs/api/CONTRATO-API.md`](docs/api/CONTRATO-API.md) | Contrato da API, para integrar o front-end |
| [`docs/backend/ARQUITETURA.md`](docs/backend/ARQUITETURA.md) | Decisões de implementação do back-end |
| [`docs/backend/PONTOS-ABERTOS.md`](docs/backend/PONTOS-ABERTOS.md) | O que ainda depende de decisão do grupo |
| [`reservas/`](reservas/) | O back-end (Spring Boot 4.1, Java 21, PostgreSQL 16) |

Comece pelo [README do back-end](reservas/README.md) para rodar o projeto.

## Começando rápido

```bash
cd reservas
docker compose up -d        # PostgreSQL 16
./mvnw spring-boot:run      # API em http://localhost:8080
```

A API sobe com dados de demonstração e contas de teste — a lista está no README
do back-end.

## Fluxo de trabalho no Git

Três branches de longa duração:

- `main` — o que está pronto para apresentar. Protegida.
- `homolog` — validação antes de `main`. Protegida.
- `dev` — integração do trabalho da equipe. Protegida.

Todo trabalho nasce de uma branch temporária a partir de `dev`:

```
feature/<assunto>    funcionalidade nova
fix/<assunto>        correção
docs/<assunto>       documentação
chore/<assunto>      configuração, build, infraestrutura
```

E volta para `dev` por pull request, com a CI verde.

As mensagens de commit seguem [Conventional Commits](https://www.conventionalcommits.org/pt-br/):
`tipo(escopo): descrição no imperativo`. Os tipos ficam em inglês (`feat`, `fix`,
`chore`, `docs`, `test`, `refactor`, `build`, `ci`); **o resto da mensagem é em
português**, como todo o projeto.

```
feat(reserva): valida antecedencia, duracao e funcionamento na solicitacao
```

## Equipe

Gabriel Calheiros Ferro Gomes · Domenico Lopes · José Lucas da Silva Cardoso ·
Luis Gustavo Ferro Gomes
