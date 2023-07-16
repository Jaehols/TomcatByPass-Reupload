package com.unimelb.tomcatbypass.control;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "LandingServlet", value = "/auth/landing-page")
public class LandingServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LandingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        request.getRequestDispatcher("landing-page.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {}
}
