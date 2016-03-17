package be.kdg.kandoe.backend.utils;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Wrapper class
 */

@Component
public class JobScheduler {
    private TaskScheduler taskScheduler;

    public JobScheduler() {
        this.taskScheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    }

    @Async
    public void scheduleJob(Runnable runnable, Date date) {
        taskScheduler.schedule(runnable, date);
    }
}
