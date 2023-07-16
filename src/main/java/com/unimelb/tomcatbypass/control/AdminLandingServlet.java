package com.unimelb.tomcatbypass.control;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "AdminLandingServlet", value = "/auth/admin/admin-landing-page")
public class AdminLandingServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminLandingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("GET " + request.getRequestURI());
        request.getRequestDispatcher("admin-landing-page.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {}
}
