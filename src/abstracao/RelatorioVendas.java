package abstracao;

import implementacao.DadosRelatorio;
import implementacao.Exportador;

import java.util.List;

/** ABSTRAÇÃO REFINADA: o Relatório de Vendas que já existia no sistema legado. */
public class RelatorioVendas extends Relatorio {

    public RelatorioVendas(Exportador exportador) {
        super(exportador);
    }

    @Override
    protected DadosRelatorio montarDados() {
        return new DadosRelatorio(
                "Relatório de Vendas",
                List.of("Mês", "Região", "Produto", "Total (R$)"),
                List.of(
                        List.of("Setembro", "Sudeste", "Licença BI Pro", "R$ 48.500,00"),
                        List.of("Setembro", "Sul", "Licença BI Basic", "R$ 21.300,00"),
                        List.of("Outubro", "Sudeste", "Consultoria", "R$ 35.750,00"),
                        List.of("Outubro", "Nordeste", "Licença BI Pro", "R$ 29.900,00"),
                        List.of("Novembro", "Centro-Oeste", "Treinamento", "R$ 12.400,00")),
                "TechFatec BI - Total do trimestre: R$ 147.850,00");
    }
}
