package com.unimelb.tomcatbypass.control;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "LoginControlServlet", value = "/auth/login-control")
public class LoginControlServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginControlServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("GET " + request.getRequestURI());

        Session.startSession();
        AppUser loggedInUser = AppUserService.findByUsername(AuthUtils.getAuthenticatedUser());
        if (loggedInUser.getROLE().equals("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/auth/admin/admin-landing-page");
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/landing-page");
        }
        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("POST " + request.getRequestURI());
    }
}
