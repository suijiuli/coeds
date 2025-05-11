package servlet;

import util.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/SellerManageServlet")
public class SellerManageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer sellerId = (Integer) request.getSession().getAttribute("seller_id");
        if (sellerId == null) {
            response.sendRedirect("seller_login.jsp?error=请先登录");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 查询销售人员管理的商品
            List<Product> products = new ArrayList<>();
            PreparedStatement productStmt = conn.prepareStatement(
                    "SELECT category_id, category_name, price, stock, status " +
                            "FROM product_categories WHERE seller_id = ?");
            productStmt.setInt(1, sellerId);
            ResultSet productRs = productStmt.executeQuery();
            while (productRs.next()) {
                Product product = new Product();
                product.setCategoryId(productRs.getInt("category_id"));
                product.setCategoryName(productRs.getString("category_name"));
                product.setPrice(productRs.getDouble("price"));
                product.setStock(productRs.getInt("stock"));
                product.setStatus(productRs.getString("status"));
                products.add(product);
            }

            // 查询销售订单
            List<Order> orders = new ArrayList<>();
            PreparedStatement orderStmt = conn.prepareStatement(
                    "SELECT ph.purchase_id, ph.user_id, u.username, ph.category_id, pc.category_name, " +
                            "ph.purchase_date, ph.price, ph.quantity " +
                            "FROM purchase_history ph " +
                            "JOIN users u ON ph.user_id = u.user_id " +
                            "JOIN product_categories pc ON ph.category_id = pc.category_id " +
                            "WHERE pc.seller_id = ?");
            orderStmt.setInt(1, sellerId);
            ResultSet orderRs = orderStmt.executeQuery();
            while (orderRs.next()) {
                Order order = new Order();
                order.setPurchaseId(orderRs.getInt("purchase_id"));
                order.setUserId(orderRs.getInt("user_id"));
                order.setUsername(orderRs.getString("username"));
                order.setCategoryId(orderRs.getInt("category_id"));
                order.setCategoryName(orderRs.getString("category_name"));
                order.setPurchaseDate(orderRs.getTimestamp("purchase_date"));
                order.setPrice(orderRs.getDouble("price"));
                order.setQuantity(orderRs.getInt("quantity"));
                orders.add(order);
            }

            // 生成销售趋势预测
            List<SalesTrend> salesTrends = generateSalesTrends(conn, sellerId);

            request.setAttribute("products", products);
            request.setAttribute("orders", orders);
            request.setAttribute("salesTrends", salesTrends);
            request.getRequestDispatcher("seller_dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "数据库查询失败：" + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // 生成销售趋势预测
    private List<SalesTrend> generateSalesTrends(Connection conn, int sellerId) throws SQLException {
        List<SalesTrend> trends = new ArrayList<>();
        String sql = "SELECT pc.category_id, pc.category_name, " +
                "SUM(ph.quantity) as total_quantity, " +
                "MONTH(ph.purchase_date) as purchase_month " +
                "FROM purchase_history ph " +
                "JOIN product_categories pc ON ph.category_id = pc.category_id " +
                "WHERE pc.seller_id = ? AND ph.purchase_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " +
                "GROUP BY pc.category_id, pc.category_name, purchase_month " +
                "ORDER BY pc.category_id, purchase_month";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();
            Map<Integer, SalesTrend> categoryMap = new HashMap<>();
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                SalesTrend trend = categoryMap.computeIfAbsent(categoryId, k -> new SalesTrend());
                trend.setCategoryId(categoryId);
                trend.setCategoryName(rs.getString("category_name"));
                trend.getMonthlySales().put(rs.getInt("purchase_month"), rs.getDouble("total_quantity"));
            }

            // 计算预测和置信度
            for (SalesTrend trend : categoryMap.values()) {
                List<Double> sales = new ArrayList<>(trend.getMonthlySales().values());
                if (sales.isEmpty()) {
                    trend.setPredictedSales(0);
                    trend.setConfidence("低");
                    continue;
                }

                // 简单移动平均预测
                double avgSales = sales.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                trend.setPredictedSales(Math.round(avgSales));

                // 置信度：基于标准差
                double mean = avgSales;
                double variance = sales.stream().mapToDouble(s -> Math.pow(s - mean, 2)).average().orElse(0);
                double stdDev = Math.sqrt(variance);
                if (stdDev < mean * 0.3) {
                    trend.setConfidence("高");
                } else if (stdDev < mean * 0.6) {
                    trend.setConfidence("中");
                } else {
                    trend.setConfidence("低");
                }
            }

            return new ArrayList<>(categoryMap.values());
        }
    }
}



