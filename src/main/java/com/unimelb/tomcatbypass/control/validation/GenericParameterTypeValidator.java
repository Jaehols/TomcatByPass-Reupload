package com.unimelb.tomcatbypass.control.validation;

import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * The idea of this class is to get rid of heaps of duplicated try-catches in the servlets, while also enforcing
 *  consistent logging messages.
 */
public class GenericParameterTypeValidator<T> {
    private static final Logger log = Logger.getLogger(GenericParameterTypeValidator.class.getName());

    public T getParamOfTypeOrNull(HttpServletRequest request, String paramName, StringConverter<T> stringConverter) {
        T param = null;
        try {
            param = tryGetParam(request, paramName, stringConverter);
            log.info(MessageFormat.format(
                    "good request param: method={0} url={1} paramName={2} paramVal={3}",
                    request.getMethod(), request.getRequestURI(), paramName, param));
        } catch (IllegalArgumentException | NullPointerException e) {
            logException(request, paramName, stringConverter, param);
        }
        return param;
    }

    protected T tryGetParam(HttpServletRequest request, String paramName, StringConverter<T> stringConverter)
            throws IllegalArgumentException, NullPointerException {
        return stringConverter.getObjectFromString(request.getParameter(paramName));
    }

    protected void logException(
            HttpServletRequest request, String paramName, StringConverter<T> stringConverter, T param) {
        log.info(MessageFormat.format(
                "empty or bad request param: method={0} url={1} paramName={2} paramVal={3}",
                request.getMethod(), request.getRequestURI(), paramName, param));
    }
}
