202130442921 滕凤军
Product：存储商品信息（categoryId, categoryName, price, stock, status），用于 products.jsp 和 seller_dashboard.jsp 展示。
Order：存储订单（purchaseId, userId, categoryId, purchaseDate, price, quantity），用于订单查看。
SalesTrend：存储趋势（categoryId, monthlySales, predictedSales, confidence），展示预测结果。
浏览记录存储在 user_access_log（user_id, ip_address, login_time），无单独 GoodsDetail 表。
Servlet：

LoginServlet：验证用户登录，存 user_id 至 session，生成协同过滤推荐，request.setAttribute("products", "recommendations") 转发至 products.jsp。
SellerManageServlet：查询商品（product_categories）、订单（purchase_history），计算趋势（移动平均），转发至 seller_dashboard.jsp。假设通过 status 管理上下架。
AdminManageServlet：管理销售人员（sellers），查询统计、日志（purchase_history, user_access_log, operation_log），转发至 admin_dashboard.jsp。
CartServlet、BuyServlet（假设）：处理购物车添加/移除和购买，更新 cart 或 purchase_history。
JSP：

products.jsp：显示商品、推荐，支持表单提交至 CartServlet（加购物车）、BuyServlet（购买）。
seller_dashboard.jsp：展示商品、订单、趋势。
admin_dashboard.jsp：展示统计、日志，支持销售人员管理。
数据库含 users, sellers, product_categories, purchase_history 等。
