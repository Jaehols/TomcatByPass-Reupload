package com.unimelb.tomcatbypass.control.user;

import static java.util.Objects.isNull;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "UserServlet", value = "/auth/user/user-details")
public class UserServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(UserServlet.class.getName());

    // Shows all details of a user sans password and created timestamp
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        Session.startSession();

        String username = ParameterValidator.getStringOrNull(request, "uname");
        AppUser user = AppUserService.findByUsername(username);

        if (!isNull(user)) {
            request.setAttribute("username", user.getUsername());
            request.setAttribute("email", user.getEmail());
            request.setAttribute("role", user.getROLE());
            request.setAttribute("address", user.getAddress());
            request.setAttribute("createtimestamp", user.getCreateTimestamp());
            request.getRequestDispatcher("user-details.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("null-user.jsp").forward(request, response);
        }
        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
