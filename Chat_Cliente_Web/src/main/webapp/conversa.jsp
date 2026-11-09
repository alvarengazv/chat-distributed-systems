<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" import="java.util.*" %>
<%@ page import="com.mycompany.chat_cliente_web.InterfaceMiddlewareWeb" %>
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
        .acoes { text-align: center; }
        input[type=text] { flex: 1; min-width: 120px; }
        details { position: relative; }
        details[open] { flex-basis: 100%; }
        details button { padding:4px; }
        #figurinhas { margin-top:8px; }
        [role=status]:empty { display:none; }
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
    boolean enviada = false;
    String aviso = "";
    if (request.getParameter("btnErro") != null) {
        try { throw new IllegalStateException("Erro Web provocado para demonstração"); }
        catch (Exception erro) { InterfaceMiddlewareWeb.registrar(request.getRemoteHost(), "conversa:demonstrarErro", erro); aviso = "Erro de demonstração encaminhado ao coletor."; }
    }
    String figura = request.getParameter("figura");
    boolean temFigura = Arrays.asList("coracao", "beijo", "dinheiro", "homem", "menina", "sinal-de-interrogacao").contains(figura);
    if (request.getParameter("btnEnviar") != null && ((texto != null && !texto.trim().isEmpty()) || temFigura)) {
        if (texto == null) texto = "";
        String nome = (String) session.getAttribute("nickname");
        String cor = (String) session.getAttribute("cor");
        String avatar = (String) session.getAttribute("avatar");

        if (!Arrays.asList("blue", "navy", "red", "black").contains(cor)) cor = "black";
        if (!Arrays.asList("homem.png", "menina.png", "sinal-de-interrogacao.png", "manIcon.png", "woman.png", "neutralIcon.png").contains(avatar)) avatar = "neutralIcon.png";

        String modo = request.getParameter("cbModo");
        String conteudo;
        if ("Grita".equals(modo)) {
            conteudo = " <b><u>GRITA:</u></b> <span style=\"color:red; font-size:larger\">" + textoHtml(texto, true) + "</span>";
        } else if ("Xinga".equals(modo)) {
            conteudo = " <b><u><i style=\"color:red\">Xinga:</i></u></b> <b style=\"color:red; font-size:xx-large\">" + textoHtml(texto, true) + "!!!!!</b>";
        } else {
            conteudo = " <b>Fala:</b> " + textoHtml(texto, false);
        }
        if (temFigura) {
            int tamanhoFigura = "Xinga".equals(modo) ? 28 : "Grita".equals(modo) ? 24 : 16;
            conteudo += " <img src=\"" + escapar(InterfaceMiddlewareWeb.imagem(figura + ".png"))
                    + "\" width=\"" + tamanhoFigura + "\" height=\"" + tamanhoFigura + "\" alt=\"\">";
        }
        String linha = "<div><img src=\"" + escapar(InterfaceMiddlewareWeb.imagem(avatar)) + "\" width=\"20\" height=\"20\" alt=\"\"> "
                + "<span style=\"color:" + cor + "\"><b>" + escapar(nome) + "</b></span>"
                + conteudo + "</div>";

        try {
            InterfaceMiddlewareWeb.enviar(linha);
            enviada = true;
        } catch (java.io.IOException erro) {
            InterfaceMiddlewareWeb.registrar(request.getRemoteHost(), "conversa:enviar", erro);
            aviso = "Não foi possível enviar. O serviço Web pode estar em manutenção.";
        }


    }
%>
<p role="status"><%= escapar(aviso) %></p>
<form id="frmConversa" name="frmConversa" method="post" action="conversa.jsp">
    <label for="txtMensagem">Mensagem:</label>
    <input id="txtMensagem" type="text" name="txtMensagem" size="200" maxlength="150" value="<%= enviada ? "" : escapar(texto) %>">
    <label for="cbModo">Modo:</label>
    <select id="cbModo" name="cbModo">
        <option>Fala</option><option>Grita</option><option>Xinga</option>
    </select>
    <input type="hidden" name="figura" value="">
    <details><summary>☺ Emojis / Figurinhas</summary>
        <div aria-label="Emojis">
        <% for (String emoji : new String[]{"😀", "😁", "😂", "😊", "😍", "😘", "😎", "😮", "😢", "😭", "😡", "👍", "👎", "❤️"}) { %>
            <button type="button" data-emoji="<%= emoji %>"><%= emoji %></button>
        <% } %>
        </div>
        <div id="figurinhas">
        <% for (String nomeFigura : new String[]{"coracao", "beijo", "dinheiro", "homem", "menina", "sinal-de-interrogacao"}) { %>
            <button type="button" data-figura="<%= nomeFigura %>" title="<%= nomeFigura %>"><img src="<%= InterfaceMiddlewareWeb.imagem(nomeFigura + ".png") %>" width="24" height="24" alt="<%= nomeFigura %>"></button>
        <% } %>
        </div>
    </details>
    <div class="acoes"><input type="submit" name="btnEnviar" value="Enviar">
        <button type="submit" name="btnErro" value="1">Demonstrar erro</button></div>
</form>
<script>
    <% if (enviada) { %>
    setTimeout(() => {
        if (parent.frames.quadroMensagens) parent.frames.quadroMensagens.location.reload();
    }, 500);
    <% } %>
    const formulario = document.forms.frmConversa;
    const txtMensagem = formulario.elements.txtMensagem;
    document.querySelectorAll('[data-emoji]').forEach(botao => {
        botao.addEventListener('click', () => {
            txtMensagem.setRangeText(botao.dataset.emoji, txtMensagem.selectionStart, txtMensagem.selectionEnd, 'end');
            txtMensagem.focus();
        });
    });

    document.querySelectorAll('[data-figura]').forEach(botao => {
        botao.addEventListener('click', () => {
            formulario.elements.figura.value = botao.dataset.figura;
            formulario.requestSubmit(formulario.elements.btnEnviar);
        });
    });
    formulario.addEventListener('submit', function (event) {
        if (event.submitter && event.submitter.name === 'btnErro') return;
        if (!txtMensagem.value.trim() && !formulario.elements.figura.value) {
            event.preventDefault();
            txtMensagem.focus();
            return;
        }

    });
</script>
</body>
</html>
