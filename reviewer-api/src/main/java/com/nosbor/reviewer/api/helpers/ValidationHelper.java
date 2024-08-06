package com.nosbor.reviewer.api.helpers;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidationHelper {

    public static final String A_PROPRIEDADE_S_DEVE_SER_INFORMADA_AO_USAR_O_SERVICE =
            "A propriedade %s deve ser informada ao usar o %s!";

    public static void validate(final Map<String, String> properties,
                                final String service) {
        List<String> errorsMsg = new ArrayList<>();
        properties
                .forEach((key, value) -> validateProperty(key, value, service, errorsMsg));
        if (!errorsMsg.isEmpty()) {
            throw new RuntimeException(errorsMsg.toString());
        }
    }

    private static void validateProperty(String property,
                                        String propertyName,
                                        String service,
                                        List<String> errorsMsg) {
        if (Strings.isBlank(property)) {
            errorsMsg.add(String.format(A_PROPRIEDADE_S_DEVE_SER_INFORMADA_AO_USAR_O_SERVICE, propertyName, service));
        }
    }

}
