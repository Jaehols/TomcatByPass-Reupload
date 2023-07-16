package com.unimelb.tomcatbypass.control.order;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.model.AuctionOrder;
import com.unimelb.tomcatbypass.model.FixedPriceOrder;
import com.unimelb.tomcatbypass.service.OrderService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "ViewMyOrderServlet", value = "/auth/orders/view-orders")
public class ViewMyOrderServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ViewMyOrderServlet.class.getName());
    private static final String QUERY = "query";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        Session.startSession();

        String username = AuthUtils.getAuthenticatedUser();

        List<FixedPriceOrder> myFixedOrders = OrderService.getFixedOrderByUser(username);
        List<AuctionOrder> myAuctionOrders = OrderService.getAuctionByUser(username);

        HashMap<UUID, String> descriptions = OrderServletUtils.allOrderHashmapCreator(myFixedOrders, myAuctionOrders);

        request.setAttribute("fixedOrders", myFixedOrders);
        request.setAttribute("auctionOrders", myAuctionOrders);
        request.setAttribute("descriptions", descriptions);

        request.getRequestDispatcher("view-orders.jsp").forward(request, response);

        Session.closeSession();
    }
}
