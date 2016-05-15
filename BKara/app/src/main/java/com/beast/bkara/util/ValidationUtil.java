package com.beast.bkara.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by VINH on 5/3/2016.
 */
public class ValidationUtil {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }
}
