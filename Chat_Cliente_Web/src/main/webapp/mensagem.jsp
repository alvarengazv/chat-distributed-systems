<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="java.net.Socket,java.net.InetSocketAddress,java.io.ObjectInputStream,java.io.IOException" %>
<%
    response.setHeader("Cache-Control", "no-store");
    String historico = "";
    boolean indisponivel = false;
    try (Socket cliente = new Socket()) {
        cliente.connect(new InetSocketAddress("127.0.0.1", 6661), 2000);
        cliente.setSoTimeout(2000);
        try (ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream())) {
            historico = entrada.readUTF();
            historico = historico.replaceAll("(?i)src=(['\"])chatimg:([a-zA-Z0-9_-]+\\.png)\\1", "src=$1images/$2$1");
        }
    } catch (IOException e) {
        indisponivel = true;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="refresh" content="5;url=mensagem.jsp?atualizacao=<%= System.currentTimeMillis() %>#fim">
    <title>Mensagens</title>
</head>
<body>
<div><%= historico %></div>
<% if (indisponivel) { %>
<p>Em manutenção...</p>
<% } %>
<div id="fim"></div>
<script>
    window.addEventListener('load', () => {
        requestAnimationFrame(() => requestAnimationFrame(() => {
            const area = document.scrollingElement || document.documentElement;
            area.scrollTop = area.scrollHeight;
        }));
    });
</script>
</body>
</html>
