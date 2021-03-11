package io.zahori.server.model.jenkins;

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

/**
 * The type Artifact.
 */
public class Artifact {

    private String displayPath;
    private String fileName;
    private String relativePath;

    /**
     * Instantiates a new Artifact.
     */
    public Artifact() {

    }

    /**
     * Instantiates a new Artifact.
     *
     * @param displayPath  the display path
     * @param fileName     the file name
     * @param relativePath the relative path
     */
    public Artifact(String displayPath, String fileName, String relativePath) {
        super();
        this.displayPath = displayPath;
        this.fileName = fileName;
        this.relativePath = relativePath;
    }

    @Override
    public String toString() {
        return "Artifact [displayPath=" + displayPath + ", fileName=" + fileName + ", relativePath=" + relativePath + "]";
    }

    /**
     * Gets display path.
     *
     * @return the display path
     */
    public String getDisplayPath() {
        return displayPath;
    }

    /**
     * Sets display path.
     *
     * @param displayPath the display path
     */
    public void setDisplayPath(String displayPath) {
        this.displayPath = displayPath;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets relative path.
     *
     * @return the relative path
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * Sets relative path.
     *
     * @param relativePath the relative path
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

}
