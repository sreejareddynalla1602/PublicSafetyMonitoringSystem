package com.publicsafety;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class EmergencyContactServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
                           throws ServletException, IOException {

        try {

            HttpSession session = request.getSession(false);

            if (session == null ||
                session.getAttribute("userId") == null) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            int userId = (Integer) session.getAttribute("userId");

            String contactName =
                    request.getParameter("contact_name");

            String phone =
                    request.getParameter("phone");

            String relationship =
                    request.getParameter("relationship");

            String sql =
                    "INSERT INTO emergency_contacts " +
                    "(user_id, contact_name, phone, relationship) " +
                    "VALUES (?, ?, ?, ?)";

            Connection con =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setString(2, contactName);
            ps.setString(3, phone);
            ps.setString(4, relationship);

            ps.executeUpdate();

            ps.close();
            con.close();

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}