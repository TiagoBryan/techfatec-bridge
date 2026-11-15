package implementacao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** IMPLEMENTOR CONCRETO: gera uma página HTML com tabela. */
public class ExportadorHTML implements Exportador {

    private final Path diretorioSaida;

    public ExportadorHTML(Path diretorioSaida) {
        this.diretorioSaida = diretorioSaida;
    }

    @Override
    public String getFormato() {
        return "HTML";
    }

    @Override
    public Path exportar(DadosRelatorio dados) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"pt-BR\">\n<head>\n<meta charset=\"UTF-8\">\n")
            .append("<title>").append(esc(dados.titulo())).append("</title>\n")
            .append("<style>body{font-family:Segoe UI,Arial,sans-serif;margin:40px;color:#222}")
            .append("h1{color:#b20000}table{border-collapse:collapse;width:100%}")
            .append("th{background:#b20000;color:#fff;text-align:left}")
            .append("th,td{border:1px solid #ccc;padding:8px 12px}tr:nth-child(even){background:#f6f6f6}")
            .append("footer{margin-top:24px;color:#666;font-size:.9em}</style>\n</head>\n<body>\n")
            .append("<h1>").append(esc(dados.titulo())).append("</h1>\n<table>\n<tr>");
        for (String c : dados.colunas()) {
            html.append("<th>").append(esc(c)).append("</th>");
        }
        html.append("</tr>\n");
        for (List<String> linha : dados.linhas()) {
            html.append("<tr>");
            for (String v : linha) {
                html.append("<td>").append(esc(v)).append("</td>");
            }
            html.append("</tr>\n");
        }
        html.append("</table>\n<footer>").append(esc(dados.rodape())).append("</footer>\n</body>\n</html>\n");

        Path arquivo = diretorioSaida.resolve(dados.nomeArquivo() + ".html");
        try {
            Files.createDirectories(diretorioSaida);
            Files.writeString(arquivo, html, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return arquivo;
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
