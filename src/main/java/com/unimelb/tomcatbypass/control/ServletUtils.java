package com.unimelb.tomcatbypass.control;

import com.unimelb.tomcatbypass.control.validation.ParameterValidator;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    public static void logHttpRequest(HttpServletRequest request, Logger log) {
        log.info(MessageFormat.format("{0} {1}", request.getMethod(), request.getRequestURI()));
    }

    public static void logUnexpectedParameterIfPresent(HttpServletRequest request, Logger log, String paramName) {
        String query = ParameterValidator.getStringOrNull(request, paramName);
        if (query != null) {
            log.warning(MessageFormat.format(
                    "{0} {1} received unexpected parameter value for: {2}={3}",
                    request.getMethod(), request.getRequestURI(), paramName, query));
        }
    }

    public static void logEvent(HttpServletRequest request, Logger log, String event) {
        log.info(MessageFormat.format("in {0} {1} {2}", request.getMethod(), request.getRequestURI(), event));
    }
}
