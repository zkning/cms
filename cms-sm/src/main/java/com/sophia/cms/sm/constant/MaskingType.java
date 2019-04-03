package com.sophia.cms.sm.constant;

public enum MaskingType {

    CHINESE_NAME(1, "中文名"),
    ID_CARD(2, "身份证"),
    MOBILE_PHONE(3, "手机号"),
    EMAIL(4, "电子邮件"),
    BANK_CARD(5, "银行卡");

    private Integer code;
    private String value;

    private MaskingType(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static MaskingType get(Integer value) {
        if (value == null) {
            return null;
        }
        for (MaskingType e : MaskingType.values()) {
            if (value.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
