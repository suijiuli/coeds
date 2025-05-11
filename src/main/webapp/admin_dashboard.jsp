<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>管理员仪表板</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2, h3 { color: #333; }
        table { border-collapse: collapse; width: 100%; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        form { margin-bottom: 20px; }
        input, select { margin: 5px; padding: 5px; }
        a { text-decoration: none; color: #0066cc; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<h2>管理员仪表板</h2>

<h3>销售人员管理</h3>
<form action="AdminManageServlet" method="post">
    <input type="hidden" name="action" value="add">
    <label>销售人员名称:</label><input type="text" name="sellerName" required><br>
    <label>密码:</label><input type="password" name="password" required><br>
    <input type="submit" value="添加销售人员">
</form>

<h3>销售人员列表</h3>
<table>
    <tr>
        <th>销售人员ID</th>
        <th>名称</th>
        <th>操作</th>
    </tr>
    <c:if test="${empty sellers}">
        <tr><td colspan="3">暂无销售人员</td></tr>
    </c:if>
    <c:forEach var="seller" items="${sellers}">
        <tr>
            <td>${seller.sellerId}</td>
            <td>${seller.sellerName}</td>
            <td>
                <a href="AdminManageServlet?action=resetPassword&sellerId=${seller.sellerId}">重置密码</a>
                <a href="AdminManageServlet?action=delete&sellerId=${seller.sellerId}">删除</a>
            </td>
        </tr>
    </c:forEach>
</table>

<h3>销售业绩统计</h3>
<table>
    <tr>
        <th>商品类别</th>
        <th>总销量</th>
        <th>总收入</th>
    </tr>
    <c:if test="${empty reports}">
        <tr><td colspan="3">暂无销售记录</td></tr>
    </c:if>
    <c:forEach var="report" items="${reports}">
        <tr>
            <td>${report.categoryName}</td>
            <td>${report.totalQuantity}</td>
            <td>${report.totalRevenue}</td>
        </tr>
    </c:forEach>
</table>

<h3>用户购买记录</h3>
<table>
    <tr>
        <th>购买ID</th>
        <th>用户名</th>
        <th>商品类别</th>
        <th>购买时间</th>
        <th>价格</th>
        <th>数量</th>
    </tr>
    <c:if test="${empty purchaseRecords}">
        <tr><td colspan="6">暂无购买记录</td></tr>
    </c:if>
    <c:forEach var="record" items="${purchaseRecords}">
        <tr>
            <td>${record.purchaseId}</td>
            <td>${record.username}</td>
            <td>${record.categoryName}</td>
            <td>${record.purchaseDate}</td>
            <td>${record.price}</td>
            <td>${record.quantity}</td>
        </tr>
    </c:forEach>
</table>

<h3>用户访问日志</h3>
<table>
    <tr>
        <th>日志ID</th>
        <th>用户名</th>
        <th>IP地址</th>
        <th>登录时间</th>
        <th>登出时间</th>
    </tr>
    <c:if test="${empty userAccessLogs}">
        <tr><td colspan="5">暂无访问日志</td></tr>
    </c:if>
    <c:forEach var="accessLog" items="${userAccessLogs}">
        <tr>
            <td>${accessLog.logId}</td>
            <td>${accessLog.username}</td>
            <td>${accessLog.ipAddress}</td>
            <td>${accessLog.loginTime}</td>
            <td>${accessLog.logoutTime}</td>
        </tr>
    </c:forEach>
</table>

<h3>操作日志</h3>
<table>
    <tr>
        <th>日志ID</th>
        <th>操作者</th>
        <th>账户类型</th>
        <th>操作时间</th>
        <th>操作内容</th>
        <th>IP地址</th>
    </tr>
    <c:if test="${empty logs}">
        <tr><td colspan="6">暂无操作日志</td></tr>
    </c:if>
    <c:forEach var="log" items="${logs}">
        <tr>
            <td>${log.logId}</td>
            <td>${log.operatorName}</td>
            <td>${log.accountType}</td>
            <td>${log.operationTime}</td>
            <td>${log.operationContent}</td>
            <td>${ipAddress}</td>
        </tr>
    </c:forEach>
</table>

<p><a href="LogoutServlet">注销</a></p>
</body>
</html>