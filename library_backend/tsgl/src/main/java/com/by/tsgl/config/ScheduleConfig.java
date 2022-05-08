package com.by.tsgl.config;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/15 15:32
 */
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));

    }
}
