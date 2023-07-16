package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.DEFAULT_LIMIT;
import static com.unimelb.tomcatbypass.control.listing.ListingServletUtils.DEFAULT_OFFSET;

import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.FixedListing;
import com.unimelb.tomcatbypass.model.Listing;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.utils.Session;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListingAllCommon {
    static final String IS_FIXED = "isFixed";
    static final String QUERY = "query";

    public static void doGetListingAllGeneric(
            HttpServletRequest request, HttpServletResponse response, Logger log, String forwardUrl)
            throws ServletException, IOException {

        // Just show Fixed listings by default, even if there aren't any.
        Session.startSession();
        List<FixedListing> listings = ListingService.findFixedListingsInLimitOffset(DEFAULT_LIMIT, DEFAULT_OFFSET);
        Session.closeSession();

        if (!listings.isEmpty()) {
            request.setAttribute(IS_FIXED, true);
            ServletUtils.logEvent(request, log, MessageFormat.format("found {0} fixed listings", listings.size()));
        } else {
            String event =
                    MessageFormat.format("no fixed listings with limit={0} offset={1}", DEFAULT_LIMIT, DEFAULT_OFFSET);
            ServletUtils.logEvent(request, log, event);
            // Don't set isFixed attribute.
        }

        request.setAttribute("limit", DEFAULT_LIMIT);
        request.setAttribute("offset", DEFAULT_OFFSET);
        request.setAttribute("listings", listings);
        request.getRequestDispatcher(forwardUrl).forward(request, response);
    }

    public static void doPostListingAllGeneric(
            HttpServletRequest request, HttpServletResponse response, Logger log, String forwardUrl)
            throws ServletException, IOException {

        // Integer limit = getValidatedLimitParameter(request);  // NOTE: User can specify number of results per page.
        Integer offset = ListingServletUtils.getValidatedOffsetParameter(request, "offset");
        Boolean isFixed = ParameterValidator.getBooleanOrNull(request, IS_FIXED);
        String query = ParameterValidator.getStringOrNull(request, QUERY);

        if (query == null) {
            query = "";
        }

        List<? extends Listing> listings = new ArrayList<>();

        if (!"".equals(query)) {
            // Run a description search.
            ServletUtils.logEvent(request, log, "about to run description search request");
            if (isFixed == null || isFixed) {
                // Default to returning results for fixed listing.
                Session.startSession();
                listings = ListingService.findFixedListingsByDescriptionInLimitOffset(query, DEFAULT_LIMIT, offset);
                Session.closeSession();
                request.setAttribute(IS_FIXED, true);
            } else {
                Session.startSession();
                listings = ListingService.findAuctionListingsByDescriptionInLimitOffset(query, DEFAULT_LIMIT, offset);
                Session.closeSession();
                request.setAttribute(IS_FIXED, false);
            }

        } else {
            // Don't run a description search.
            ServletUtils.logEvent(request, log, "about to run basic limit offset search");
            if (isFixed == null || isFixed) {
                // Default to returning results for fixed listing.
                Session.startSession();
                listings = ListingService.findFixedListingsInLimitOffset(DEFAULT_LIMIT, offset);
                Session.closeSession();
                request.setAttribute(IS_FIXED, true);
            } else {
                Session.startSession();
                listings = ListingService.findAuctionListingsInLimitOffset(DEFAULT_LIMIT, offset);
                Session.closeSession();
                request.setAttribute(IS_FIXED, false);
            }
        }

        listings.sort(Comparator.comparing(Listing::getCreateTimestamp));

        if (listings.isEmpty()) {
            String event = MessageFormat.format(
                    "no listings with limit={0} offset={1} isFixed={2}", DEFAULT_LIMIT, offset, isFixed);
            ServletUtils.logEvent(request, log, event);
        }

        request.setAttribute("limit", DEFAULT_LIMIT);
        request.setAttribute("offset", offset);
        request.setAttribute("listings", listings);
        request.getRequestDispatcher(forwardUrl).forward(request, response);
    }
}
