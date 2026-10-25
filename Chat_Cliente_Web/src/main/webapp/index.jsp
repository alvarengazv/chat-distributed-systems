<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>
<br><br><br>
<center>
    <form name="frmLogin" action="chat.jsp" method ="get">
        <table border ="1">
            <tr>
                <td colspan="2" bgcolor="black" align="center">
                    <font color="white" size="3"> chat distribuído 1.0 </font>
                </td>
            </tr>
            <tr>
                <td>
                    <b>Nickname</b>
                </td>
                <td>
                    <input type="text" name ="txtNickname" maxlength="20" size="100">
                </td>
            </tr>
            <tr>
                <td align="right">
                    <b>Cor</b>
                </td>
                <td align="center">
                    <input type="radio" name="radCor" value="navy" checked>
                    <font color="navy"><b>Azul</b></font>
                    <input type="radio" name="radCor" value="red" >
                    <font color="red"><b>Vermelho</b></font>
                    <input type="radio" name="radCor" value="black" >
                    <font color="black"><b>Preto</b></font>
                </td>

            </tr>
            <tr>
                <td align="right">
                    <b>Avatar</b>
                </td>
                <td align='center'>
                    <input type="radio" name="radAvatar" value="manIcon.png" checked>
                    <img src="images/manIcon.png" width='30' height="30">

                    <input type="radio" name="radAvatar" value="woman.png" >
                    <img src="images/woman.png" width='30' height="30">

                    <input type="radio" name="radAvatar" value="neutralIcon.png" >
                    <img src="images/neutralIcon.png" width='30' height="30">
                </td>
            </tr>
            <tr >
                <td colspan='2' align='center'>
                    <input type="submit" name="btnEntrar" value="entrar">
                </td>

            </tr>
        </table>
    </form>
</center>
</body>
</html>