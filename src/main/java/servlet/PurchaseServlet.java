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

@WebServlet("/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        Integer userId = (Integer) request.getSession().getAttribute("user_id");

        try (Connection conn = DBUtil.getConnection()) {
            // 检查库存
            PreparedStatement stmt = conn.prepareStatement("SELECT price, stock FROM product_categories WHERE category_id = ?");
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("stock") > 0) {
                double price = rs.getDouble("price");

                // 插入购买记录
                PreparedStatement purchaseStmt = conn.prepareStatement(
                        "INSERT INTO purchase_history (user_id, category_id, price, quantity) VALUES (?, ?, ?, 1)");
                purchaseStmt.setInt(1, userId);
                purchaseStmt.setInt(2, categoryId);
                purchaseStmt.setDouble(3, price);
                purchaseStmt.executeUpdate();

                // 更新库存
                PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE product_categories SET stock = stock - 1 WHERE category_id = ?");
                updateStmt.setInt(1, categoryId);
                updateStmt.executeUpdate();

                response.sendRedirect("ProductServlet");
            } else {
                response.sendRedirect("products.jsp?error=库存不足");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}