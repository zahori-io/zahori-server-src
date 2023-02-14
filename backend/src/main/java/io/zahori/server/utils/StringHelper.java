package io.zahori.server.utils;

import org.apache.commons.lang3.StringUtils;

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

public class StringHelper {
    
    private StringHelper() {
    }
    
    public static String replaceBlanksWithHyphens(String input){
        return StringUtils.replace(StringUtils.trim(input)," ", "-");
    }
    
    public static  String removeDuplicatedHyphens(String input)
    {
        return removeDuplicatedChars(input, '-');
    }
    
    public static  String removeDuplicatedChars(String input, char charToRemove)
    {
        if (input.length() <= 1) {
            return input;
        }
        
        if (input.charAt(0) == input.charAt(1) && input.charAt(0) == charToRemove){
            return removeDuplicatedChars(input.substring(1), charToRemove);
        } else {
            return input.charAt(0) + removeDuplicatedChars(input.substring(1), charToRemove);
        }
    }
}
