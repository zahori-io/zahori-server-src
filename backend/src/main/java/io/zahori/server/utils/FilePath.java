package io.zahori.server.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;

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

public class FilePath {
    
    /**
     * Normalizes path according to current Operating System
     * @param rawPath
     * @return 
     */
    public static String normalize(String rawPath) {
        Path path = Paths.get(rawPath);
        Path normalizedPath = path.normalize();
        return FilenameUtils.separatorsToSystem(normalizedPath.toString());
    }
}
