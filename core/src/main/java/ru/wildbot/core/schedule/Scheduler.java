package ru.wildbot.core.schedule;

import lombok.*;

import java.util.concurrent.*;

@SuppressWarnings("unused")
public class Scheduler {
    @NonNull private final ScheduledExecutorService executorService;

    public Scheduler(final int poolSize) {
        executorService = Executors.newScheduledThreadPool(poolSize);
    }

    public ScheduledFuture<?> schedule(final Runnable task, final long delay, final TimeUnit timeUnit) {
        return executorService.schedule(task, delay, timeUnit);
    }

    public <V> ScheduledFuture<V> schedule(final Callable<V> task, final long delay, final TimeUnit timeUnit) {
        return executorService.schedule(task, delay, timeUnit);
    }

    public SelfCancellingScheduledFuture scheduleAtFixedRate(final Runnable task, final long initialDelay,
                                                             final long period, final TimeUnit timeUnit,
                                                             final int times) {
        val selfCancellingRunnable = new SelfCancellingScheduledFuture(task, times);
        return selfCancellingRunnable.setScheduledFuture(executorService
                .scheduleAtFixedRate(selfCancellingRunnable, initialDelay, period, timeUnit));
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final long initialDelay, final long period,
                                                  final TimeUnit timeUnit) {
        return executorService.scheduleAtFixedRate(task, initialDelay, period, timeUnit);
    }

    public SelfCancellingScheduledFuture scheduleWithFixedDelay(final Runnable task, final long initialDelay,
                                                                   final long delay, final TimeUnit timeUnit,
                                                                   final int times) {
        val selfCancellingRunnable = new SelfCancellingScheduledFuture(task, times);
        return selfCancellingRunnable.setScheduledFuture(executorService
                .scheduleWithFixedDelay(selfCancellingRunnable, initialDelay, delay, timeUnit));
    }

    @Data
    private static class SelfCancellingScheduledFuture implements Runnable {
        @NonNull private final Runnable runnable;
        @NonNull private int counter;
        @Setter(AccessLevel.PRIVATE) private ScheduledFuture<?> scheduledFuture; // final but set later

        @Override
        public void run() {
            runnable.run();
            if (--counter == 0) scheduledFuture.cancel(false);
        }
    }
}
