package com.sophia.cms.framework.constants;

/**
 * 状态码
 * @author zkning
 */
public enum StatusCodeEnum {
	
	SUCCESS(0,"处理成功"),
	
	SYSTEM_ERROR(1,"处理失败"),

	INVALID_REQUEST(2, "无效请求"),
	
	SERVICE_UNACCESSABLE(3,"服务器异常"),
	
	NEED_LOGIN(5,"未登录"),
	
	INSUFFICIENT_PRIVILEGES(6,"权限不足"),

	SESSION_EXPIRED(7, "Session失效"),
	
	INVALID_ARGS(8,"参数异常"),
	
	USER_CALL_TIMEOUT(100,"用户请求超时"),

	USER_CALL_LIMITED(101, "用户请求受限"),

	SESSION_CALL_LIMITED(102, "Session请求受限"),

	IO_EXCEPTION(103, "IO异常"),
	
	SYSTEM_DATA_ERROR(104,"数据异常");
	
	public Integer code;
	public String message;

	private StatusCodeEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
