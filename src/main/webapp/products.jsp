<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>商品列表</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2, h3 { color: #333; }
        table { border-collapse: collapse; width: 100%; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        ul { list-style-type: none; padding: 0; }
        li { margin: 5px 0; }
        a { text-decoration: none; color: #0066cc; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<h2>欢迎, ${username}</h2>

<h3>推荐商品类别</h3>
<c:if test="${empty recommendations}">
    <p>暂无推荐</p>
</c:if>
<c:if test="${not empty recommendations}">
    <ul>
        <c:forEach var="recommendation" items="${recommendations}">
            <li>${recommendation}</li>
        </c:forEach>
    </ul>
</c:if>

<h3>商品列表</h3>
<table>
    <tr>
        <th>商品类别ID</th>
        <th>类别名称</th>
        <th>价格</th>
        <th>库存</th>
        <th>状态</th>
    </tr>
    <c:if test="${empty products}">
        <tr><td colspan="5">暂无商品</td></tr>
    </c:if>
    <c:forEach var="product" items="${products}">
        <tr>
            <td>${product.categoryId}</td>
            <td>${product.categoryName}</td>
            <td>${product.price}</td>
            <td>${product.stock}</td>
            <td>${product.status}</td>
        </tr>
    </c:forEach>
</table>

<p><a href="LogoutServlet">注销</a></p>
</body>
</html>