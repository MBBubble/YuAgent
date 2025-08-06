package com.rain.yuagent.demo.test;

/**
 * 单例模式——懒汉式+双重锁+volatitle防止指令重排
 */
public class SingletonDemo {

    //volatitle 禁止指令重排
    private static volatile SingletonDemo singletonDemo;

    private static Object lock = new Object();

    // 私有的构造函数
    private SingletonDemo() {}

    // 提供公共的方法用于获取单例对象
    public static SingletonDemo getInstance() {
        if (singletonDemo == null) {
            synchronized (SingletonDemo.class) {
                if (singletonDemo == null) {
                    singletonDemo = new SingletonDemo();
                }
            }
        }
        return singletonDemo;
    }
}
