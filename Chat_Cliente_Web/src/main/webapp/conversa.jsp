<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" import="java.util.*" %>
<%@ page import="java.net.Socket" %>
<%@ page import="java.io.ObjectOutputStream" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Conversa</title>
    <style>
        body { margin: 0; font: 14px Arial, sans-serif; background: #f3f2f1; color: #303438; }
        form { display: flex; align-items: center; justify-content: center; flex-wrap: wrap; gap: 8px; padding: 12px; }
        input, select, button { box-sizing: border-box; height: 34px; border: 1px solid #b9b1ac; border-radius: 5px; background: white; font: inherit; }
        input { width: 255px; max-width: 100%; padding: 0 8px; }
        select, button { padding: 0 10px; }
        .acoes { flex-basis: 100%; text-align: center; }
    </style>
</head>
<body>
<%!
    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
    private String textoHtml(String texto, boolean maiusculas) {
        String convertido = texto.replace("</3", "💔").replace("<3", "❤️")
                .replace("<ok>", "👌").replace(":-)", "😁").replace(";-)", "😉")
                .replace("-_-", "😑").replace("s2_s2", "😍");
        return escapar(maiusculas ? convertido.toUpperCase(Locale.ROOT) : convertido);
    }
%>
<%
    request.setCharacterEncoding("UTF-8");
    String texto = request.getParameter("txtMensagem");
    if (request.getParameter("btnEnviar") != null && texto != null && !texto.trim().isEmpty()) {
        String nome = (String) session.getAttribute("nickname");
        String cor = (String) session.getAttribute("cor");
        String avatar = (String) session.getAttribute("avatar");

        if (!Arrays.asList("navy", "red", "black").contains(cor)) cor = "black";
        if (!Arrays.asList("manIcon.png", "woman.png", "neutralIcon.png").contains(avatar)) avatar = "neutralIcon.png";

        String modo = request.getParameter("cbModo");
        String conteudo;
        if ("Grita".equals(modo)) {
            conteudo = " <b><u>GRITA:</u></b> <span style=\"color:red; font-size:larger\">" + textoHtml(texto, true) + "</span>";
        } else if ("Xinga".equals(modo)) {
            conteudo = " <b><u><i style=\"color:red\">Xinga:</i></u></b> <b style=\"color:red; font-size:xx-large\">" + textoHtml(texto, true) + "!!!!!</b>";
        } else {
            conteudo = " <b>Fala:</b> " + textoHtml(texto, false);
        }
        String linha = "<div><img src=\"images/" + avatar + "\" width=\"29\" height=\"32\" alt=\"\"> "
                + "<span style=\"color:" + cor + "\"><b>" + escapar(nome) + "</b></span>"
                + conteudo + "</div>";

        Socket client = new Socket("127.0.0.1", 6662);
        ObjectOutputStream writer = new ObjectOutputStream(client.getOutputStream());
        writer.flush();
        writer.writeUTF(linha);
        writer.close();
        client.close();


    }
%>
<form id="frmConversa" name="frmConversa" method="post" action="conversa.jsp">
    <label for="txtMensagem">Mensagem:</label>
    <input id="txtMensagem" type="text" name="txtMensagem" size="200" maxlength="150">
    <label for="cbModo">Modo:</label>
    <select id="cbModo" name="cbModo">
        <option>Fala</option><option>Grita</option><option>Xinga</option>
    </select>
    <label for="cbEmoji">Emoji:</label>
    <select id="cbEmoji" name="cbEmoji" aria-label="Adicionar emoji">
        <option value="">Selecione</option>
        <option>😀</option><option>😁</option><option>😂</option><option>😊</option>
        <option>😍</option><option>😘</option><option>😎</option><option>😮</option>
        <option>😢</option><option>😭</option><option>😡</option><option>👍</option>
        <option>👎</option><option>❤️</option>
    </select>
    <div class="acoes"><input type="submit" name="btnEnviar" value="Enviar"></div>
</form>
<script>
    <% if (request.getParameter("btnEnviar") != null && texto != null && !texto.trim().isEmpty()) { %>
    setTimeout(() => {
        if (parent.frames.quadroMensagens) parent.frames.quadroMensagens.location.reload();
    }, 500);
    <% } %>
    const formulario = document.forms.frmConversa;
    const txtMensagem = formulario.elements.txtMensagem;
    const cbEmoji = formulario.elements.cbEmoji;

    cbEmoji.addEventListener('change', function () {
        if (!this.value) return;

        txtMensagem.setRangeText(
            this.value,
            txtMensagem.selectionStart,
            txtMensagem.selectionEnd,
            'end'
        );

        this.value = '';
        txtMensagem.focus();
    });

    formulario.addEventListener('submit', function (event) {
        if (!txtMensagem.value.trim()) {
            event.preventDefault();
            txtMensagem.focus();
            return;
        }
        setTimeout(() => { txtMensagem.value = ''; }, 0);
    });
</script>
</body>
</html>
