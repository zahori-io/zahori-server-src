package io.zahori.server.model;

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

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerVersions {

    public String apiVersion;
    public String remote;
    public String remoteVersion;
    public String latestServerVersion;
    public String zenOfZahori;

    public ServerVersions() {
        super();
    }

    @Override
    public String toString() {
        return "ServerVersions [apiVersion=" + apiVersion + ", remote=" + remote + ", remoteVersion=" + remoteVersion + ", latestServerVersion="
                + latestServerVersion + ", zenOfZahori=" + zenOfZahori + "]";
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public String getLatestServerVersion() {
        return latestServerVersion;
    }

    public void setLatestServerVersion(String latestServerVersion) {
        this.latestServerVersion = latestServerVersion;
    }

    public String getZenOfZahori() {
        return zenOfZahori;
    }

    public void setZenOfZahori(String zenOfZahori) {
        this.zenOfZahori = zenOfZahori;
    }

}
