package implementacao;

import java.nio.file.Path;

/**
 * IMPLEMENTOR do padrão Bridge.
 *
 * Define o contrato que todo formato de exportação deve cumprir.
 * A hierarquia de relatórios (abstração) conhece apenas esta interface,
 * nunca as classes concretas (PDF, Excel, HTML).
 *
 * Para criar um novo formato (ex.: CSV, JSON) basta criar uma nova classe
 * que implemente esta interface — nenhuma classe existente é alterada
 * (Princípio Aberto/Fechado).
 */
public interface Exportador {

    /** Nome legível do formato (ex.: "PDF"). */
    String getFormato();

    /**
     * Converte os dados neutros do relatório no formato específico
     * e grava o arquivo em disco.
     *
     * @param dados conteúdo do relatório, independente de formato
     * @return caminho do arquivo gerado
     */
    Path exportar(DadosRelatorio dados);
}
