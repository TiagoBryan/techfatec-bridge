package implementacao;

import java.util.List;

/**
 * Objeto de transferência (DTO) que trafega pela "ponte".
 *
 * A abstração (Relatorio) monta o conteúdo; a implementação (Exportador)
 * decide como esse conteúdo vira PDF, XLSX ou HTML. Nenhum dos lados
 * precisa conhecer os detalhes do outro.
 */
public record DadosRelatorio(
        String titulo,
        List<String> colunas,
        List<List<String>> linhas,
        String rodape) {

    public DadosRelatorio {
        colunas = List.copyOf(colunas);
        linhas = linhas.stream().map(List::copyOf).toList();
    }

    /** Nome de arquivo seguro, derivado do título (ex.: "relatorio_de_vendas"). */
    public String nomeArquivo() {
        String semAcento = java.text.Normalizer
                .normalize(titulo, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
    }
}
