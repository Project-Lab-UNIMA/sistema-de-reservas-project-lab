package br.unima.reservas.compartilhado.config;

import java.time.Clock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * O tempo entra na aplicação por um único lugar.
 *
 * <p>Nenhuma regra usa {@code Instant.now()} direto: prazos de pré-bloqueio,
 * janela de check-in e antecedência são avaliados a partir deste {@link Clock}.
 * Isso atende à exigência de que prazos não dependam do relógio do navegador
 * (RNF12) e permite que os testes fixem o tempo em vez de esperar por ele.
 *
 * <p>O bean é condicional para que os testes possam declarar um relógio
 * controlável sem precisar excluir esta configuração.
 */
@Configuration(proxyBeanMethods = false)
public class ConfiguracaoTempo {

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock relogio() {
        return Clock.systemUTC();
    }
}
