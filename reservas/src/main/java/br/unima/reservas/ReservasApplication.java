package br.unima.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Sistema de Reservas de Salas e Laboratórios — UNIMA - Afya.
 *
 * <p>Monólito modular. Cada módulo tem as camadas {@code dominio},
 * {@code repositorio}, {@code servico} e {@code api}; {@code compartilhado}
 * guarda o que atravessa todos eles — tempo, erros, segurança, paginação.
 *
 * <p>Os módulos: {@code estrutura} (campus e prédios), {@code ambiente}
 * (ambientes, funcionamento, checklist), {@code pessoas} (usuários e perfis),
 * {@code reserva} (o ciclo completo do pedido), {@code auditoria},
 * {@code notificacao} e {@code tarefas} (rotinas periódicas).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ReservasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasApplication.class, args);
    }
}
