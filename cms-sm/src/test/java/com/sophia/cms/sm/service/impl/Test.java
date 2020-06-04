package com.sophia.cms.sm.service.impl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lenovo on 2019/8/25.
 */
public class Test {

    public static void main(String[] args) throws Exception {
/*
        char ASCII = 'S';
        System.out.println(Integer.valueOf(ASCII));

        String str = "12136464ffD我们大发达了就发大水反垃圾fdaljFFFDJLAFJDAL";
        char[] array = str.toCharArray();
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("a", 0);
        countMap.put("b", 0);
        countMap.put("c", 0);
        countMap.put("index", 0);
        Test.count(array, countMap);
        System.out.println(countMap.toString());
        Test.write(str);

        // 字符串反转
        String a = "abcdefg";
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("1", 1);
        treeMap.put("3", 1);
        treeMap.put("2", 1);
        treeMap.forEach((c, b) -> {
            System.out.println(c);
        });

        Method method = Test.class.getDeclaredMethod("",String.class);
        method.invoke(null,"");

        CyclicBarrier cyclicBarrier = null;
        cyclicBarrier.await();

        HashMap hashMap= new HashMap();
        hashMap.put("",null);
        hashMap.get("");

        Thread thread = new Thread();
        thread.yield();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(null);

        AtomicInteger atomicInteger = null;

        ReentrantLock reentrantLock;

        List<String> list = new ArrayList<>();
        list.add("");
        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern().equals(str2));

        System.out.println(100 % 12);
        int[] s = {9, 5, 3, 1, 10, 14, 8, 2, 6, 7, 9};
        Test.QS(s,0,s.length - 1);
        Test.CR(s);
        for (int i : s) {
            System.out.print(i + ",");
        }
        CyclicBarrier cyclicBarrier;
        Test.sum();
*/

        Test.RXJava();
    }

    public static void sum () throws InterruptedException {

        AtomicInteger sum = new AtomicInteger();
        List<Thread> threadList = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            final int cur = i;

            Thread thread = new Thread(() -> {
                for (int j = cur * 100 + 1; j <= cur * 100 + 100; j++) {
                    sum.addAndGet(j);
                }
            });
            thread.start();
            threadList.add(thread);
        }

        for (Thread thread : threadList) {
            //join方法，那么当前线程就会被阻塞，直到thread线程执行完毕
            thread.join();
        }
        System.out.println(sum.get());
        Future<String> rs = null;
    }

    public static void RXJava() {

        for (int i = 0; i < 200; i++) {
            System.out.println(i % 3);
        }
    }

    public static void Pool(int[] array) {

        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.DAYS,
                new ArrayBlockingQueue(10));

        threadPoolExecutor.execute(null);
    }

    public static void lock() throws InterruptedException {
        Map<String,String> map  = new ConcurrentHashMap<>();
        map.put("","");
        new Hashtable<>().put(null,null);

    }


    public static void CR(int[] array) {
        for (int i = 1; i < array.length; i++) {

            // 从下标为1开始往前遍历
            for (int j = i; j > 0; j--) {

                // 当前位大于前位就替换双方值
                if (array[j] > array[j - 1]) {
                    int T = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = T;
                } else {
                    break;
                }
            }
        }
    }

    public static void XZ(int[] array) {
        for (int i = 0; i < array.length; i++) {

            // 以当前下标后一位开始遍历
            for (int j = i + 1; j < array.length; j++) {

                // 后一位大于前一位值则替换双方值
                if (array[i] < array[j]) {
                    int T = array[i];
                    array[i] = array[j];
                    array[j] = T;
                }
            }
        }
    }


    public static void MP(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length - i - 1; j++) { // -i 每次冒泡都会产生一个最大移动至数组最末位

                // 当前位大于后一位则替换双方值
                if (array[j] > array[j + 1]) {
                    int T = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = T;
                }
            }
        }
    }


    /**
     * abcdefghijklmn
     * 快速排序
     */
    public static int partion(int[] array, int L, int R) {
        int T = array[L];
        while (L < R) {

            // 右边大于T ，下标前移
            while (L < R && array[R] >= T)
                R--;

            // 右边小于T则右边值赋值给左边位置
            array[L] = array[R];
            while (L < R && array[L] <= T)
                L++;
            array[R] = array[L];
        }

        // 左右下标重合则将值插入当前下标
        array[L] = T;
        return L;
    }


    public static void QS(int[] array, int L, int R) {
        if (L < R) {
            int p = partion(array, L, R);
            QS(array, L, p - 1);
            QS(array, p + 1, R);
        }
    }


    /**
     * 写出文件
     *
     * @param content
     * @throws Exception
     */
    public static void write(String content) throws Exception {
        char[] array = content.toCharArray();
        String str = "e:/a.txt";
        FileOutputStream fos = new FileOutputStream(new File(str));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
        for (int index = 0; index < array.length; index++) {
            bufferedOutputStream.write(String.valueOf(array[index]).getBytes());
            bufferedOutputStream.write("\r\n".getBytes());// 写入一个换行
        }
        System.out.println("文件已写入" + str);
        bufferedOutputStream.close();
    }

    /**
     * 递归函数
     *
     * @param array
     * @param countMap
     */
    public static void count(char[] array, Map<String, Integer> countMap) {
        int idx = countMap.get("index");
        char c = array[idx];
        if (c >= '0' && c < '9') {
            countMap.put("a", countMap.get("a") + 1);
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            countMap.put("b", countMap.get("b") + 1);
        } else {
            countMap.put("c", countMap.get("c") + 1);
        }
        Integer index = countMap.get("index") + 1;
        countMap.put("index", index);
        if (index < array.length) {
            count(array, countMap);
        }
    }
}

