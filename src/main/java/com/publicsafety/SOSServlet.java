package com.publicsafety;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/SOSServlet")
public class SOSServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String message = request.getParameter("message");
        if (message == null || message.trim().isEmpty()) {
            message = "EMERGENCY: SOS Alert Triggered!";
        }

        String sqlSOS = "INSERT INTO sos_alerts (user_id, message) VALUES (?, ?)";
        String sqlNotify = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        String sqlContact = "SELECT phone FROM emergency_contacts WHERE user_id = ? ORDER BY id DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection()) {
            // 1. Record SOS Alert in database
            try (PreparedStatement psSOS = con.prepareStatement(sqlSOS)) {
                psSOS.setInt(1, userId);
                psSOS.setString(2, message);
                psSOS.executeUpdate();
            }

            // 2. Record Notification in database
            try (PreparedStatement psNotify = con.prepareStatement(sqlNotify)) {
                psNotify.setInt(1, userId);
                psNotify.setString(2, "CRITICAL: " + message);
                psNotify.executeUpdate();
            }

            // 3. Fetch latest emergency contact phone number
            String targetPhone = null;
            try (PreparedStatement psContact = con.prepareStatement(sqlContact)) {
                psContact.setInt(1, userId);
                try (ResultSet rs = psContact.executeQuery()) {
                    if (rs.next()) {
                        targetPhone = rs.getString("phone");
                    }
                }
            }

            // 4. Dispatch real SMS via Fast2SMS API Gateway
            if (targetPhone != null && !targetPhone.trim().isEmpty()) {
                String smsContent = "EMERGENCY ALERT: SOS triggered by Public Safety System! Message: " + message;
                SMSUtil.sendSMS(targetPhone, smsContent);
            } else {
                System.out.println("[SOS WARNING]: No emergency contact found in database for userId " + userId);
            }

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database/SMS Error");
        }
    }
}