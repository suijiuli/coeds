<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>错误</title>
</head>
<body>
<h2>发生错误</h2>
<p>${param.error != null ? param.error : '未知错误'}</p>
<a href="login.jsp">返回首页</a>
</body>
</html>