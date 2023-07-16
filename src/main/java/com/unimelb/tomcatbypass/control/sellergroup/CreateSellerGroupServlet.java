package com.unimelb.tomcatbypass.control.sellergroup;

import static com.unimelb.tomcatbypass.service.SellerGroupService.ValidatedSellerGroupParams;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@WebServlet(name = "CreateSellerGroupServlet", value = "/auth/sellergroup/create-seller-group")
public class CreateSellerGroupServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(CreateSellerGroupServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletUtils.logHttpRequest(request, log);
        response.sendRedirect("seller-group-create.jsp");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletUtils.logHttpRequest(request, log);

        String sgName = ParameterValidator.getStringOrNull(request, "sg-name");

        try {
            ValidatedSellerGroupParams validatedSellerGroupParams =
                    new ValidatedSellerGroupParams(sgName);
            String username = AuthUtils.getAuthenticatedUser();
            log.info("Creating new seller group");
            SellerGroupService.saveNewSellerGroup(validatedSellerGroupParams, username);
        } catch (IllegalArgumentException e) {
            log.log(Level.WARNING, "validation error: " + e.getMessage(), e);

            // TODO: Some sort of pop-up instead.
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println("<p>Invalid Input Given For Seller Group Parameters</p>");
        }
        response.sendRedirect("seller-group-home");
    }
}
