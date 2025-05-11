<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
<h2>用户登录</h2>
<form action="LoginServlet" method="post">
    <label>用户名:</label><input type="text" name="username" required><br>
    <label>密码:</label><input type="password" name="password" required><br>
    <input type="submit" value="登录"><button id="regist" onclick="register()">没有账号？注册</button>
</form>
</body>
<script>
    function register(){
        window.location.href='register.jsp'
    }

</script>
</html>