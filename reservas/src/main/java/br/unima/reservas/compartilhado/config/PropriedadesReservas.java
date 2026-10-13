package br.unima.reservas.compartilhado.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração da aplicação sob o prefixo {@code reservas}.
 *
 * <p>O fuso horário não aparece aqui de propósito: a apresentação e a avaliação
 * de horários usam sempre o fuso do campus do ambiente, e não um padrão global
 * (RNF12). Um padrão de aplicação só abriria espaço para divergência silenciosa.
 *
 * @param contaInicial provisionamento da primeira conta da direção, que é uma
 *        providência técnica de implantação e fica fora do autocadastro
 * @param cors origens do front-end autorizadas a chamar a API com credenciais
 */
@ConfigurationProperties(prefix = "reservas")
public record PropriedadesReservas(
        ContaInicial contaInicial,
        Cors cors) {

    public PropriedadesReservas {
        if (contaInicial == null) {
            contaInicial = new ContaInicial(false, null, null, null, null);
        }
        if (cors == null) {
            cors = new Cors(List.of());
        }
    }

    public record ContaInicial(
            boolean habilitada,
            String nome,
            String identificacaoInstitucional,
            String identificacaoAcesso,
            String senha) {
    }

    public record Cors(List<String> origensPermitidas) {

        public Cors {
            origensPermitidas = origensPermitidas == null ? List.of() : List.copyOf(origensPermitidas);
        }
    }
}
