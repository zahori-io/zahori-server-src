package io.zahori.server.model;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class HttpResponse {

    protected int statusCode;
    protected HttpStatus status;
    protected String message;
    protected Page<?> data;

    public HttpResponse() {
        super();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page<?> getData() {
        return data;
    }

    public void setData(Page<?> data) {
        this.data = data;
    }

}
