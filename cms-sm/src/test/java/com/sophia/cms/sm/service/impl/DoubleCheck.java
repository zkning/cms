package com.sophia.cms.sm.service.impl;

/**
 * Created by lenovo on 2020/3/18.
 */
public class DoubleCheck {

    // 实例静态变量
    private static volatile DoubleCheck doubleCheck;

    /**
     * 无参构造
     */
    private DoubleCheck() {
    }

    /**
     * 双重检查
     *
     * @return
     */
    public static DoubleCheck getInstance() {
        if (null == doubleCheck) {
            synchronized (DoubleCheck.class) {
                if (null == doubleCheck) {

                    /**
                     * 堆分配内存
                     * 将方法区实例变量定义拷贝至堆区并赋初始值
                     * 执行初始化方法
                     * 在栈区定义DoubleCheck引用变量doubleCheck，并实例对象地址赋值给它
                     * 不加volatile会存在线程安全问题（volatile可以防止指令重排序）
                     */
                    doubleCheck = new DoubleCheck();
                }
            }
        }
        return doubleCheck;
    }

    public static class LHMode {
        private static LHMode lhMode = new LHMode();

        private LHMode() {
        }

        public static LHMode getInstance() {
            return lhMode;
        }
    }

    public static class EHModel {
        private static EHModel ehModel;

        private EHModel() {
        }

        private static EHModel getInstance() {
            return ehModel = new EHModel();
        }
    }
}

/**
 * 枚举
 */
class User {

    // 私有构造方法
    private User() {
    }

    // 枚举单例
    static enum SingleEnum {

        // 创建枚举实例，天生为单例
        INSTANCE;

        // 待初始化对象
        private User user;

        private SingleEnum() {

            // 通过构造初始化
            user = new User();
        }

        // 通过get方法获取实例
        public User getUser(){
            return user;
        }
    }

    /**
     * 暴露获取实例方法
     * @return
     */
    public static User getInstance() {
       return SingleEnum.INSTANCE.getUser();
    }

}
