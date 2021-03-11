package io.zahori.server.model.selenoid;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The type Selenoid caps.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelenoidCaps {

    private String name;
    private String browserName;
    private String version;
    private boolean enableVNC;
    private String screenResolution;
    private String timeZone;

    /**
     * Instantiates a new Selenoid caps.
     */
    public SelenoidCaps() {
        super();
    }

    @Override
    public String toString() {
        return "SelenoidCaps [name=" + name + ", browserName=" + browserName + ", version=" + version + ", enableVNC=" + enableVNC + ", screenResolution="
                + screenResolution + ", timeZone=" + timeZone + "]";
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets browser name.
     *
     * @return the browser name
     */
    public String getBrowserName() {
        return browserName;
    }

    /**
     * Sets browser name.
     *
     * @param browserName the browser name
     */
    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Is enable vnc boolean.
     *
     * @return the boolean
     */
    public boolean isEnableVNC() {
        return enableVNC;
    }

    /**
     * Sets enable vnc.
     *
     * @param enableVNC the enable vnc
     */
    public void setEnableVNC(boolean enableVNC) {
        this.enableVNC = enableVNC;
    }

    /**
     * Gets screen resolution.
     *
     * @return the screen resolution
     */
    public String getScreenResolution() {
        return screenResolution;
    }

    /**
     * Sets screen resolution.
     *
     * @param screenResolution the screen resolution
     */
    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    /**
     * Gets time zone.
     *
     * @return the time zone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets time zone.
     *
     * @param timeZone the time zone
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
