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
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zahori.server.model.Process;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Case.
 */
@Entity
@Table(name = "cases")
@NamedQuery(name = "Case.findAll", query = "SELECT c FROM Case c")
public class Case implements Serializable {

    private static final long serialVersionUID = 6216951137885144104L;
    private static final Logger LOG = LoggerFactory.getLogger(Case.class);

    @Id
    @Column(name = "case_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    private Boolean active;

    private String name;

    private String data;

    @Transient
    private Map<String, String> dataMap = new HashMap<>();

    // bi-directional many-to-one association to Process
    @JsonBackReference(value = "process")
    @ManyToOne
    @JoinColumn(name = "process_id")
    private Process process;

    // bi-directional many-to-one association to CasesExecution
    @JsonBackReference(value = "casesExecutions")
    @OneToMany(mappedBy = "cas")
    private List<CaseExecution> casesExecutions;

    // bi-directional many-to-many association to ClientTag
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cases_tags", joinColumns = {
        @JoinColumn(name = "case_id")}, inverseJoinColumns = {
        @JoinColumn(name = "tag_id")})
    private List<ClientTag> clientTags;

    /**
     * Instantiates a new Case.
     */
    public Case() {
    }

    /**
     * Gets case id.
     *
     * @return the case id
     */
    public Long getCaseId() {
        return this.caseId;
    }

    /**
     * Sets case id.
     *
     * @param caseId the case id
     */
    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    /**
     * Gets active.
     *
     * @return the active
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
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
     * Gets data.
     *
     * @return the data
     */
    public Map<String, String> getData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!StringUtils.isBlank(data)) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
                };
                return mapper.readValue(data, typeRef);
            }
        } catch (IOException e) {
            LOG.error("Error reading case data: " + e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(Map<String, String> data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            this.data = "Error parsing data map: " + ex.getMessage();
        }
    }

    /**
     * Gets data map.
     *
     * @return the data map
     */
    public Map<String, String> getDataMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (!StringUtils.isBlank(data)) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
                };
                return mapper.readValue(data, typeRef);
            }
        } catch (IOException e) {
            LOG.error("Error reading case data: " + e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * Sets data map.
     *
     * @param dataMap the data map
     */
    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * Gets process.
     *
     * @return the process
     */
    public Process getProcess() {
        return this.process;
    }

    /**
     * Sets process.
     *
     * @param process the process
     */
    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Gets cases executions.
     *
     * @return the cases executions
     */
    public List<CaseExecution> getCasesExecutions() {
        return this.casesExecutions;
    }

    /**
     * Sets cases executions.
     *
     * @param casesExecutions the cases executions
     */
    public void setCasesExecutions(List<CaseExecution> casesExecutions) {
        this.casesExecutions = casesExecutions;
    }

    /**
     * Add cases execution case execution.
     *
     * @param casesExecution the cases execution
     * @return the case execution
     */
    public CaseExecution addCasesExecution(CaseExecution casesExecution) {
        getCasesExecutions().add(casesExecution);
        casesExecution.setCas(this);

        return casesExecution;
    }

    /**
     * Remove cases execution case execution.
     *
     * @param casesExecution the cases execution
     * @return the case execution
     */
    public CaseExecution removeCasesExecution(CaseExecution casesExecution) {
        getCasesExecutions().remove(casesExecution);
        casesExecution.setCas(null);

        return casesExecution;
    }

    /**
     * Gets client tags.
     *
     * @return the client tags
     */
    public List<ClientTag> getClientTags() {
        return this.clientTags;
    }

    /**
     * Sets client tags.
     *
     * @param clientTags the client tags
     */
    public void setClientTags(List<ClientTag> clientTags) {
        this.clientTags = clientTags;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseId == null) ? 0 : caseId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Case other = (Case) obj;
        if (caseId == null) {
            if (other.caseId != null) {
                return false;
            }
        } else if (!caseId.equals(other.caseId)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
