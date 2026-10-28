package com.publicsafety;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class IncidentReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        String reportType = request.getParameter("report_type");
        String description = request.getParameter("description");

        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "Other";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "No description provided";
        }

        String sql = "INSERT INTO emergency_reports " +
                     "(user_id, report_type, description) " +
                     "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, reportType);
            ps.setString(3, description);

            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error saving incident report"
            );
        }
    }
}