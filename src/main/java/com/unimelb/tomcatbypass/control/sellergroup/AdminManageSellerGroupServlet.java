package com.unimelb.tomcatbypass.control.sellergroup;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.model.UserSgMapping;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.AuthService;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.service.UserSgMappingService;
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

@WebServlet(name = "AdminManageSellerGroupServlet", value = "/auth/admin/sellergroup/seller-group-manage")
public class AdminManageSellerGroupServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminManageSellerGroupServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletUtils.logHttpRequest(request, log);
        UUID sgId = ParameterValidator.getUuidOrNull(request, "sg_id");

        SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
        List<AppUser> appUserList = sellerGroup.getAppUsers();
        request.setAttribute("sellerGroup", sellerGroup);
        request.setAttribute("appUsers", appUserList);

        request.getRequestDispatcher("seller-group-manage.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        UUID sgId = ParameterValidator.getUuidOrNull(request, "sg_id");
        String usernameDel = ParameterValidator.getStringOrNull(request, "username_del");
        String usernameAdd = ParameterValidator.getStringOrNull(request, "username_add");
        String username = AuthUtils.getAuthenticatedUser();

        if (usernameDel != null) {
            UserSgMappingService.deleteMapping(usernameDel, sgId, username);
        }

        if (usernameAdd != null) {
            UserSgMappingService.insertMapping(usernameAdd, sgId, username);
        }

        SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
        List<AppUser> appUserList = sellerGroup.getAppUsers();
        request.setAttribute("sellerGroup", sellerGroup);
        request.setAttribute("appUsers", appUserList);

        request.getRequestDispatcher("seller-group-manage.jsp").forward(request, response);

        Session.closeSession();
    }
}
