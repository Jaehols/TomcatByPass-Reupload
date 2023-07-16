package com.unimelb.tomcatbypass.control.order;

import com.unimelb.tomcatbypass.auth.AuthUtils;
import com.unimelb.tomcatbypass.control.ServletUtils;
import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import com.unimelb.tomcatbypass.model.FixedListing;
import com.unimelb.tomcatbypass.model.FixedPriceOrder;
import com.unimelb.tomcatbypass.service.ListingService;
import com.unimelb.tomcatbypass.service.OrderService;
import com.unimelb.tomcatbypass.utils.Session;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "EditFixedPriceOrderServlet", value = "/auth/orders/edit-fixed-price-order")
public class EditFixedPriceOrderServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(EditFixedPriceOrderServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        Session.startSession();

        UUID orderId = ParameterValidator.getUuidOrNull(request, "orderId");
        String isSgRequest = ParameterValidator.getStringOrNull(request, "sg");

        FixedPriceOrder order = OrderService.getFixedOrderById(orderId);
        FixedListing listing = (FixedListing) order.getListing();

        if (OrderService.canUserEditFixedOrder(orderId, AuthUtils.getAuthenticatedUser())) {
            request.setAttribute("order", order);
            request.setAttribute("listing", listing);
            if (isSgRequest.equals("true")) {
                request.getRequestDispatcher("edit-fixed-price-order-sg.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("edit-fixed-price-order.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("edit-forbidden.jsp").forward(request, response);
        }

        Session.closeSession();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.logHttpRequest(request, log);

        UUID orderId = ParameterValidator.getUuidOrNull(request, "orderId");
        String requestType = ParameterValidator.getStringOrNull(request, "requestType");
        String isSgRequest = ParameterValidator.getStringOrNull(request, "isSgRequest");

        switch (requestType) {
            case "cancel":
                if (OrderService.deleteFixedPriceOrder(orderId, AuthUtils.getAuthenticatedUser())) {
                    response.sendRedirect(request.getContextPath()
                            + OrderServletUtils.afterEditRedirectPath(
                            isSgRequest, AuthUtils.getAuthenticatedUser(), requestType));
                    log.info("Fixed Price order delete approved and attempted");
                } else {
                    request.getRequestDispatcher("edit-forbidden.jsp").forward(request, response);
                }
                break;

            case "edit":
                if (OrderService.updateFixedPriceOrder(
                        orderId,
                        ParameterValidator.getIntegerOrNull(request, "quantity"),
                        ParameterValidator.getStringOrNull(request, "address"),
                        AuthUtils.getAuthenticatedUser())) {
                    response.sendRedirect(request.getContextPath()
                            + OrderServletUtils.afterEditRedirectPath(
                            isSgRequest, AuthUtils.getAuthenticatedUser(), requestType));
                    log.info("Fixed Price order update approved and attempted");
                } else {
                    request.getRequestDispatcher("edit-forbidden.jsp").forward(request, response);
                }
                break;

            default:
                log.severe("Unrecognised Fixed Price edit/delete request");
                request.getRequestDispatcher("edit-forbidden.jsp").forward(request, response);
        }
    }
}
