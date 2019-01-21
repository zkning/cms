package com.sophia.cms.framework.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * GUID 
 * @author zkning
 */
public class GUID {
	
	/**
	 * 生成32位ID
	 * @return
	 */
	public static String nextId(){
		return  UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 生成编码
	 * @return
	 */
	public static synchronized String createCode(String prefix){
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMddhhmmss");

		//产生1000-9999的随机数
		int random = (int)(Math.random() * (9999 - 1000 + 1)) + 1000;
		return ( null != prefix ? prefix : "" ) + sdf.format(new Date()) + random;
	}
	
	public static void main(String[] args) {
		System.out.println(createCode("SG"));
	}
}
