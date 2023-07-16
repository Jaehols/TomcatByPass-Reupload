package com.unimelb.tomcatbypass.control.sellergroup;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.service.AuthService;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AllSellerGroupsServlet", value = "/auth/sellergroup/seller-group-home")
public class AllSellerGroupsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(CreateSellerGroupServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        UUID sgId = ParameterValidator.getUuidOrNull(request, "sg_id");
        String username = AuthUtils.getAuthenticatedUser();
        if (sgId != null && AuthService.checkUserSellerGroupPermission(username, sgId)) {
            SellerGroupService.deleteSellerGroup(sgId, username);
        }

        List<SellerGroup> sellerGroups = SellerGroupService.findSellerGroupsByAppUsername(AuthUtils.getAuthenticatedUser());
        request.setAttribute("sellerGroups", sellerGroups);
        request.getRequestDispatcher("seller-group-home.jsp").forward(request, response);

        Session.closeSession();
    }
}
