package com.fast.admin.rbac.constants;

/**
 * 资源类型
 * Created by ningzuokun on 2018/3/21.
 */
public enum ResourceTypeEnum {
    MENU(1, "菜单"),
    FUNC(2, "功能按钮"),
    SERV(3, "服务");

    private Integer code;
    private String value;

    ResourceTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static ResourceTypeEnum getInstance(Integer code) {
        for (ResourceTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
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
