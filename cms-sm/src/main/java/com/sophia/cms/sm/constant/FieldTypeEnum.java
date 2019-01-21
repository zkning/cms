package com.sophia.cms.sm.constant;

/**
 * 控件
 * @author zkning
 */
public enum FieldTypeEnum {
	 TEXT("TEXT", "文本框"),
	 SELECT("DROPDOWN", "下拉框"),
	 CHECKBOX("CHECKBOX", "单选框"),
	 DATEPICKER("DATETIMEPICKER", "日期"),
	 TEXTAREA("TEXTAREA", "文本域");

	/**
	 * 成员变量
 	 */
    private String value;
    private String text;

	/**
	 * 构造方法
 	 */
    private FieldTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

	/**
	 * 普通方法
 	 */
    public static String getName(int value) {
        for (FieldTypeEnum c : FieldTypeEnum.values()) {
            if (c.getValue().equals(value)) {
                return c.value;
            }
        }
        return null;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

    
}
