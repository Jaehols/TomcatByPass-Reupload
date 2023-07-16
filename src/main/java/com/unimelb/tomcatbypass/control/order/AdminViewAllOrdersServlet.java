package com.unimelb.tomcatbypass.control.order;

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
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "AdminViewAllOrdersServlet", value = "/auth/admin/orders/all-orders")
public class AdminViewAllOrdersServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminViewAllOrdersServlet.class.getName());
    private static final String QUERY = "query";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
        ServletUtils.logUnexpectedParameterIfPresent(request, log, QUERY);

        Session.startSession();

        List<FixedPriceOrder> allFixedOrders = OrderService.getAllFixed();
        List<AuctionOrder> allAuctionOrders = OrderService.getAllAuction();

        HashMap<UUID, String> descriptions = OrderServletUtils.allOrderHashmapCreator(allFixedOrders, allAuctionOrders);

        request.setAttribute("fixedOrders", allFixedOrders);
        request.setAttribute("auctionOrders", allAuctionOrders);
        request.setAttribute("descriptions", descriptions);
        request.getRequestDispatcher("all-orders.jsp").forward(request, response);

        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

    }
}
