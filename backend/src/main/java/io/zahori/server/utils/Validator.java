package io.zahori.server.utils;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 PANEL SISTEMAS INFORMATICOS,S.L
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class Validator {

    private static final String EMAIL_REGEXP = "^[a-z0-9]+([\\._-]?[a-z0-9]+)*@[a-z0-9]+([\\.-]?[a-z0-9]+)*(\\.[a-z0-9]{2,})+$";
    public static final String INVALID_EMAIL = "i18n.validator.email";

    private static final String PASSWORD_REGEXP = "^(?=.*[0-9].{0,})(?=.*[a-z].{0,})(?=.*[A-Z].{0,})(?=.*[*.!¡'`\"ªº@çÇ#$%^&(){}\\[\\]:;<>,.¿?/~_+\\-=|\\\\].{0,}).{8,}$";
    // "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!¡'`\"ªº@çÇ#$%^&(){}\\[\\]:;<>,.¿?/~_+\\-=|\\\\]).{8,}$"
    public static final String INVALID_PASSWORD = "i18n.validator.password";

    private Validator() {
    }

    public static boolean isValidEmail(String email) {
        email = email.trim().toLowerCase();
        return isValid(EMAIL_REGEXP, email);
    }

    public static boolean isValidPassword(String password) {
        return isValid(PASSWORD_REGEXP, password);
    }

    private static boolean isValid(String regExp, String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
