package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getBigDecimalOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getConditionOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getNonEmptyStringOrNull;
import static com.unimelb.tomcatbypass.control.validation.ParameterValidator.getTimestampOrNull;
import static com.unimelb.tomcatbypass.service.ListingService.ValidatedAuctionListingParams;

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

@WebServlet(name = "AuctionListingCreateServlet", value = "/auth/listing/auction-listing-create")
public class AuctionListingCreateServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AuctionListingCreateServlet.class.getName());
    private static final String NO_SG_JSP = "auction-listing-create.jsp";
    private static final String SUCCESS_DELETE_URL = "listing-user-all";
    private static final Long SMALLEST_AUCTION_DELAY_MINUTES = 30L;

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
            ValidatedAuctionListingParams validatedAuctionListingParams = ValidatedAuctionListingParams.builder()
                    .startPrice(getBigDecimalOrNull(request, "startPrice"))
                    .description(getNonEmptyStringOrNull(request, "description"))
                    .endTimestamp(getTimestampOrNull(request, "endTimestamp", SMALLEST_AUCTION_DELAY_MINUTES))
                    .condition(getConditionOrNull(request, "condition"))
                    .build();

            if (ListingService.saveNewAuctionListing(
                    validatedAuctionListingParams, sellerGroupId, AuthUtils.getAuthenticatedUser())) {
                log.log(Level.INFO, "Successfully created Auction listing");
                response.sendRedirect(SUCCESS_DELETE_URL);
            } else {
                log.log(Level.INFO, "Failed to create Auction listing");
                ListingServletUtils.checkSellerGroupAndRedirect(request, response, NO_SG_JSP);
            }

        } catch (NullPointerException e) {
            log.log(Level.INFO, "Invalid Input Field Value For New Auction Listing: " + e.getMessage());
            request.setAttribute("errorMessage", "Invalid Input Field Value For New Auction Listing");
            ListingServletUtils.checkSellerGroupAndRedirect(request, response, NO_SG_JSP);
        }
    }
}
