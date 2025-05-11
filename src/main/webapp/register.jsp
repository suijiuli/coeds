<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户注册</title>
</head>
<body>
<form action="RegisterServlet" method="post">
    <label>用户名:</label><input type="text" name="username" required><br>
    <label>密码:</label><input type="password" name="password" required><br>
    <label>邮箱:</label><input type="email" name="email" required><br>
    <input type="submit" value="注册">
</form><button id="login" onclick="login()">已有账号？登录</button>
<script>
    function login(){
        window.location.href='login.jsp'
    }

</script>
</body>
</html>