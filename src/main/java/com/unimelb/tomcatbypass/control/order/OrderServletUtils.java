package com.unimelb.tomcatbypass.control.order;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.model.AuctionOrder;
import com.unimelb.tomcatbypass.model.FixedPriceOrder;
import com.unimelb.tomcatbypass.service.AppUserService;
import com.unimelb.tomcatbypass.service.AuthService;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.utils.Session;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class OrderServletUtils {
    private static final Logger log = Logger.getLogger(OrderServletUtils.class.getName());
    private static final String SELLER_GROUP_HOME_ADDRESS = "/auth/sellergroup/seller-group-home";
    private static final String ADMIN_ALL_ORDERS_ADDRESS = "/auth/admin/orders/all-orders";
    private static final String USER_ORDERS_ADDRESS = "/auth/orders/view-orders";

    static String afterEditRedirectPath(String isSgRequest, String username, String updateType) {

        //There is a session in here as we can't use the same sessions that are used in the service methods that are
        // called before this in a servlet, they are closed at the end of executeTransaction so need to start a newy
        Session.startSession();

        if (isSgRequest.equals("true")) {
            log.info("Seller Group Authorized Order Redirect" + updateType);
            Session.closeSession();
            return SELLER_GROUP_HOME_ADDRESS;
        } else {
            if (AuthService.isUserAdmin(username)) {
                log.info("Admin Order Redirect" + updateType);
                Session.closeSession();
                return ADMIN_ALL_ORDERS_ADDRESS;
            } else {
                log.info("User is Owner Authorized Order Redirect" + updateType);
                Session.closeSession();
                return USER_ORDERS_ADDRESS;
            }
        }
    }

    static HashMap<UUID, String> allOrderHashmapCreator (List<FixedPriceOrder> fixedOrders, List<AuctionOrder> auctionOrders) {
        HashMap<UUID, String> descriptions = new HashMap<>();
        for (FixedPriceOrder order : fixedOrders) {
            // get the listing for this order
            descriptions.put(
                    order.getOrderId(), order.getListing().getDescription());
        }
        for (AuctionOrder order : auctionOrders) {
            // get the listing for this order
            descriptions.put(
                    order.getOrderId(), order.getListing().getDescription());
        }
        return descriptions;
    }
}
