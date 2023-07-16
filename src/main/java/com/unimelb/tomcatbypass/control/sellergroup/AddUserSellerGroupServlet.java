package com.unimelb.tomcatbypass.control.sellergroup;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AddUserSellerGroupServlet", value = "/auth/sellergroup/seller-group-add-users")
public class AddUserSellerGroupServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AddUserSellerGroupServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletUtils.logHttpRequest(request, log);

        UUID sgId = ParameterValidator.getUuidOrNull(request, "sg_id");

        SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
        request.setAttribute("sellerGroup", sellerGroup);
        request.getRequestDispatcher("seller-group-add-users.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        String query = ParameterValidator.getStringOrNull(request, "query");

        if (query == null) query = "";

        List<AppUser> appUsers = AppUserService.findAllUsersByText(query);
        appUsers.sort(Comparator.comparing(AppUser::getUsername));
        request.setAttribute("appUsers", appUsers);

        UUID sgId = ParameterValidator.getUuidOrNull(request, "sg_id");
        SellerGroup sellerGroup = SellerGroupService.findBySgId(sgId);
        request.setAttribute("sellerGroup", sellerGroup);

        request.getRequestDispatcher("seller-group-add-users.jsp").forward(request, response);

        Session.closeSession();
    }
}
