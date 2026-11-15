package abstracao;

import implementacao.DadosRelatorio;
import implementacao.Exportador;

import java.nio.file.Path;
import java.util.Objects;

/**
 * ABSTRAÇÃO do padrão Bridge.
 *
 * Mantém uma referência para o Implementor (Exportador) — essa referência
 * É a "ponte". O relatório sabe O QUE gerar; o exportador sabe COMO gravar.
 *
 * Regra de projeto: esta classe e suas subclasses NUNCA usam "new" em um
 * exportador concreto. O exportador chega pronto pelo construtor
 * (injeção de dependência) e pode ser trocado em tempo de execução.
 */
public abstract class Relatorio {

    protected Exportador exportador;

    protected Relatorio(Exportador exportador) {
        this.exportador = Objects.requireNonNull(exportador);
    }

    /** Troca o formato em tempo de execução, sem recriar o relatório. */
    public void setExportador(Exportador exportador) {
        this.exportador = Objects.requireNonNull(exportador);
    }

    public Exportador getExportador() {
        return exportador;
    }

    /**
     * Fluxo comum a todo relatório: monta os dados e delega a gravação
     * ao exportador atual, atravessando a ponte.
     */
    public final Path gerar() {
        DadosRelatorio dados = montarDados();
        System.out.printf("  -> %s monta os dados e delega para %s%n",
                getClass().getSimpleName(), exportador.getClass().getSimpleName());
        return exportador.exportar(dados);
    }

    /** Cada relatório concreto define seu próprio conteúdo. */
    protected abstract DadosRelatorio montarDados();
}
