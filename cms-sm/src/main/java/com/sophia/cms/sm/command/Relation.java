package com.sophia.cms.sm.command;

/**
 * Created by lenovo on 2020/3/29.
 */
public class Relation {


    static class Father {
        public int money = 1;

//        public Father() {
//            money = 2;
//            System.out.println("父类构造");
//            showMoney();
//        }

        public void showMoney() {
            System.out.println("i am father money : " + money);
        }
    }

    static class Son extends Father{
        public  int money = 3;

//        public Son() {
//            money = 4;
//            showMoney();
//        }

        // 虚方法
        @Override
        public void showMoney() {
            System.out.println("i am son money :" + money);
        }
    }

    static class Grandson extends Son{
        public  int money = 5;

//        public Son() {
//            money = 4;
//            showMoney();
//        }

        // 虚方法
        @Override
        public void showMoney() {
            System.out.println("i am son money :" + money);
        }
    }

    public static void main(String[] args) {
        Son son = new Grandson();
        System.out.println("money :" + son.money);

    }
}
