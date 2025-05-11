package servlet;

import util.DBUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                session.setAttribute("username", username);

                // 记录登录日志
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO user_access_log (user_id, ip_address, login_time) VALUES (?, ?, NOW())")) {
                    logStmt.setInt(1, userId);
                    logStmt.setString(2, ipAddress);
                    logStmt.executeUpdate();
                }

                // 查询商品数据
                List<Product> products = new ArrayList<>();
                try (PreparedStatement productStmt = conn.prepareStatement("SELECT * FROM product_categories")) {
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
                }

                // 生成推荐列表
                List<String> recommendations = generateRecommendations(conn, userId);

                // 设置属性并转发到 products.jsp
                request.setAttribute("products", products);
                request.setAttribute("recommendations", recommendations);
                request.getRequestDispatcher("products.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp?error=用户名或密码错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=登录失败");
        }
    }

    // 基于用户的协同过滤生成推荐
    private List<String> generateRecommendations(Connection conn, int targetUserId) throws SQLException {
        // 1. 构建用户-商品矩阵
        Map<Integer, Map<Integer, Integer>> userItemMatrix = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id, category_id, SUM(quantity) as total_quantity " +
                        "FROM purchase_history GROUP BY user_id, category_id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int categoryId = rs.getInt("category_id");
                int quantity = rs.getInt("total_quantity");
                userItemMatrix.computeIfAbsent(userId, k -> new HashMap<>()).put(categoryId, quantity);
            }
        }

        // 2. 计算用户相似度（余弦相似度）
        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Integer> targetVector = userItemMatrix.getOrDefault(targetUserId, new HashMap<>());
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : userItemMatrix.entrySet()) {
            int otherUserId = entry.getKey();
            if (otherUserId == targetUserId) continue;
            Map<Integer, Integer> otherVector = entry.getValue();
            double similarity = calculateCosineSimilarity(targetVector, otherVector);
            similarities.put(otherUserId, similarity);
        }

        // 3. 选择 top-K 相似用户（K=5）
        List<Integer> topSimilarUsers = similarities.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 4. 生成推荐：相似用户购买但目标用户未购买的类别
        List<Integer> recommendedCategoryIds = new ArrayList<>();
        for (int similarUserId : topSimilarUsers) {
            Map<Integer, Integer> similarUserVector = userItemMatrix.getOrDefault(similarUserId, new HashMap<>());
            for (int categoryId : similarUserVector.keySet()) {
                if (!targetVector.containsKey(categoryId) && !recommendedCategoryIds.contains(categoryId)) {
                    recommendedCategoryIds.add(categoryId);
                }
            }
        }

        // 限制推荐数量（最多 5 个）
        if (recommendedCategoryIds.size() > 5) {
            recommendedCategoryIds = recommendedCategoryIds.subList(0, 5);
        }

        // 5. 转换为类别名称
        List<String> recommendations = new ArrayList<>();
        if (!recommendedCategoryIds.isEmpty()) {
            String placeholders = String.join(",", recommendedCategoryIds.stream().map(id -> "?").collect(Collectors.toList()));
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT category_id, category_name FROM product_categories WHERE category_id IN (" + placeholders + ")")) {
                for (int i = 0; i < recommendedCategoryIds.size(); i++) {
                    stmt.setInt(i + 1, recommendedCategoryIds.get(i));
                }
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recommendations.add(rs.getString("category_name"));
                }
            }
        }

        // 如果无推荐，提供默认热门类别
        if (recommendations.isEmpty()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT pc.category_name " +
                            "FROM purchase_history ph JOIN product_categories pc ON ph.category_id = pc.category_id " +
                            "GROUP BY pc.category_id, pc.category_name ORDER BY SUM(ph.quantity) DESC LIMIT 3")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recommendations.add(rs.getString("category_name"));
                }
            }
        }

        return recommendations;
    }

    // 计算余弦相似度
    private double calculateCosineSimilarity(Map<Integer, Integer> vector1, Map<Integer, Integer> vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int categoryId : vector1.keySet()) {
            int quantity1 = vector1.getOrDefault(categoryId, 0);
            int quantity2 = vector2.getOrDefault(categoryId, 0);
            dotProduct += quantity1 * quantity2;
            norm1 += quantity1 * quantity1;
            norm2 += quantity2 * quantity2;
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dotProduct / (norm1 * norm2);
    }
}

