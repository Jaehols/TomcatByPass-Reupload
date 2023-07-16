package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.deleteListing;

import com.unimelb.tomcatbypass.control.ServletUtils;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "ListingDeleteServlet", value = "/auth/listing/listing-delete")
public class ListingDeleteServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ListingDeleteServlet.class.getName());
    private static final String LISTING_ID = "listing_id";
    private static final String SEND_REDIRECT_URL = "listing-user-all";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        if (!deleteListing(request, LISTING_ID)) {
            request.setAttribute("errorMessage", "cannot delete selected listing");
        }
        response.sendRedirect(SEND_REDIRECT_URL);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
