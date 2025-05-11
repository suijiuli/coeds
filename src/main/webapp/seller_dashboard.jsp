<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
  <title>销售人员仪表板</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h2, h3 { color: #333; }
    table { border-collapse: collapse; width: 100%; margin-top: 10px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    a { text-decoration: none; color: #0066cc; }
    a:hover { text-decoration: underline; }
  </style>
</head>
<body>
<h2>销售人员仪表板</h2>

<h3>我的商品</h3>
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

<h3>销售订单</h3>
<table>
  <tr>
    <th>购买ID</th>
    <th>用户名</th>
    <th>商品类别</th>
    <th>购买时间</th>
    <th>价格</th>
    <th>数量</th>
  </tr>
  <c:if test="${empty orders}">
    <tr><td colspan="6">暂无订单</td></tr>
  </c:if>
  <c:forEach var="order" items="${orders}">
    <tr>
      <td>${order.purchaseId}</td>
      <td>${order.username}</td>
      <td>${order.categoryName}</td>
      <td>${order.purchaseDate}</td>
      <td>${order.price}</td>
      <td>${order.quantity}</td>
    </tr>
  </c:forEach>
</table>

<h3>商品销售趋势预测</h3>
<table>
  <tr>
    <th>商品类别ID</th>
    <th>商品类别</th>
    <th>过去3个月销量</th>
    <th>预测下月销量</th>
    <th>置信度</th>
  </tr>
  <c:if test="${empty salesTrends}">
    <tr><td colspan="5">暂无趋势数据</td></tr>
  </c:if>
  <c:forEach var="trend" items="${salesTrends}">
    <tr>
      <td>${trend.categoryId}</td>
      <td>${trend.categoryName}</td>
      <td>${trend.formattedMonthlySales}</td>
      <td>${trend.predictedSales}</td>
      <td>${trend.confidence}</td>
    </tr>
  </c:forEach>
</table>

<p><a href="LogoutServlet">注销</a></p>
</body>
</html>