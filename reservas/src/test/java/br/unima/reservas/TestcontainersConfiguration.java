package br.unima.reservas;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Banco dos testes.
 *
 * <p>A versão está fixada e é a mesma do {@code compose.yaml}. O esquema depende
 * de recursos específicos do PostgreSQL — restrição de exclusão com GiST sobre
 * um intervalo, a extensão {@code btree_gist}, triggers de imutabilidade — e
 * testar em uma versão diferente da usada em desenvolvimento esconderia
 * exatamente o tipo de diferença que importa aqui.
 *
 * <p>O contêiner é reaproveitado entre as classes de teste: a aplicação sobe uma
 * vez por contexto e as migrações rodam uma vez, e cada teste limpa o estado de
 * que precisa.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:16"));
    }
}
