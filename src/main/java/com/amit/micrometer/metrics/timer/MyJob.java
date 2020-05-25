package com.amit.micrometer.metrics.timer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;

@Component
public class MyJob {

	
	@Scheduled(fixedDelay = 300, initialDelay = 1000)
	@Timed(value = "schedule.job1", histogram = true, percentiles = {0.3, 0.5})
	public void job1() {
		System.out.println("Executing job1");
		try {
			int sleepTime = ThreadLocalRandom.current().nextInt(20);
			TimeUnit.SECONDS.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Completing job1");
	}

	
	@Scheduled(fixedDelay = 100, initialDelay = 5000)
	@Timed(value = "schedule.job2", longTask = true)
	public void job2() {
		System.out.println("Executing job2");
		try {
			int sleepTime = ThreadLocalRandom.current().nextInt(5);
			TimeUnit.MINUTES.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Completing job2");
	}

}