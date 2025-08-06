package com.rain.yuagent.demo.test;

public class PrintOddEven {

    private static Integer count = 0;
    private static final Object lock = new Object();

    public static void main(String[] args) {

        Thread jishu = new Thread(new Runnable() {
            @Override
            public void run() {
                while (count < 100) {
                    synchronized (lock) {
                        if (count % 2 == 0) {
                            count++;
                            System.out.println(Thread.currentThread().getName() + count);
                            lock.notify();
                        } else {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, "jishu");

        Thread oushu = new Thread(new Runnable() {
            @Override
            public void run() {
                while (count < 100) {
                    synchronized (lock) {
                        if (count % 2 == 1) {
                            count++;
                            System.out.println(Thread.currentThread().getName() + count);
                            lock.notify();
                        } else {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }, "oushu");

        jishu.start();
        oushu.start();
    }

}
