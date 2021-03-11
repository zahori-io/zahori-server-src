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
 * The type Selenoid state.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelenoidState {

    private int pending = 0;
    private int queued = 0;
    private int total = 0;
    private int used = 0;

    /**
     * Instantiates a new Selenoid state.
     */
    public SelenoidState() {
        super();
    }

    @Override
    public String toString() {
        return "SelenoidState [pending=" + pending + ", queued=" + queued + ", total=" + total + ", used=" + used + "]";
    }

    /**
     * Gets pending.
     *
     * @return the pending
     */
    public int getPending() {
        return pending;
    }

    /**
     * Sets pending.
     *
     * @param pending the pending
     */
    public void setPending(int pending) {
        this.pending = pending;
    }

    /**
     * Gets queued.
     *
     * @return the queued
     */
    public int getQueued() {
        return queued;
    }

    /**
     * Sets queued.
     *
     * @param queued the queued
     */
    public void setQueued(int queued) {
        this.queued = queued;
    }

    /**
     * Gets total.
     *
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets total.
     *
     * @param total the total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets used.
     *
     * @return the used
     */
    public int getUsed() {
        return used;
    }

    /**
     * Sets used.
     *
     * @param used the used
     */
    public void setUsed(int used) {
        this.used = used;
    }

}
