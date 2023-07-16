package com.unimelb.tomcatbypass.control.order;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.AuctionOrder;
import com.unimelb.tomcatbypass.model.FixedPriceOrder;
import com.unimelb.tomcatbypass.service.AuthService;
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

@WebServlet(name = "ViewSellerGroupOrdersServlet", value = "/auth/orders/view-seller-group-orders")
public class ViewSellerGroupOrdersServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ViewSellerGroupOrdersServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        UUID sellerGroupId = ParameterValidator.getUuidOrNull(request, "sg_id");

        if (AuthService.isUserInSellerGroup(AuthUtils.getAuthenticatedUser(), sellerGroupId)) {
            List<FixedPriceOrder> fixedOrders = OrderService.getFixedBySgId(sellerGroupId);
            List<AuctionOrder> auctionOrders = OrderService.getAuctionBySgId(sellerGroupId);

            HashMap<UUID, String> descriptions = OrderServletUtils.allOrderHashmapCreator(fixedOrders, auctionOrders);

            request.setAttribute("fixedOrders", fixedOrders);
            request.setAttribute("auctionOrders", auctionOrders);
            request.setAttribute("descriptions", descriptions);

            request.getRequestDispatcher("view-orders-sg.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/landing-page");
        }

        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);
    }
}
