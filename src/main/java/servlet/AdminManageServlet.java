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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AdminManageServlet")
public class AdminManageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        Integer adminId = (Integer) request.getSession().getAttribute("admin_id");

        if (adminId == null) {
            response.sendRedirect("admin_login.jsp?error=请先登录");
            return;
        }

        if ("resetPassword".equals(action)) {
            int sellerId = Integer.parseInt(request.getParameter("sellerId"));
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE sellers SET password = 'default123' WHERE seller_id = ?")) {
                stmt.setInt(1, sellerId);
                stmt.executeUpdate();
                logOperation(conn, adminId, "admin", "重置销售人员密码: " + sellerId, request.getRemoteAddr());
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "数据库操作失败：" + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
            response.sendRedirect("AdminManageServlet");
        } else if ("delete".equals(action)) {
            int sellerId = Integer.parseInt(request.getParameter("sellerId"));
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM sellers WHERE seller_id = ?")) {
                stmt.setInt(1, sellerId);
                stmt.executeUpdate();
                logOperation(conn, adminId, "admin", "删除销售人员: " + sellerId, request.getRemoteAddr());
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "数据库操作失败：" + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
            response.sendRedirect("AdminManageServlet");
        } else {
            List<Seller> sellers = new ArrayList<>();
            List<SalesReport> reports = new ArrayList<>();
            List<OperationLog> logs = new ArrayList<>();
            List<PurchaseRecord> purchaseRecords = new ArrayList<>();
            List<UserAccessLog> userAccessLogs = new ArrayList<>();
            try (Connection conn = DBUtil.getConnection()) {
                // 获取销售人员列表
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sellers");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Seller seller = new Seller();
                    seller.setSellerId(rs.getInt("seller_id"));
                    seller.setSellerName(rs.getString("seller_name"));
                    sellers.add(seller);
                }

                // 获取销售统计
                PreparedStatement reportStmt = conn.prepareStatement(
                        "SELECT pc.category_name, SUM(ph.quantity) as total_quantity, SUM(ph.price * ph.quantity) as total_revenue " +
                                "FROM purchase_history ph JOIN product_categories pc ON ph.category_id = pc.category_id " +
                                "GROUP BY pc.category_id");
                ResultSet reportRs = reportStmt.executeQuery();
                while (reportRs.next()) {
                    SalesReport report = new SalesReport();
                    report.setCategoryName(reportRs.getString("category_name"));
                    report.setTotalQuantity(reportRs.getInt("total_quantity"));
                    report.setTotalRevenue(reportRs.getDouble("total_revenue"));
                    reports.add(report);
                }

                // 获取所有操作日志，包含操作者用户名
                PreparedStatement logStmt = conn.prepareStatement(
                        "SELECT ol.log_id, ol.account_id, ol.account_type, ol.operation_content, ol.operation_time, ol.ip_address, " +
                                "COALESCE(a.admin_name, s.seller_name) as operator_name " +
                                "FROM operation_log ol " +
                                "LEFT JOIN admins a ON ol.account_id = a.admin_id AND ol.account_type = 'admin' " +
                                "LEFT JOIN sellers s ON ol.account_id = s.seller_id AND ol.account_type = 'seller'");
                ResultSet logRs = logStmt.executeQuery();
                while (logRs.next()) {
                    OperationLog log = new OperationLog();
                    log.setLogId(logRs.getInt("log_id"));
                    log.setAccountId(logRs.getInt("account_id"));
                    log.setAccountType(logRs.getString("account_type"));
                    log.setOperationContent(logRs.getString("operation_content"));
                    log.setOperationTime(logRs.getTimestamp("operation_time"));
                    log.setIpAddress(logRs.getString("ip_address"));
                    log.setOperatorName(logRs.getString("operator_name"));
                    logs.add(log);
                }

                // 获取用户购买记录
                PreparedStatement purchaseStmt = conn.prepareStatement(
                        "SELECT ph.purchase_id, ph.user_id, u.username, ph.category_id, pc.category_name, ph.purchase_date, ph.price, ph.quantity " +
                                "FROM purchase_history ph " +
                                "JOIN users u ON ph.user_id = u.user_id " +
                                "JOIN product_categories pc ON ph.category_id = pc.category_id");
                ResultSet purchaseRs = purchaseStmt.executeQuery();
                while (purchaseRs.next()) {
                    PurchaseRecord record = new PurchaseRecord();
                    record.setPurchaseId(purchaseRs.getInt("purchase_id"));
                    record.setUserId(purchaseRs.getInt("user_id"));
                    record.setUsername(purchaseRs.getString("username"));
                    record.setCategoryId(purchaseRs.getInt("category_id"));
                    record.setCategoryName(purchaseRs.getString("category_name"));
                    record.setPurchaseDate(purchaseRs.getTimestamp("purchase_date"));
                    record.setPrice(purchaseRs.getDouble("price"));
                    record.setQuantity(purchaseRs.getInt("quantity"));
                    purchaseRecords.add(record);
                }

                // 获取用户访问日志
                PreparedStatement accessStmt = conn.prepareStatement(
                        "SELECT ual.log_id, ual.user_id, u.username, ual.ip_address, ual.login_time, ual.logout_time " +
                                "FROM user_access_log ual " +
                                "JOIN users u ON ual.user_id = u.user_id");
                ResultSet accessRs = accessStmt.executeQuery();
                while (accessRs.next()) {
                    UserAccessLog accessLog = new UserAccessLog();
                    accessLog.setLogId(accessRs.getInt("log_id"));
                    accessLog.setUserId(accessRs.getInt("user_id"));
                    accessLog.setUsername(accessRs.getString("username"));
                    accessLog.setIpAddress(accessRs.getString("ip_address"));
                    accessLog.setLoginTime(accessRs.getTimestamp("login_time"));
                    accessLog.setLogoutTime(accessRs.getTimestamp("logout_time"));
                    userAccessLogs.add(accessLog);
                }

                request.setAttribute("sellers", sellers);
                request.setAttribute("reports", reports);
                request.setAttribute("logs", logs);
                request.setAttribute("purchaseRecords", purchaseRecords);
                request.setAttribute("userAccessLogs", userAccessLogs);
                request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "数据库查询失败：" + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        Integer adminId = (Integer) request.getSession().getAttribute("admin_id");

        if (adminId == null) {
            response.sendRedirect("admin_login.jsp?error=请先登录");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if ("add".equals(action)) {
                String sellerName = request.getParameter("sellerName");
                String password = request.getParameter("password");

                PreparedStatement stmt = conn.prepareStatement("INSERT INTO sellers (seller_name, password) VALUES (?, ?)");
                stmt.setString(1, sellerName);
                stmt.setString(2, password);
                stmt.executeUpdate();
                logOperation(conn, adminId, "admin", "添加销售人员: " + sellerName, request.getRemoteAddr());
            }
            response.sendRedirect("AdminManageServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "数据库操作失败：" + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void logOperation(Connection conn, int accountId, String accountType, String content, String ipAddress) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO operation_log (account_id, account_type, operation_content, ip_address) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, accountId);
        stmt.setString(2, accountType);
        stmt.setString(3, content);
        stmt.setString(4, ipAddress);
        stmt.executeUpdate();
    }
}









