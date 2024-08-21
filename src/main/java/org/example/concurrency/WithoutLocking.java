package org.example.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class WithoutLocking {

    private final AtomicBoolean locked = new AtomicBoolean(false);

    private volatile int counter = 100;

    private void start() {
        Thread t1 = new Thread(() -> print(c -> c % 2 == 0, "Hi, I'm " + Thread.currentThread().getName()), "1");
        Thread t2 = new Thread(() -> print(c -> c % 2 != 0, "Hi, I'm " + Thread.currentThread().getName()), "2");
        t1.start();
        t2.start();
    }

    private void print(Predicate<Integer> test, String message) {
        while (counter > 0) {
            lock();
            if (test.test(counter)) {
                if (counter > 0) {
                    counter--;
                    System.out.println(message + " changing to " + counter);
                }
            }
            unlock();
        }
    }

    private void lock() {
        while (!locked.compareAndSet(false, true)) {
//            System.out.println("waiting in " + Thread.currentThread().getName());
        }
    }

    private void unlock() {
        locked.set(false);
    }

    public static void main(String[] args) {
        WithoutLocking wl = new WithoutLocking();
        wl.start();
    }
}
