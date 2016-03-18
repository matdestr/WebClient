package be.kdg.kandoe.backend.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfig implements AsyncConfigurer {

    @Bean
    public TaskScheduler taskScheduler(){
        return new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    }


    @Override
    public Executor getAsyncExecutor() {
        //return new SimpleAsyncTaskExecutor();
        return new ScheduledThreadPoolExecutor(8);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
