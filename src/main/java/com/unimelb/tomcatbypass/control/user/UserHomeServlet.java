package com.unimelb.tomcatbypass.control.user;

import com.unimelb.tomcatbypass.control.ServletUtils;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "UserHomeServlet", value = "/auth/user/user-home")
public class UserHomeServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserHomeServlet.class.getName());
    private static final String QUERY = "query";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        request.getRequestDispatcher("user-home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
