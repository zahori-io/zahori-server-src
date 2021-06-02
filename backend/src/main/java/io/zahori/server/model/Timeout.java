package io.zahori.server.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The persistent class for the timeouts database table.
 * 
 */
@Entity
@Table(name = "timeouts")
public class Timeout implements Serializable {

    private static final long serialVersionUID = -8108674227561269699L;

    @Id
    @Column(name = "timeout_id")
    private Long timeoutId;

    private Boolean active;

    //bi-directional many-to-many association to Client
    @JsonBackReference(value = "client")
    @ManyToMany
    @JoinTable(name = "client_timeouts", joinColumns = { @JoinColumn(name = "timeout_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
    private Set<Client> clients;

    //bi-directional many-to-one association to Configuration
    @JsonBackReference(value = "configurations")
    @OneToMany(mappedBy = "timeout")
    private Set<Configuration> configurations;

    public Timeout() {
    }

    public Long getTimeoutId() {
        return this.timeoutId;
    }

    public void setTimeoutId(Long timeoutId) {
        this.timeoutId = timeoutId;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Set<Configuration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }

}