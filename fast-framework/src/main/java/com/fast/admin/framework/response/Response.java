package com.fast.admin.framework.response;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.constants.StatusCodeEnum;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 响应
 * @author zkning
 */
public class Response<T> implements Serializable{
	private static final long serialVersionUID = 665474083431171685L;
	@ApiModelProperty(value = "响应码")
	private Integer code;

	@ApiModelProperty(value = "响应消息")
	private String message;

	@ApiModelProperty(value = "响应结果")
	private T result;

	public Response(){}

	public Response(Integer code,String message){
		this.code = code;
		this.message = message;
	}
	
	public Response(Integer code,String message,T result){
		this.code = code;
		this.message = message;
		this.result = result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response getInstance(Integer code,String message,Object result){
		return new Response(code, message, result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response getInstance(Integer code,String message){
		return new Response(code, message, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response SUCCESS(){
		return new Response(StatusCodeEnum.SUCCESS.code, StatusCodeEnum.SUCCESS.message, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response SUCCESS(Object result){
		return new Response(StatusCodeEnum.SUCCESS.code, StatusCodeEnum.SUCCESS.message, result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response FAILURE(){
		return new Response(StatusCodeEnum.SYSTEM_ERROR.code, StatusCodeEnum.SYSTEM_ERROR.message, null);
	}

	public static Response FAILURE(String message){
		return new Response(StatusCodeEnum.SYSTEM_ERROR.code, message, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response FAILURE(Object result){
		return new Response(StatusCodeEnum.SYSTEM_ERROR.code, StatusCodeEnum.SYSTEM_ERROR.message, result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response FAILURE(Exception ex){
		return new Response(StatusCodeEnum.SYSTEM_ERROR.code, ex.getMessage(), null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response FAILURE(Integer code ,String message){
		return new Response(code, message, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Response SYSTEMEXCEPTION(Exception ex){
		return new Response(StatusCodeEnum.SYSTEM_ERROR.code, StatusCodeEnum.SYSTEM_ERROR.message, ex);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	/**
	 * 检查是否成功
	 */
	public Boolean checkSuccess() {
		return StatusCodeEnum.SUCCESS.code.equals(this.code);
	}

	/**
	 * toJsonString
	 */
	public String toJsonString() {
		return JSONObject.toJSONString(this);
	}
}
