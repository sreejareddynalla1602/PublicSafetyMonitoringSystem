package com.publicsafety;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        String sql = "INSERT INTO users "
                   + "(name, username, password, phone, email) "
                   + "VALUES (?, ?, ?, ?, ?)";

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, phone);
            ps.setString(5, email);

            int result = ps.executeUpdate();

            if (result > 0) {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Registration Successful</title>");

                out.println("<style>");

                out.println("* { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }");

                out.println("body {");
                out.println("min-height: 100vh;");
                out.println("display: flex;");
                out.println("justify-content: center;");
                out.println("align-items: center;");
                out.println("background: linear-gradient(135deg, #e3f2fd, #bbdefb);");
                out.println("}");

                out.println(".success-box {");
                out.println("width: 430px;");
                out.println("background: white;");
                out.println("padding: 45px 35px;");
                out.println("border-radius: 20px;");
                out.println("text-align: center;");
                out.println("box-shadow: 0 10px 35px rgba(0,0,0,0.15);");
                out.println("}");

                out.println(".icon {");
                out.println("width: 75px;");
                out.println("height: 75px;");
                out.println("margin: 0 auto 20px;");
                out.println("border-radius: 50%;");
                out.println("background: #e8f5e9;");
                out.println("color: #2e7d32;");
                out.println("font-size: 42px;");
                out.println("display: flex;");
                out.println("align-items: center;");
                out.println("justify-content: center;");
                out.println("}");

                out.println("h1 {");
                out.println("color: #2e7d32;");
                out.println("font-size: 27px;");
                out.println("margin-bottom: 12px;");
                out.println("}");

                out.println(".message {");
                out.println("color: #666;");
                out.println("font-size: 15px;");
                out.println("margin-bottom: 25px;");
                out.println("}");

                out.println(".user {");
                out.println("color: #1565c0;");
                out.println("font-weight: bold;");
                out.println("}");

                out.println(".login-btn {");
                out.println("display: inline-block;");
                out.println("padding: 13px 35px;");
                out.println("background: #1565c0;");
                out.println("color: white;");
                out.println("text-decoration: none;");
                out.println("border-radius: 8px;");
                out.println("font-weight: bold;");
                out.println("transition: 0.3s;");
                out.println("}");

                out.println(".login-btn:hover {");
                out.println("background: #0d47a1;");
                out.println("transform: translateY(-2px);");
                out.println("}");

                out.println(".note {");
                out.println("margin-top: 22px;");
                out.println("font-size: 12px;");
                out.println("color: #999;");
                out.println("}");

                out.println("</style>");
                out.println("</head>");

                out.println("<body>");

                out.println("<div class='success-box'>");

                out.println("<div class='icon'>✓</div>");

                out.println("<h1>Registration Successful!</h1>");

                out.println("<p class='message'>");
                out.println("Welcome <span class='user'>" + name + "</span>!");
                out.println("<br>");
                out.println("Your account has been created successfully.");
                out.println("</p>");

                out.println("<a href='login.html' class='login-btn'>Go to Login</a>");

                out.println("<p class='note'>");
                out.println("🛡 Your safety and well-being matter to us.");
                out.println("</p>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Registration Failed</title>");

            out.println("<style>");
            out.println("body {");
            out.println("min-height:100vh;");
            out.println("display:flex;");
            out.println("justify-content:center;");
            out.println("align-items:center;");
            out.println("font-family:Arial;");
            out.println("background:#ffebee;");
            out.println("}");

            out.println(".error-box {");
            out.println("background:white;");
            out.println("padding:40px;");
            out.println("border-radius:18px;");
            out.println("text-align:center;");
            out.println("box-shadow:0 10px 30px rgba(0,0,0,0.15);");
            out.println("}");

            out.println("h1 { color:#c62828; }");

            out.println("a {");
            out.println("display:inline-block;");
            out.println("margin-top:20px;");
            out.println("padding:12px 25px;");
            out.println("background:#1565c0;");
            out.println("color:white;");
            out.println("text-decoration:none;");
            out.println("border-radius:8px;");
            out.println("}");
            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='error-box'>");
            out.println("<h1>Registration Failed</h1>");
            out.println("<p>Something went wrong while creating your account.</p>");
            out.println("<a href='register.html'>Try Again</a>");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }
}