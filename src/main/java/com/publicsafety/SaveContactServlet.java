package com.publicsafety;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/SaveContactServlet")
public class SaveContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // UPDATE YOUR MYSQL ROOT PASSWORD HERE IF DIFFERENT
    private static final String DB_URL = "jdbc:mysql://localhost:3306/public_safety?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root"; 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String contactName = request.getParameter("name");
        if (contactName == null) contactName = request.getParameter("contactName");
        if (contactName == null) contactName = request.getParameter("contact_name");

        String contactPhone = request.getParameter("phone");
        if (contactPhone == null) contactPhone = request.getParameter("phoneNumber");
        if (contactPhone == null) contactPhone = request.getParameter("phone_number");

        String relation = request.getParameter("relation");

        // Handle JSON Body fallback
        if (contactName == null && contactPhone == null) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();
            
            if (body.contains("\"name\":")) contactName = extractJsonValue(body, "name");
            else if (body.contains("\"contactName\":")) contactName = extractJsonValue(body, "contactName");

            if (body.contains("\"phone\":")) contactPhone = extractJsonValue(body, "phone");
            else if (body.contains("\"phoneNumber\":")) contactPhone = extractJsonValue(body, "phoneNumber");

            if (body.contains("\"relation\":")) relation = extractJsonValue(body, "relation");
        }

        if (relation == null || relation.trim().isEmpty()) {
            relation = "Emergency";
        }

        // Resolve user ID from session
        HttpSession session = request.getSession(false);
        int userId = 1; // Default fallback ID
        if (session != null) {
            Object userObj = session.getAttribute("userId");
            if (userObj == null) userObj = session.getAttribute("user_id");
            if (userObj == null) userObj = session.getAttribute("id");

            if (userObj instanceof Integer) {
                userId = (Integer) userObj;
            } else if (userObj instanceof String) {
                try { userId = Integer.parseInt((String) userObj); } catch (Exception ignored) {}
            }
        }

        if (contactName == null || contactPhone == null || contactName.trim().isEmpty() || contactPhone.trim().isEmpty()) {
            out.print("{\"status\":\"error\", \"message\":\"Contact name and phone number are required.\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                
                String sql = "INSERT INTO emergency_contacts (user_id, contact_name, phone_number, relation) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, contactName.trim());
                    pstmt.setString(3, contactPhone.trim());
                    pstmt.setString(4, relation.trim());

                    int rowsInserted = pstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("[SUCCESS] Contact saved into MySQL database!");
                        out.print("{\"status\":\"success\", \"message\":\"Contact saved successfully!\"}");
                    } else {
                        out.print("{\"status\":\"error\", \"message\":\"Failed to save contact in database.\"}");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DATABASE EXCEPTION]: " + e.getMessage());
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Database error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start != -1) {
                start += pattern.length();
                int end = json.indexOf("\"", start);
                if (end != -1) {
                    return json.substring(start, end);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
}