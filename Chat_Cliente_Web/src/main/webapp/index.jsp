<%@ page import="com.mycompany.chat_cliente_web.InterfaceMiddlewareWeb" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <style>
        body { margin:0; background:#f5f4f3; color:#303438; font:14px Arial,sans-serif; }
        form { width:max-content; max-width:calc(100% - 32px); margin:80px auto; }
        .linha { display:flex; align-items:center; gap:10px; margin:8px 0; }
        .titulo { width:75px; text-align:right; }
        input[type=text] { width:230px; padding:4px; border:1px solid #aaa; border-radius:4px; }
        .opcao { display:inline-flex; align-items:center; gap:3px; }
        img { width:16px; height:16px; object-fit:contain; }
        button { display:block; margin:10px auto; padding:8px 16px; }
    </style>
</head>
<body>
<form name="frmLogin" action="chat.jsp" method="post">
    <div class="linha"><label class="titulo" for="txtNickname">Nickname:</label><input id="txtNickname" name="txtNickname" maxlength="20" required autofocus></div>
    <div class="linha"><span class="titulo">Cor:</span>
        <label class="opcao" style="color:blue"><input type="radio" name="radCor" value="blue" checked>Azul</label>
        <label class="opcao"><input type="radio" name="radCor" value="black">Preto</label>
        <label class="opcao" style="color:red"><input type="radio" name="radCor" value="red">Vermelho</label>
    </div>
    <div class="linha"><span class="titulo">Avatar:</span>
        <label class="opcao"><input type="radio" name="radAvatar" value="homem.png"><img src="<%= InterfaceMiddlewareWeb.imagem("homem.png") %>" alt="Homem"></label>
        <label class="opcao"><input type="radio" name="radAvatar" value="menina.png"><img src="<%= InterfaceMiddlewareWeb.imagem("menina.png") %>" alt="Menina"></label>
        <label class="opcao"><input type="radio" name="radAvatar" value="sinal-de-interrogacao.png" checked><img src="<%= InterfaceMiddlewareWeb.imagem("sinal-de-interrogacao.png") %>" alt="Sem avatar"></label>
    </div>
    <button type="submit" name="btnEntrar">Entrar</button>
</form>
</body>
</html>
