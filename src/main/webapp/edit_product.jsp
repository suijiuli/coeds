<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>修改商品类别</title>
</head>
<body>
<h2>修改商品类别</h2>
<form action="SellerManageServlet" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="categoryId" value="${product.categoryId}">
    <label>类别名称:</label><input type="text" name="categoryName" value="${product.categoryName}" required><br>
    <label>价格:</label><input type="number" step="0.01" name="price" value="${product.price}" required><br>
    <label>库存:</label><input type="number" name="stock" value="${product.stock}" required><br>
    <label>状态:</label>
    <select name="status">
        <option value="active" ${product.status == 'active' ? 'selected' : ''}>上架</option>
        <option value="inactive" ${product.status == 'inactive' ? 'selected' : ''}>下架</option>
    </select><br>
    <input type="submit" value="更新">
</form>
</body>
</html>