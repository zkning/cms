package com.sophia.cms.framework.exception;

import com.sophia.cms.framework.constants.StatusCodeEnum;
import lombok.Data;

/**
 * @author zkning
 */
@Data
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String message;

    public ServiceException(String message) {
        super(message);
        this.code = StatusCodeEnum.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = StatusCodeEnum.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(StatusCodeEnum statusCodeEnum) {
        super(statusCodeEnum.getMessage());
        this.code = statusCodeEnum.getCode();
        this.message = statusCodeEnum.getMessage();
    }
}
