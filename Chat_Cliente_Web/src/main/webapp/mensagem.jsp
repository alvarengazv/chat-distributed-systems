<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="com.mycompany.chat_cliente_web.InterfaceMiddlewareWeb,java.io.IOException" %>
<%
    response.setHeader("Cache-Control", "no-store");
    String historico = "";
    boolean indisponivel = false;
    try {
        historico = InterfaceMiddlewareWeb.imagens(InterfaceMiddlewareWeb.consultar());
    } catch (IOException erro) {
        indisponivel = true;
        InterfaceMiddlewareWeb.registrar(request.getRemoteHost(), "mensagem:consultar", erro);
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
