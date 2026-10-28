package com.publicsafety;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/HealthCheckinServlet")
public class HealthCheckinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String healthStatus = request.getParameter("health_status");
        String notes = request.getParameter("notes");

        String sql = "INSERT INTO health_checkins (user_id, health_status, notes) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, healthStatus);
            ps.setString(3, notes);
            ps.executeUpdate();

            response.sendRedirect("dashboard.html?status=checkin_success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.html?error=checkin_failed");
        }
    }
}