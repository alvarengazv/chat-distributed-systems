<%--
  Created by IntelliJ IDEA.
  User: alvarengazv
  Date: 15/10/2026
  Time: 09:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sala de Conversa</title>
</head>
<body>
<%
    session.setAttribute("nickname", request.getParameter("txtNickname"));
    session.setAttribute("cor", request.getParameter("radCor"));
    session.setAttribute("avatar", request.getParameter("radAvatar"));
%>
    <%  // Scriptlet
        /*out.print("<b>Nickname:</b>" + request.getParameter("txtNickname") + "<br>" +
                    "<b>Cor:</b>" + request.getParameter("radCor") + "<br>" +
                    "<b>Avatar:</b>" + request.getParameter("radAvatar"));

        out.print("<br><br><b>Nickname em sessão:</b>" + session.getAttribute("nickname") + "<br>" +
                "<b>Cor em sessão:</b>" + session.getAttribute("cor") + "<br>" +
                "<b>Avatar em sessão:</b>" + session.getAttribute("avatar"));*/
    %>
    <iframe src="topo.jsp" width="100%" height="20%"></iframe>
    <iframe src="mensagem.jsp" width="100%" height="60%"></iframe>
    <iframe src="conversa.jsp" width="100%" height="20%"></iframe>
</body>
</html>
