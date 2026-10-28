package com.publicsafety;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // Create login session
                HttpSession session = request.getSession();

                // Store logged-in user's ID
                session.setAttribute("userId", rs.getInt("id"));

                // Store username
                session.setAttribute("username", rs.getString("username"));

                response.sendRedirect("dashboard.html");

            } else {

                response.setContentType("text/html");

                PrintWriter out = response.getWriter();

                out.println("<h2>Invalid Username or Password</h2>");
                out.println("<a href='login.html'>Try Again</a>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            PrintWriter out = response.getWriter();

            out.println("<h2>Login Failed</h2>");
            out.println("<p>Something went wrong.</p>");
        }
    }
}