package br.unima.reservas;

import org.springframework.boot.SpringApplication;

/**
 * Sobe a aplicação com o banco em contêiner, sem precisar do Docker Compose.
 *
 * <p>Atalho de desenvolvimento: executar esta classe levanta um PostgreSQL
 * descartável, aplica as migrações e inicia a API.
 */
public class TestReservasApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReservasApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
