package abstracao;

import implementacao.DadosRelatorio;
import implementacao.Exportador;

import java.util.List;

/** ABSTRAÇÃO REFINADA: o novo Relatório de Desempenho de RH. */
public class RelatorioDesempenhoRH extends Relatorio {

    public RelatorioDesempenhoRH(Exportador exportador) {
        super(exportador);
    }

    @Override
    protected DadosRelatorio montarDados() {
        return new DadosRelatorio(
                "Relatório de Desempenho de RH",
                List.of("Colaborador", "Departamento", "Avaliação", "Metas atingidas"),
                List.of(
                        List.of("Ana Souza", "Engenharia", "9,2", "95%"),
                        List.of("Bruno Lima", "Comercial", "8,5", "88%"),
                        List.of("Carla Mendes", "Financeiro", "8,9", "91%"),
                        List.of("Diego Rocha", "Suporte", "7,8", "80%"),
                        List.of("Elisa Prado", "RH", "9,0", "93%")),
                "TechFatec BI - Média geral de avaliação: 8,7");
    }
}
