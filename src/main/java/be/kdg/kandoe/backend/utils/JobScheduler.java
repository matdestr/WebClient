package be.kdg.kandoe.backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * Wrapper class for the Spring framework's {@link TaskScheduler}.
 */
@Component
public class JobScheduler {
    private final TaskScheduler taskScheduler;

    /*public JobScheduler() {
        this.taskScheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    }*/

    @Autowired
    public JobScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Async
    public ScheduledFuture<?> scheduleJob(Runnable runnable, Date date) {
        return taskScheduler.schedule(runnable, date);
    }
}
