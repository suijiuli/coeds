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
import java.util.List;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> products = new ArrayList<>();
        Integer userId = (Integer) request.getSession().getAttribute("user_id");
        String ipAddress = request.getRemoteAddr();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product_categories")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setStatus(rs.getString("status"));
                products.add(product);
            }

            // 记录访问日志
            if (userId != null) {
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO user_access_log (user_id, ip_address, login_time, category_id, duration_seconds) VALUES (?, ?, NOW(), ?, 0)")) {
                    logStmt.setInt(1, userId);
                    logStmt.setString(2, ipAddress);
                    logStmt.setInt(3, products.get(0).getCategoryId()); // 记录第一个商品类别
                    logStmt.executeUpdate();
                }
            }

            request.setAttribute("products", products);
            request.getRequestDispatcher("products.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}

