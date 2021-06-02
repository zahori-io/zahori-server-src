package io.zahori.server.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "client_retries")
public class ClientRetries implements Serializable {

    @EmbeddedId
    private ClientRetriesPK id;

    public ClientRetriesPK getId() {
        return id;
    }

    public void setId(ClientRetriesPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClientRetries that = (ClientRetries) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientRetries{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}