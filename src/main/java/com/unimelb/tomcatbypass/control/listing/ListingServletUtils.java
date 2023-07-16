package com.unimelb.tomcatbypass.control.listing;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.SellerGroup;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.service.SellerGroupService;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListingServletUtils {
    private static final Logger log = Logger.getLogger(ListingServletUtils.class.getName());
    static final Integer DEFAULT_LIMIT = 5;
    static final Integer MAX_LIMIT = 100;
    static final Integer DEFAULT_OFFSET = 0;
    private static final String USER_HAS_NO_SELLER_GROUP = "listing-user-not-in-seller-group.jsp";

    /**
     * Checks if a user is in any seller group, if they are, this redirects them to the given uri, otherwise it sends
     *  them to a page for people who aren't in a seller group.
     */
    static void checkSellerGroupAndRedirect(HttpServletRequest request, HttpServletResponse response, String noSgUrl)
            throws ServletException, IOException {
        log.log(Level.INFO, "Finding user's seller groups");
        List<SellerGroup> sellerGroups =
                SellerGroupService.findSellerGroupsByAppUsername(AuthUtils.getAuthenticatedUser());
        if (sellerGroups.isEmpty()) {
            log.info("Found no seller groups for user");
            response.sendRedirect(USER_HAS_NO_SELLER_GROUP);
        } else {
            log.info("Found " + sellerGroups.size() + " seller groups for user");
            request.setAttribute("sellerGroups", sellerGroups);
            request.getRequestDispatcher(noSgUrl).forward(request, response);
        }
    }

    static UUID getChosenSellerGroupId(HttpServletRequest request, HttpServletResponse response, String uri)
            throws ServletException, IOException {
        UUID sellerGroupId = ParameterValidator.getUuidOrNull(request, "seller-group-uuid");

        // sellerGroupId comes from the frontend, but we passed it to the frontend, so it **should** be valid unless
        //  someone is sending posts to our app programmatically, or a user submits the form with no seller group
        // selected.
        if (sellerGroupId == null) {
            request.setAttribute("errorMessage", "Invalid Input Field Value For New Listing");
            checkSellerGroupAndRedirect(request, response, uri);
        }

        return sellerGroupId;
    }

    static Integer getValidatedLimitParameter(HttpServletRequest request, String paramName) {
        Integer limit = ParameterValidator.getIntegerOrNull(request, paramName);

        if (limit == null) {
            limit = DEFAULT_LIMIT;

        } else if (limit < 0 || limit > MAX_LIMIT) {
            String event = MessageFormat.format("dodgy user tried setting limit to {0}={1}", paramName, limit);
            ServletUtils.logEvent(request, log, event);
            limit = DEFAULT_LIMIT;
        }

        return limit;
    }

    static Integer getValidatedOffsetParameter(HttpServletRequest request, String paramName) {
        Integer offset = ParameterValidator.getIntegerOrNull(request, paramName);

        if (offset == null) {
            offset = DEFAULT_OFFSET;

        } else if (offset < 0) {
            ServletUtils.logEvent(request, log, "dodgy user set offset to < 0");
            offset = DEFAULT_OFFSET;
        }

        return offset;
    }

    public static Boolean deleteListing(HttpServletRequest request, String listingIdParamName) {
        UUID listingIdToDelete = ParameterValidator.getUuidOrNull(request, listingIdParamName);

        if (listingIdToDelete != null) {
            ServletUtils.logEvent(request, log, "received non-empty delete request");
            if (ListingService.deleteListing(listingIdToDelete, AuthUtils.getAuthenticatedUser())) {
                ServletUtils.logEvent(request, log, "Processed valid delete request");
                return true;
            } else {
                ServletUtils.logEvent(request, log, "Did not process non-empty delete request");
                return false;
            }
        } else {
            return false;
        }
    }
}
