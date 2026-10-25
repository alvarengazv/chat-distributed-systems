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
    <h1>TOPO</h1>
    <%  // Scriptlet
//        out.print("<b>Nickname:</b>" + session.getAttribute("nickname") + "<br>" +
//                "<b>Cor:</b>" + session.getAttribute("cor") + "<br>" +
//                "<b>Avatar:</b>" + session.getAttribute("avatar"));
    %>
<%--    <img src="images/neutralIcon.png" width="58" height="64">--%>
    <%
//        out.print("<img src=\"images/" + session.getAttribute("avatar") + "\"  width=\"58\" height=\"64\">")
    %>
    <img alt="profilePic" src="images/<%=session.getAttribute("avatar")%>" width="58" height="64">
    <span style='font-size: 144%; color: <%=session.getAttribute("cor")%>; '><b><%=session.getAttribute("nickname")%></b></span>

</body>
</html>
