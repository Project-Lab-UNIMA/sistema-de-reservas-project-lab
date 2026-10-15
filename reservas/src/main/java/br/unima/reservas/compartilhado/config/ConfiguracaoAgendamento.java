package br.unima.reservas.compartilhado.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita as tarefas periódicas de expiração de pré-bloqueio e de marcação de
 * não comparecimento.
 *
 * <p>Fica desligada no perfil {@code teste}: os testes chamam os serviços
 * diretamente, com o relógio sob controle, em vez de esperar o agendador
 * disparar. Isso também evita que uma tarefa concorrente altere o estado de uma
 * reserva no meio de uma asserção.
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@Profile("!teste")
public class ConfiguracaoAgendamento {
}
