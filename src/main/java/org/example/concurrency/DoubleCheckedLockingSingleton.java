package org.example.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/***
 * Source: https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
 */
public class DoubleCheckedLockingSingleton {

    private volatile SomeClass singleton;

    public SomeClass getInstance() {
        if (singleton == null) {
            synchronized (this) {
                if (singleton == null) {
                    singleton = new SomeClass();
                }
            }
        }
        return singleton;
    }

    private static class SomeClass {
        private SomeClass() {
        }
    }

    private static <T> T getFuture(Future<T> f) {
        try {
            return f.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int taskCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<SomeClass>> tasks = new ArrayList<>(taskCount);
        var dcl = new DoubleCheckedLockingSingleton();
        for (int i = 0; i < taskCount; i++) {
            tasks.add(dcl::getInstance);
        }
        var futures = executorService.invokeAll(tasks);
        var result = futures.stream().map(DoubleCheckedLockingSingleton::getFuture).distinct().toList();
        assert result.size() == 1;
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }
}