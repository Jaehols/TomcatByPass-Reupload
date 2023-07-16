package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getBigDecimalOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getConditionOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getIntegerOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getNonEmptyStringOrNull;
import static com.unimelb.tomcatbypass.service.ListingService.ValidatedFixedListingParams;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.service.ListingService;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "FixedListingCreateServlet", value = "/auth/listing/fixed-listing-create")
public class FixedListingCreateServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(FixedListingCreateServlet.class.getName());
    private static final String NO_SG_JSP = "fixed-listing-create.jsp";
    private static final String SUCCESS_DELETE_URL = "listing-user-all";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        ListingServletUtils.checkSellerGroupAndRedirect(request, response, NO_SG_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        UUID sellerGroupId = ListingServletUtils.getChosenSellerGroupId(request, response, NO_SG_JSP);

        try {
            ValidatedFixedListingParams validatedFixedListingParams = ValidatedFixedListingParams.builder()
                    .price(getBigDecimalOrNull(request, "price"))
                    .description(getNonEmptyStringOrNull(request, "description"))
                    .condition(getConditionOrNull(request, "condition"))
                    .quantity(getIntegerOrNull(request, "quantity"))
                    .build();

            String activeUsername = AuthUtils.getAuthenticatedUser();
            if (ListingService.saveNewFixedListing(validatedFixedListingParams, sellerGroupId, activeUsername)) {
                log.log(Level.INFO, "Successfully created Fixed listing");
                response.sendRedirect(SUCCESS_DELETE_URL);
            } else {
                log.log(Level.INFO, "Failed to create Fixed listing");
                ListingServletUtils.checkSellerGroupAndRedirect(request, response, NO_SG_JSP);
            }

        } catch (NullPointerException e) {
            log.log(Level.INFO, "Invalid Input Field Value For New Fixed Listing: " + e.getMessage());
            request.setAttribute("errorMessage", "Invalid Input Field Value For New Fixed Listing");
            ListingServletUtils.checkSellerGroupAndRedirect(request, response, NO_SG_JSP);
        }
    }
}
