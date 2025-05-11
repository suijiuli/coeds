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
import java.sql.SQLException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String ipAddress = request.getRemoteAddr();

        try (Connection conn = DBUtil.getConnection()) {
            // 处理用户注销
            Integer userId = (Integer) session.getAttribute("user_id");
            if (userId != null) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE user_access_log SET logout_time = NOW() WHERE user_id = ? AND logout_time IS NULL")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
            }

            // 处理销售人员注销
            Integer sellerId = (Integer) session.getAttribute("seller_id");
            if (sellerId != null) {
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO operation_log (account_id, account_type, operation_content, ip_address) VALUES (?, ?, ?, ?)")) {
                    logStmt.setInt(1, sellerId);
                    logStmt.setString(2, "seller");
                    logStmt.setString(3, "销售人员注销");
                    logStmt.setString(4, ipAddress);
                    logStmt.executeUpdate();
                }
            }

            // 处理管理员注销
            Integer adminId = (Integer) session.getAttribute("admin_id");
            if (adminId != null) {
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO operation_log (account_id, account_type, operation_content, ip_address) VALUES (?, ?, ?, ?)")) {
                    logStmt.setInt(1, adminId);
                    logStmt.setString(2, "admin");
                    logStmt.setString(3, "管理员注销");
                    logStmt.setString(4, ipAddress);
                    logStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        session.invalidate();
        response.sendRedirect("index.jsp");
    }
}