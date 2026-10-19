# Reservas API

Backend do Sistema de Reservas de Salas e Laboratórios. Referência funcional:
[Regras do MVP](../docs/requisitos/REGRAS-MVP.md).

## Estado atual

A base integrada à `dev` possui configuração da aplicação, PostgreSQL 16 no
Compose/Testcontainers e CI de compilação e teste. Ainda não possui migrações
estruturais, dados de demonstração, provisionamento das contas do MVP ou
endpoints que implementem o ciclo de reservas. O teste existente verifica a
inicialização do contexto; não comprova as regras de negócio.

As propriedades de conta inicial, CORS e agendamento já existem como preparação.
Configurar uma senha de direção, sozinho, ainda não provisiona essa conta.

## Executar a base local

Pré-requisitos: Java 21, Docker em execução e Docker Compose. O Maven Wrapper
está no projeto; a primeira execução precisa baixar suas dependências.

No diretório `reservas/`:

```bash
docker compose up -d
./mvnw spring-boot:run
```

O perfil padrão é `local`. A configuração de banco usa
`jdbc:postgresql://localhost:5432/reservas` e os valores de desenvolvimento
definidos no Compose. A porta padrão da aplicação é `8080`. Isso inicia a base
existente; não há ainda um fluxo de login/reservas do MVP para demonstrar.

O Compose automático do Spring está desativado: o comando de Docker acima é
necessário para usar esse banco local. A configuração pode ser ajustada por
`RESERVAS_BANCO_URL`, `RESERVAS_BANCO_USUARIO`, `RESERVAS_BANCO_SENHA` e
`RESERVAS_PORTA`, conforme `application.yml`.

## Perfis e dados

| Perfil | Uso previsto | Situação |
| --- | --- | --- |
| `local` | Desenvolvimento com esquema e dados fictícios | Aponta para `db/migration` e `db/dados-local`; as cargas ainda serão criadas |
| `teste` | Integração com dados montados por cada teste e sem agendamento | Configurado, mas deve ser ativado pelos novos testes; não usar a carga de demonstração |

A carga local futura não deve depender de IDs fixos presumidos nem de datas
de uma apresentação antiga. O formato final de persistência aguarda a revisão
da modelagem. O [roteiro de demonstração](../docs/demonstracao/ROTEIRO-DADOS-MVP.md)
lista os dados de negócio que o grupo já pode organizar.

## Compilar e testar

O comando usado pelo CI, executado em `reservas/`, é:

```bash
./mvnw -B --no-transfer-progress verify
```

Os testes de integração existentes usam Testcontainers com PostgreSQL 16 e
precisam de Docker disponível. O
[plano de validação](../docs/testes/PLANO-VALIDACAO-MVP.md) detalha os testes de
negócio a implementar, inclusive limites de tempo, concorrência e histórico.
