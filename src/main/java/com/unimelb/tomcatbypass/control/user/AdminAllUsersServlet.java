package com.unimelb.tomcatbypass.control.user;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "AdminAllUsersServlet", value = "/auth/admin/user/all-users")
public class AdminAllUsersServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminAllUsersServlet.class.getName());
    private static final String QUERY = "query";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        Session.startSession();

        List<AppUser> allUsers = AppUserService.getAllUsers();
        request.setAttribute("users", allUsers);
        request.getRequestDispatcher("all-users.jsp").forward(request, response);

        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
