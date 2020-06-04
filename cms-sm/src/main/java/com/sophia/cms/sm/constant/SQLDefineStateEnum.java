package com.sophia.cms.sm.constant;

/**
 * Created by lenovo on 2018/4/15.
 */
public enum SQLDefineStateEnum {

    ISSUE(1, "已发布"),
    UN_ISSUE(0, "待发布");
    private Integer code;
    private String text;
    SQLDefineStateEnum(Integer code, String text) {
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
        for (SQLDefineStateEnum e : SQLDefineStateEnum.values()) {
            if (value.equals(e.getCode())) {
                return e.getText();
            }
        }
        return "";
    }
}