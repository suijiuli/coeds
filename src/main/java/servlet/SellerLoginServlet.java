package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/SellerLoginServlet")
public class SellerLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sellerName = request.getParameter("sellerName");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT seller_id FROM sellers WHERE seller_name = ? AND password = ?")) {
            stmt.setString(1, sellerName);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int sellerId = rs.getInt("seller_id");
                HttpSession session = request.getSession();
                session.setAttribute("seller_id", sellerId);
                session.setAttribute("seller_name", sellerName);

                // 记录登录日志
                try (PreparedStatement logStmt = conn.prepareStatement(
                        "INSERT INTO operation_log (account_id, account_type, operation_content, ip_address) VALUES (?, ?, ?, ?)")) {
                    logStmt.setInt(1, sellerId);
                    logStmt.setString(2, "seller");
                    logStmt.setString(3, "销售人员登录");
                    logStmt.setString(4, ipAddress);
                    logStmt.executeUpdate();
                }
                System.out.println("销售成功登录");
                response.sendRedirect("SellerManageServlet");
            } else {
                response.sendRedirect("seller_login.jsp?error=用户名或密码错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("seller_login.jsp?error=登录失败");
        }
    }
}