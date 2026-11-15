package cliente;

import abstracao.Relatorio;
import abstracao.RelatorioDesempenhoRH;
import abstracao.RelatorioVendas;
import implementacao.Exportador;
import implementacao.ExportadorExcel;
import implementacao.ExportadorHTML;
import implementacao.ExportadorPDF;

import java.nio.file.Path;

/**
 * CLIENTE / Script de validação.
 *
 * É o único ponto do sistema que conhece as classes concretas: ele cria os
 * exportadores e os INJETA nos relatórios pelo construtor (composition root).
 */
public class Main {

    public static void main(String[] args) {
        Path saida = Path.of("saida");

        Exportador pdf = new ExportadorPDF(saida);
        Exportador excel = new ExportadorExcel(saida);
        Exportador html = new ExportadorHTML(saida);

        System.out.println("==============================================");
        System.out.println(" TechFatec BI - Módulo de Relatórios (Bridge)");
        System.out.println("==============================================");

        // 1. Relatório de Vendas em PDF (dependência injetada via construtor)
        System.out.println("\n[1] Relatório de Vendas em PDF");
        Relatorio vendas = new RelatorioVendas(pdf);
        executar(vendas);

        // 2. MESMO objeto de vendas, formato trocado em tempo de execução
        System.out.println("\n[2] Mesmo relatório de vendas, trocado para Excel em tempo de execução");
        System.out.println("  -> Objeto antes da troca:  RelatorioVendas@" + Integer.toHexString(System.identityHashCode(vendas)));
        vendas.setExportador(excel);
        System.out.println("  -> Objeto após a troca:    RelatorioVendas@" + Integer.toHexString(System.identityHashCode(vendas)));
        executar(vendas);

        // 3. Relatório de RH em HTML
        System.out.println("\n[3] Relatório de Desempenho de RH em HTML");
        Relatorio rh = new RelatorioDesempenhoRH(html);
        executar(rh);

        System.out.println("\n==============================================");
        System.out.println(" 3 rotinas executadas com sucesso.");
        System.out.println(" Herança pura: 2 relatórios x 3 formatos = 6 subclasses.");
        System.out.println(" Com Bridge:   2 relatórios + 3 formatos = 5 classes.");
        System.out.println("==============================================");
    }

    private static void executar(Relatorio relatorio) {
        Path arquivo = relatorio.gerar();
        System.out.printf("  OK: %s gerado em %s%n",
                relatorio.getExportador().getFormato(), arquivo);
    }
}
