package com.unimelb.tomcatbypass.control.listing;

import static com.unimelb.tomcatbypass.service.ListingService.findByBidder;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.AppUser;
import com.unimelb.tomcatbypass.model.AuctionListing;
import com.unimelb.tomcatbypass.model.Bid;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.BidService;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.service.OrderService;
import com.unimelb.tomcatbypass.utils.Session;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ViewFollowedAuctionListingsServlet", value = "/auth/listing/auctions")
public class ViewFollowedAuctionListingsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ViewFollowedAuctionListingsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        fetchAndDisplay(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        // First thing is to start a session!!!
        Session.startSession();

        // validate listing_id
        UUID listingId = ParameterValidator.getUuidOrNull(request, "listing_id");
        listingId = ListingService.getById(listingId) == null ? null : listingId;

        if (listingId != null) {
            // get address (use user address if that is decided) parameter
            boolean useDefaultAddress =
                    Objects.equals(ParameterValidator.getStringOrNull(request, "use_default_address"), "on");
            String username = AuthUtils.getAuthenticatedUser();
            String address = ParameterValidator.getStringOrNull(request, "address");
            if (useDefaultAddress) {
                AppUser user = AppUserService.findByUsername(username);
                if (user != null) {
                    address = user.getAddress();
                }
            }

            // fire the order off
            log.info(MessageFormat.format(
                    "Auction Order submitted for '{0}' with use_default_address set to: '{1}'! The address used was '{2}'.",
                    listingId, useDefaultAddress, address));

            // This service will start its own session, but that's fine!
            OrderService.ValidatedAuctionOrderParams order = new OrderService.ValidatedAuctionOrderParams(address);

            if (OrderService.createAuctionOrder(order, listingId, username)) {
                request.setAttribute("feedback", "Your order was successfully submitted.");
            } else {
                request.setAttribute("feedback", "Your order was not successful, please try again.");
            }
        }

        // this method creates a session itself, but that's okay, because creating a new session closes any existing
        // ones safely
        fetchAndDisplay(request, response);

        // Close the session at the end! This catches cases where other methods don't close it properly.
        Session.closeSession();
    }

    private void fetchAndDisplay(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // At the start of your servlet calls, start a Session.
        Session.startSession();

        // fetch user details
        String username = AuthUtils.getAuthenticatedUser();
        request.setAttribute("username", username);

        // get followed listings
        List<AuctionListing> listings = findByBidder(username);
        listings.sort(Comparator.comparing(AuctionListing::getEndTimestamp));
        request.setAttribute("listings", listings);

        /*
         * TODO: Should we just use one Service call instead of multiple service methods here?
         */

        // get info for each listing
        HashMap<UUID, Bid> bids = new HashMap<>();
        HashMap<UUID, String> usernames = new HashMap<>();
        HashMap<UUID, Boolean> listingHasOrder = new HashMap<>();
        HashMap<UUID, Boolean> listingEnded = new HashMap<>();
        for (AuctionListing listing : listings) {
            Bid bid = BidService.findHighestBid(listing.getListingId());
            listingHasOrder.put(listing.getListingId(), OrderService.listingHasAuctionOrder(listing.getListingId()));

            bids.put(listing.getListingId(), bid);
            usernames.put(listing.getListingId(), bid.getUser().getUsername());
            listingEnded.put(listing.getListingId(), listing.getEndTimestamp().before(Timestamp.from(Instant.now())));
        }
        request.setAttribute("bids", bids);
        request.setAttribute("bids_usernames", usernames);
        request.setAttribute("listing_has_order", listingHasOrder);
        request.setAttribute("listing_ended", listingEnded);

        request.getRequestDispatcher("auction-listing-followed.jsp").forward(request, response);

        // Once you are done, close the Session to clean up the connection object!!!
        Session.closeSession();
    }
}
