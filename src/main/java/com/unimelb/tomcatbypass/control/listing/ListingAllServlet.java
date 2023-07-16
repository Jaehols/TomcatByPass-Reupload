package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.listing.ListingAllCommon.QUERY;

import com.unimelb.tomcatbypass.control.ServletUtils;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "ListingAllServlet", value = "/auth/listing/listing-all")
public class ListingAllServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ListingAllServlet.class.getName());
    private static final String DO_GET_FORWARD_JSP = "listing-all.jsp";
    private static final String DO_POST_FORWARD_JSP = "listing-all.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        ListingAllCommon.doGetListingAllGeneric(request, response, log, DO_GET_FORWARD_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        ListingAllCommon.doPostListingAllGeneric(request, response, log, DO_POST_FORWARD_JSP);
    }
}
