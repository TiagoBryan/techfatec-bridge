package implementacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTOR CONCRETO: gera um documento PDF (versão 1.4).
 *
 * O PDF é escrito "à mão" com a biblioteca padrão do Java, usando a fonte
 * Helvetica embutida em todo leitor de PDF — o projeto não tem dependências.
 */
public class ExportadorPDF implements Exportador {

    private final Path diretorioSaida;

    public ExportadorPDF(Path diretorioSaida) {
        this.diretorioSaida = diretorioSaida;
    }

    @Override
    public String getFormato() {
        return "PDF";
    }

    @Override
    public Path exportar(DadosRelatorio dados) {
        Path arquivo = diretorioSaida.resolve(dados.nomeArquivo() + ".pdf");
        try {
            Files.createDirectories(diretorioSaida);
            Files.write(arquivo, montarPdf(dados));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return arquivo;
    }

    private byte[] montarPdf(DadosRelatorio dados) {
        int colunas = dados.colunas().size();
        float larguraCol = 495f / colunas;
        StringBuilder s = new StringBuilder();

        // Título
        s.append("0.70 0 0 rg BT /F2 18 Tf 50 780 Td (").append(esc(dados.titulo())).append(") Tj ET\n");
        // Faixa do cabeçalho
        s.append("0.70 0 0 rg 50 732 495 22 re f\n");
        s.append("1 1 1 rg BT /F2 11 Tf\n");
        for (int c = 0; c < colunas; c++) {
            s.append("1 0 0 1 ").append(fmt(56 + c * larguraCol)).append(" 739 Tm (")
             .append(esc(dados.colunas().get(c))).append(") Tj\n");
        }
        s.append("ET\n");
        // Linhas
        float y = 712;
        int i = 0;
        for (List<String> linha : dados.linhas()) {
            if (i++ % 2 == 1) {
                s.append("0.95 0.95 0.95 rg 50 ").append(fmt(y - 6)).append(" 495 20 re f\n");
            }
            s.append("0.13 0.13 0.13 rg BT /F1 10 Tf\n");
            for (int c = 0; c < linha.size(); c++) {
                s.append("1 0 0 1 ").append(fmt(56 + c * larguraCol)).append(' ').append(fmt(y)).append(" Tm (")
                 .append(esc(linha.get(c))).append(") Tj\n");
            }
            s.append("ET\n");
            y -= 20;
        }
        // Rodapé
        s.append("0.4 0.4 0.4 rg BT /F1 9 Tf 50 ").append(fmt(y - 20)).append(" Td (")
         .append(esc(dados.rodape())).append(") Tj ET\n");

        byte[] conteudo = s.toString().getBytes(StandardCharsets.ISO_8859_1);

        List<String> objetos = new ArrayList<>();
        objetos.add("<< /Type /Catalog /Pages 2 0 R >>");
        objetos.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        objetos.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                + "/Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> /Contents 6 0 R >>");
        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        escrever(pdf, "%PDF-1.4\n");
        for (int n = 0; n < objetos.size(); n++) {
            offsets.add(pdf.size());
            escrever(pdf, (n + 1) + " 0 obj\n" + objetos.get(n) + "\nendobj\n");
        }
        offsets.add(pdf.size());
        escrever(pdf, "6 0 obj\n<< /Length " + conteudo.length + " >>\nstream\n");
        pdf.writeBytes(conteudo);
        escrever(pdf, "\nendstream\nendobj\n");

        int xref = pdf.size();
        StringBuilder tabela = new StringBuilder("xref\n0 " + (offsets.size() + 1) + "\n0000000000 65535 f \n");
        for (int off : offsets) {
            tabela.append(String.format("%010d 00000 n \n", off));
        }
        tabela.append("trailer\n<< /Size ").append(offsets.size() + 1).append(" /Root 1 0 R >>\n")
              .append("startxref\n").append(xref).append("\n%%EOF\n");
        escrever(pdf, tabela.toString());
        return pdf.toByteArray();
    }

    private static void escrever(ByteArrayOutputStream out, String texto) {
        out.writeBytes(texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static String fmt(float v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }
}
