package com.unimelb.tomcatbypass.control.validation;

import com.unimelb.tomcatbypass.enums.Condition;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class ParameterValidator {
    private static final Logger log = Logger.getLogger(ParameterValidator.class.getName());
    private static final GenericParameterTypeValidator<UUID> uuidValidator = new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<Boolean> boolValidator = new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<String> stringValidator = new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<Integer> intValidator = new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<BigDecimal> bigDecimalValidator =
            new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<Condition> conditionValidator =
            new GenericParameterTypeValidator<>();
    private static final GenericParameterTypeValidator<Timestamp> timestampValidator =
            new GenericParameterTypeValidator<>();

    public static UUID getUuidOrNull(HttpServletRequest request, String paramName) {
        return uuidValidator.getParamOfTypeOrNull(request, paramName, UUID::fromString);
    }

    public static Boolean getBooleanOrNull(HttpServletRequest request, String paramName) {
        return boolValidator.getParamOfTypeOrNull(request, paramName, Boolean::parseBoolean);
    }

    // I know this seems redundant, but it guarantees consistent log messages even for strings.
    public static String getStringOrNull(HttpServletRequest request, String paramName) {
        return stringValidator.getParamOfTypeOrNull(request, paramName, String::toString);
    }

    public static String getNonEmptyStringOrNull(HttpServletRequest request, String paramName) {
        String nullOrString = stringValidator.getParamOfTypeOrNull(request, paramName, String::toString);
        return (!"".equals(nullOrString)) ? nullOrString : null;
    }

    public static Integer getIntegerOrNull(HttpServletRequest request, String paramName) {
        return intValidator.getParamOfTypeOrNull(request, paramName, Integer::valueOf);
    }

    public static BigDecimal getBigDecimalOrNull(HttpServletRequest request, String paramName) {
        return bigDecimalValidator.getParamOfTypeOrNull(request, paramName, BigDecimal::new);
    }

    public static Condition getConditionOrNull(HttpServletRequest request, String paramName) {
        return conditionValidator.getParamOfTypeOrNull(request, paramName, Condition::valueOf);
    }

    public static Timestamp getTimestampOrNull(
            HttpServletRequest request, String paramName, Long smallestDelayMinutes) {

        StringConverter<Timestamp> sc = string -> {
            log.info("given timestamp input string = " + string);
            String formattedTimestampString = string.replace("T", " ") + ":00";
            log.info("formattedTimestampString = " + formattedTimestampString);
            return Timestamp.valueOf(formattedTimestampString);
        };

        Timestamp timestamp = timestampValidator.getParamOfTypeOrNull(request, paramName, sc);

        if (timestamp == null) {
            return null;
        } else if (timestamp.before(Timestamp.valueOf(LocalDateTime.now().plusMinutes(smallestDelayMinutes)))) {
            log.info(paramName + " given by user is too soon");
            return null;
        } else {
            return timestamp;
        }
    }
}
