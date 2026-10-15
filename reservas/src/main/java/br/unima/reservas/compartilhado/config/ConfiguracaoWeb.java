package br.unima.reservas.compartilhado.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS para o front-end.
 *
 * <p>As origens vêm de {@code reservas.cors.origens-permitidas}. Como a
 * autenticação é por sessão, {@code allowCredentials} é obrigatório, e por isso
 * não se pode usar curinga: cada origem do front-end precisa ser declarada.
 *
 * <p>O bean é {@link Primary} porque o Spring MVC também registra um
 * {@code CorsConfigurationSource} (o {@code mvcHandlerMappingIntrospector}).
 * Sem isso, quem injeta o tipo encontra dois candidatos e a aplicação não sobe.
 */
@Configuration(proxyBeanMethods = false)
public class ConfiguracaoWeb {

    @Bean
    @Primary
    public CorsConfigurationSource fonteConfiguracaoCors(PropriedadesReservas propriedades) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(propriedades.cors().origensPermitidas());
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Content-Type", "Accept", "X-XSRF-TOKEN", "X-Requested-With"));
        configuracao.setExposedHeaders(List.of("Location"));
        configuracao.setAllowCredentials(true);
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", configuracao);
        return fonte;
    }
}