package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.listing.ListingAllCommon.IS_FIXED;
import static com.unimelb.tomcatbypass.control.listing.ListingAllCommon.QUERY;
import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.DEFAULT_LIMIT;
import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.DEFAULT_OFFSET;
import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.deleteListing;

import com.unimelb.tomcatbypass.control.ServletUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminListingAllServlet", value = "/auth/admin/listing/all-listings")
public class AdminListingAllServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminListingAllServlet.class.getName());
    private static final String DELETE_SEND_REDIRECT_URL = "all-listings";
    private static final String LISTING_ID = "listing_id";
    private static final String DO_GET_FORWARD_JSP = "all-listings.jsp";
    private static final String DO_POST_FORWARD_JSP = "all-listings.jsp";

    /**
     * We're using GETs to process delete requests, because I don't want to touch the JSP to change it to POST.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        if (deleteListing(request, LISTING_ID)) {
            request.setAttribute(IS_FIXED, true); // Arbitrary default.
            request.setAttribute("limit", DEFAULT_LIMIT);
            request.setAttribute("offset", DEFAULT_OFFSET);
            request.setAttribute("listings", new ArrayList<>());
            response.sendRedirect(DELETE_SEND_REDIRECT_URL);
            return;
        }

        ListingAllCommon.doGetListingAllGeneric(request, response, log, DO_GET_FORWARD_JSP);
    }

    /**
     * This method is just for the search bar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        ListingAllCommon.doPostListingAllGeneric(request, response, log, DO_POST_FORWARD_JSP);
    }
}
