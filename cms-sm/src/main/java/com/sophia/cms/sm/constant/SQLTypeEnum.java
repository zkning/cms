package com.sophia.cms.sm.constant;

/**
 * sql类型
 */
public enum SQLTypeEnum {
    QUERY(1, "QUERY"),
    CRUD(2, "CRUD");

    private Integer code;
    private String text;

    SQLTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static String getTextByValue(Integer value) {
        if (value == null) {
            return "";
        }
        for (SQLTypeEnum e : SQLTypeEnum.values()) {
            if (value.equals(e.getCode())) {
                return e.getText();
            }
        }
        return "";
    }
}
