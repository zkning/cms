package com.sophia.cms.sm.constant;

/**
 * sql类型
 */
public enum SqlTypeEnum {
    QUERY(1, "QUERY"),
    CRUD(2, "CRUD");

    private Integer code;
    private String text;

    SqlTypeEnum(Integer code, String text) {
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
        for (SqlTypeEnum e : SqlTypeEnum.values()) {
            if (value.equals(e.getCode())) {
                return e.getText();
            }
        }
        return "";
    }
}
