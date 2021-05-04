package io.zahori.server.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * The type Evidence type.
 */
@Entity
@Table(name = "evidence_cases")
public class EvidenceCase {
	  private static final long serialVersionUID = 1L;

	    @Id
	    @Column(name = "evi_case_id")
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long eviCaseId;

	    private Boolean active;

	    @Column(name = "name")
	    private String name;

	    @Column(name = "order")
	    private Long order;
	    
	    // bi-directional many-to-many association to Client
	    @JsonBackReference(value = "clients")
	    @ManyToMany
	    @JoinTable(name = "client_evidence_cases", joinColumns = { @JoinColumn(name = "evi_case_id") }, inverseJoinColumns = { @JoinColumn(name = "client_id") })
	    private Set<Client> clients;

	    public EvidenceCase() {}

		public Long getEviCaseId() {
			return eviCaseId;
		}

		public void setEviCaseId(Long eviCaseId) {
			this.eviCaseId = eviCaseId;
		}

		public Boolean getActive() {
			return active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getOrder() {
			return order;
		}

		public void setOrder(Long order) {
			this.order = order;
		}

		public Set<Client> getClients() {
			return clients;
		}

		public void setClients(Set<Client> clients) {
			this.clients = clients;
		}
}
