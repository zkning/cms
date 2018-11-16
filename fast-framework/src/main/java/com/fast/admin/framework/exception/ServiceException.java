package com.fast.admin.framework.exception;

/**
 * Created by Kim on 2015/9/21.
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException() {
    }
    
    public ServiceException(String message) {
        super(" Service:"+message);
    }
    public ServiceException(Object obj, String message) {
        this(obj.getClass().getName() + message);
    }

}
