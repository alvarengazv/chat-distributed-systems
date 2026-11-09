<%@ page import="com.mycompany.chat_cliente_web.InterfaceMiddlewareWeb" %>
<%--
  Created by IntelliJ IDEA.
  User: alvarengazv
  Date: 15/10/2026
  Time: 09:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Topo</title>
</head>
<body>
    <%  // Scriptlet
//        out.print("<b>Nickname:</b>" + session.getAttribute("nickname") + "<br>" +
//                "<b>Cor:</b>" + session.getAttribute("cor") + "<br>" +
//                "<b>Avatar:</b>" + session.getAttribute("avatar"));
    %>
<%--    <img src="<%= InterfaceMiddlewareWeb.escapar(InterfaceMiddlewareWeb.imagem("neutralIcon.png")) %>" width="20" height="20">--%>
    <%
//        out.print("<img src=\"images/" + session.getAttribute("avatar") + "\"  width=\"58\" height=\"64\">")
    %>
    <img alt="Avatar" src="<%= InterfaceMiddlewareWeb.escapar(InterfaceMiddlewareWeb.imagem((String) session.getAttribute("avatar"))) %>" width="20" height="20">
    <span style='font-size: 144%; color: <%=session.getAttribute("cor")%>; '><b><%= InterfaceMiddlewareWeb.escapar((String) session.getAttribute("nickname")) %></b></span>

</body>
</html>
