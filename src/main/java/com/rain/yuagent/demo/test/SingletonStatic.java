package com.rain.yuagent.demo.test;

/**
 * 单利模式——静态内部类的方式
 */
public class SingletonStatic {

    //私有的构造器
    private SingletonStatic(){}

    public static class SingletonDemoHolder {
        private static final SingletonStatic INSTANCE = new SingletonStatic();
    }

    // 对外提供方法用于获取实例对象
    public static SingletonStatic getInstance(){
        return SingletonDemoHolder.INSTANCE;
    }

}
