package org.example.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class TwoThreadsOneCounter {

    private static final AtomicInteger COUNTER = new AtomicInteger(100);

    public static void main(String[] args) {
        Thread biba = new Thread(() -> print(c -> c % 2 == 0, "Hi, I'm " + Thread.currentThread().getName()), "Biba");
        Thread boba = new Thread(() -> print(c -> c % 2 != 0, "Hi, I'm " + Thread.currentThread().getName()), "Boba");
        boba.start();
        biba.start();
    }

    private static void print(Predicate<Integer> test, String message) {
        while (COUNTER.get() > 0) {
            if (test.test(COUNTER.get())) {
                System.out.println(message);
                COUNTER.getAndDecrement();
            }
        }
    }
}
