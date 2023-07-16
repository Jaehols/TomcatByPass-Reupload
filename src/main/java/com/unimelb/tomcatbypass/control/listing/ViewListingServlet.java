package com.unimelb.tomcatbypass.control.listing;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.*;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.BidService;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.service.OrderService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unimelb.tomcatbypass.utils.Session;
import org.springframework.security.core.context.SecurityContextHolder;

@WebServlet(name = "ViewListingServlet", value = "/auth/listing/view")
public class ViewListingServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ViewListingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        loadListingAndRedirect(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        // validate and fetch listing
        UUID listingId = ParameterValidator.getUuidOrNull(request, "listing_id");
        Listing listing = ListingService.getById(listingId);

        // handle auction listing case - placing a bid
        if (listing instanceof AuctionListing) {
            BigDecimal bid_value = ParameterValidator.getBigDecimalOrNull(request, "value");
            if (bid_value != null) {
                // the fire the bid off
                log.info("Bid submitted with value of $" + bid_value + "!");
                BidService.ValidatedBidParams bid = new BidService.ValidatedBidParams(bid_value);
                String username = AuthUtils.getAuthenticatedUser();
                if (BidService.createBid(bid, listing.getListingId(), username)) {
                    request.setAttribute("feedback", "Your bid was successfully submitted.");
                } else {
                    request.setAttribute("feedback", "Your bid was not successful, please try again.");
                }
            }
        }

        // handle fixed price listing case - placing an order
        if (listing instanceof FixedListing) {
            // get quantity parameter
            Integer quantity = ParameterValidator.getIntegerOrNull(request, "quantity");

            // get address (use user address if that is decided) parameter
            boolean useDefaultAddress = Objects.equals(ParameterValidator.getStringOrNull(request, "use_default_address"), "on");
            String username = AuthUtils.getAuthenticatedUser();
            String address = ParameterValidator.getStringOrNull(request, "address");
            if (useDefaultAddress) {
                AppUser user = AppUserService.findByUsername(username);
                if (user != null) {
                    address = user.getAddress();
                }
            }

            // fire the order off
            log.info("Fixed-Price Order submitted with quantity of " + quantity
                    + " and use_default_address set to: '" + useDefaultAddress
                    + "'! The address used was '" + address + "'.");
            OrderService.ValidatedFixedPriceOrderParams order =
                    new OrderService.ValidatedFixedPriceOrderParams(address, quantity);
            if (OrderService.createFixedPriceOrder(order, listing.getListingId(), username)) {
                request.setAttribute("feedback", "Your order was successfully created.");
            } else {
                request.setAttribute("feedback", "Your order was not successful, please try again.");
            }
        }


        loadListingAndRedirect(request, response);

        Session.closeSession();
    }

    private void loadListingAndRedirect(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Session.startSession();

        // validate and fetch listing
        UUID listingId = ParameterValidator.getUuidOrNull(request, "listing_id");
        Listing listing = ListingService.getById(listingId);
        if (listing == null) {
            log.warning("GET ViewListing called on invalid listing_id!");
        }

        // if auction listing, we want bid info
        if (listing instanceof AuctionListing) {
            Bid bid = BidService.findHighestBid(listing.getListingId());
            boolean bid_found = bid != null;
            request.setAttribute("bid_found", bid_found);
            if (bid_found) {
                request.setAttribute("bid", bid);
                request.setAttribute("bidder_username", bid.getUser().getUsername());
            }

            boolean auctionOver = ((AuctionListing) listing).getEndTimestamp().before(Timestamp.from(Instant.now()));
            request.setAttribute("auction_over", auctionOver);
        }

        request.setAttribute("listing", listing);

        // dispatch to jsp
        request.getRequestDispatcher("listing-view.jsp").forward(request, response);

        Session.closeSession();
    }
}
