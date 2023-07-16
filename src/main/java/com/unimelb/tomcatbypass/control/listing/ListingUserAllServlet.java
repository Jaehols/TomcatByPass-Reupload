package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.DEFAULT_LIMIT;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.model.Listing;
import com.unimelb.tomcatbypass.service.ListingService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "ListingUserAllServlet", value = "/auth/listing/listing-user-all")
public class ListingUserAllServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ListingUserAllServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        // Integer limit = getValidatedLimitParameter(request);  // NOTE: User can specify number of results per page.
        Integer offset = ListingServletUtils.getValidatedOffsetParameter(request, "offset");

        String authenticatedUser = AuthUtils.getAuthenticatedUser();
        List<? extends Listing> listings =
                ListingService.findByUsernameInLimitOffset(authenticatedUser, DEFAULT_LIMIT, offset);

        request.setAttribute("limit", DEFAULT_LIMIT);
        request.setAttribute("offset", offset);
        request.setAttribute("listings", listings);
        request.getRequestDispatcher("listing-user-all.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
