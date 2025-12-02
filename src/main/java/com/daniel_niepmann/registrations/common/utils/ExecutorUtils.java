package com.daniel_niepmann.registrations.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ExecutorUtils {

    public static ExecutorService getExecutorWithAvailableThreads() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        log.info("ExecutorHelper: using {} threads", availableProcessors);
        return Executors.newFixedThreadPool(availableProcessors);
    }

    public static void waitForFutures(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("ExecutorHelper: exception while waiting for future task: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void waitToShutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            boolean awaitTermination = executorService.awaitTermination(10, TimeUnit.MINUTES);
            if (!awaitTermination) {
                log.warn("Forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.warn("ExecutorHelper: couldn't shut down executor {}", e.getMessage());
        }
    }
}
