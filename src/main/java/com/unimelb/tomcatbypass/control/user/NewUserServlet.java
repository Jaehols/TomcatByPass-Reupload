package com.unimelb.tomcatbypass.control.user;

import static com.unimelb.tomcatbypass.service.AppUserService.ValidatedUserParams;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.service.AppUserService;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@WebServlet(name = "NewUserServlet", value = "/register")
public class NewUserServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(NewUserServlet.class.getName());
    private static final String QUERY = "query";
    static final String SECRET = "";
    static final Integer SALT_LENGTH = 8;
    static final Integer NUM_ITERATIONS = 1850;
    static final Integer HASH_WIDTH = 256;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        // No need for a session here

        String username = ParameterValidator.getStringOrNull(request, "uname");
        String email = ParameterValidator.getStringOrNull(request, "email");
        String encodedPassword = new Pbkdf2PasswordEncoder(SECRET, SALT_LENGTH, NUM_ITERATIONS, HASH_WIDTH).encode(ParameterValidator.getStringOrNull(request, "pwd"));
        String address = ParameterValidator.getStringOrNull(request, "address");

        ValidatedUserParams validatedUserParams = new ValidatedUserParams(username, email, encodedPassword, address);

        if (AppUserService.saveNewUser(validatedUserParams)) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Username Unavailable");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
