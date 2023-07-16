package com.unimelb.tomcatbypass.control.listing;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.Listing;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SearchListingsServlet", value = "/auth/listing/search")
public class SearchListingsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SearchListingsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        String query = ParameterValidator.getStringOrNull(request, "query");
        if (query == null) query = "";
        List<Listing> listings = ListingService.findAllActiveListingsByText(query);
        listings.sort(Comparator.comparing(Listing::getCreateTimestamp));
        request.setAttribute("listings", listings);
        request.getRequestDispatcher("listing-all-kept.jsp").forward(request, response);

        Session.closeSession();
    }

}
