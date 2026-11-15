package implementacao;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * IMPLEMENTOR CONCRETO: gera uma planilha Excel (.xlsx).
 *
 * Um arquivo XLSX é um pacote ZIP com arquivos XML (padrão Office Open XML).
 * Ele é montado aqui apenas com a biblioteca padrão do Java, sem dependências.
 */
public class ExportadorExcel implements Exportador {

    private final Path diretorioSaida;

    public ExportadorExcel(Path diretorioSaida) {
        this.diretorioSaida = diretorioSaida;
    }

    @Override
    public String getFormato() {
        return "Excel (XLSX)";
    }

    @Override
    public Path exportar(DadosRelatorio dados) {
        Path arquivo = diretorioSaida.resolve(dados.nomeArquivo() + ".xlsx");
        try {
            Files.createDirectories(diretorioSaida);
            try (OutputStream out = Files.newOutputStream(arquivo);
                 ZipOutputStream zip = new ZipOutputStream(out)) {
                entrada(zip, "[Content_Types].xml", CONTENT_TYPES);
                entrada(zip, "_rels/.rels", RELS);
                entrada(zip, "xl/workbook.xml", WORKBOOK);
                entrada(zip, "xl/_rels/workbook.xml.rels", WORKBOOK_RELS);
                entrada(zip, "xl/styles.xml", STYLES);
                entrada(zip, "xl/worksheets/sheet1.xml", planilha(dados));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return arquivo;
    }

    private String planilha(DadosRelatorio dados) {
        StringBuilder xml = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<cols><col min=\"1\" max=\"" + dados.colunas().size() + "\" width=\"24\" customWidth=\"1\"/></cols>"
                + "<sheetData>");
        int r = 1;
        xml.append(linha(r++, List.of(dados.titulo()), 1));
        r++; // linha em branco
        xml.append(linha(r++, dados.colunas(), 1));
        for (List<String> l : dados.linhas()) {
            xml.append(linha(r++, l, 0));
        }
        r++;
        xml.append(linha(r, List.of(dados.rodape()), 0));
        return xml.append("</sheetData></worksheet>").toString();
    }

    private String linha(int numero, List<String> valores, int estilo) {
        StringBuilder sb = new StringBuilder("<row r=\"" + numero + "\">");
        for (int c = 0; c < valores.size(); c++) {
            String ref = (char) ('A' + c) + String.valueOf(numero);
            String v = valores.get(c);
            String numerico = v.replace("R$", "").replace(".", "").replace(",", ".").trim();
            if (estilo == 0 && numerico.matches("-?\\d+(\\.\\d+)?")) {
                sb.append("<c r=\"").append(ref).append("\"><v>").append(numerico).append("</v></c>");
            } else {
                sb.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"")
                  .append(estilo == 1 ? " s=\"1\"" : "")
                  .append("><is><t>").append(esc(v)).append("</t></is></c>");
            }
        }
        return sb.append("</row>").toString();
    }

    private static void entrada(ZipOutputStream zip, String nome, String conteudo) throws IOException {
        zip.putNextEntry(new ZipEntry(nome));
        zip.write(conteudo.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String CONTENT_TYPES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
            + "</Types>";

    private static final String RELS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
            + "</Relationships>";

    private static final String WORKBOOK =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
            + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
            + "<sheets><sheet name=\"Relatorio\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>";

    private static final String WORKBOOK_RELS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
            + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
            + "</Relationships>";

    /** Estilo 0 = normal; estilo 1 = negrito (título e cabeçalho). */
    private static final String STYLES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
            + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
            + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
            + "<fills count=\"2\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill></fills>"
            + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
            + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
            + "<cellXfs count=\"2\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
            + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/></cellXfs>"
            + "</styleSheet>";
}
