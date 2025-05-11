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

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String adminName = request.getParameter("adminName");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE admin_name = ? AND password = ?")) {
            stmt.setString(1, adminName);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int adminId = rs.getInt("admin_id");
                HttpSession session = request.getSession();
                session.setAttribute("admin_id", adminId);
                session.setAttribute("admin_name", adminName);

                // 记录登录日志
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO operation_log (account_id, account_type, operation_content, ip_address) VALUES (?, ?, ?, ?)")) {
                    logStmt.setInt(1, adminId);
                    logStmt.setString(2, "admin");
                    logStmt.setString(3, "管理员登录");
                    logStmt.setString(4, ipAddress);
                    logStmt.executeUpdate();
                }

                response.sendRedirect("AdminManageServlet");
            } else {
                response.sendRedirect("admin_login.jsp?error=用户名或密码错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("admin_login.jsp?error=登录失败");
        }
    }
}