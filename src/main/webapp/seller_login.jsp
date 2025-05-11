<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>销售人员登录</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2 { color: #333; }
        form { margin-top: 10px; }
        input { margin: 5px; padding: 5px; }
    </style>
</head>
<body>
<h2>销售人员登录</h2>
<form action="SellerLoginServlet" method="post">
    <label>用户名:</label><input type="text" name="sellerName" required><br>
    <label>密码:</label><input type="password" name="password" required><br>
    <input type="submit" value="登录">
</form>
<c:if test="${param.error != null}">
    <p style="color: red;">${param.error}</p>
</c:if>
</body>
</html>