package com.mycompany.chat_cliente_web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Adaptador HTTP: mantém os JSPs existentes e separa o fluxo Web do socket desktop.
 * Os JSPs usam as threads de requisição gerenciadas pelo contêiner GlassFish, sem
 * criar um executor por página. Os timeouts limitam o bloqueio de I/O dessas threads;
 * a escolha de Virtual Threads para o atendimento do middleware está no serviço HTTP.
 */
public final class InterfaceMiddlewareWeb {
    private InterfaceMiddlewareWeb() { }

    public static String consultar() throws IOException {
        return requisitar(Util.URL_WEB_ENVIO + "/messages", null);
    }

    public static void enviar(String html) throws IOException {
        requisitar(Util.URL_WEB_RECEPCAO + "/messages", html);
    }

    // As imagens deixam de depender de recursos locais de cada cliente. O servidor Web
    // publica uma única pasta /figurinhas/, compartilhada com o cliente Java desktop.
    public static String imagem(String arquivo) {
        if (arquivo == null || !arquivo.matches("[a-zA-Z0-9_-]+\\.png")) arquivo = "neutralIcon.png";
        String base = Util.URL_IMAGENS;
        return base.replaceAll("/+$", "") + "/" + arquivo;
    }

    public static String imagens(String html) {
        var matcher = java.util.regex.Pattern.compile("(?i)src=(['\"])(?:chatimg:|(?:\\./)?images/)([a-zA-Z0-9_-]+\\.png)\\1").matcher(html);
        StringBuilder resultado = new StringBuilder();
        while (matcher.find()) matcher.appendReplacement(resultado,
                java.util.regex.Matcher.quoteReplacement("src=\"" + escapar(imagem(matcher.group(2))) + "\""));
        matcher.appendTail(resultado);
        return resultado.toString();
    }

    public static String escapar(String texto) {
        return texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static void registrar(String computador, String origem, Exception erro) {
        String dados = "tipo=web&computador=" + codificar(computador) + "&origem=" + codificar(origem)
                + "&mensagem=" + codificar(erro.toString());
        try {
            requisitar(Util.URL_LOGS + "/errors", dados, "application/x-www-form-urlencoded; charset=UTF-8");
        } catch (IOException falhaColetor) {
            // O coletor pode estar desligado independentemente do chat. Sem recursão:
            // a falha de telemetria é visível no stderr do contêiner, sem arquivo cliente.
            System.err.println("Falha no coletor: " + falhaColetor + "; erro original: " + erro);
        }
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor == null ? "DESCONHECIDO" : valor, StandardCharsets.UTF_8);
    }

    private static String requisitar(String endereco, String corpo) throws IOException {
        return requisitar(endereco, corpo, "text/html; charset=UTF-8");
    }

    private static String requisitar(String endereco, String corpo, String tipo) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) URI.create(endereco).toURL().openConnection();
        conexao.setConnectTimeout(2000);
        conexao.setReadTimeout(2000);
        try {
            if (corpo != null) {
                conexao.setRequestMethod("POST");
                conexao.setRequestProperty("Content-Type", tipo);
                conexao.setDoOutput(true);
                try (var saida = conexao.getOutputStream()) { saida.write(corpo.getBytes(StandardCharsets.UTF_8)); }
            }
            int status = conexao.getResponseCode();
            if (status < 200 || status >= 300) throw new IOException("Middleware respondeu HTTP " + status);
            try (var entrada = conexao.getInputStream()) { return new String(entrada.readAllBytes(), StandardCharsets.UTF_8); }
        } finally { conexao.disconnect(); }
    }
}
