package com.fast.admin.framework.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by Kim on 2015/9/21.
 */
public class RestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public RestException() {
    }

    public RestException(HttpStatus status) {
        this.status = status;
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
