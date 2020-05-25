package com.amit.micrometer.metrics.timer;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

@Component
public class CalculationTask {

	@Autowired
	public MeterRegistry registry;
	
	private Timer myRunnableTimer;

	private Timer myCallableTimer;
	
	@PostConstruct
	public void init() {
		myRunnableTimer = registry.timer("timer.task.runnable.calcuation", "type", "sleep.runnable");
		myCallableTimer = Timer.builder("timer.task.callable.calcuation").description("Timer with sleep in callable")
				.tag("type", "sleep.callable").publishPercentileHistogram().publishPercentiles(0.3, 0.5, 0.9)
				.sla(Duration.ofSeconds(40)).minimumExpectedValue(Duration.ofSeconds(10))
				.maximumExpectedValue(Duration.ofSeconds(70)).register(registry);
	}

	class MyRunnable implements Runnable {

		@Override
		public void run() {
			long nanoStart = registry.config().clock().monotonicTime();
			int sleepTime = ThreadLocalRandom.current().nextInt(50);
			try {
				System.out.println("Sleep time runnable " + sleepTime);
				TimeUnit.SECONDS.sleep(sleepTime); // Do some work
				System.out.println("Base time unit " + myRunnableTimer.baseTimeUnit());
				System.out.println("Time Meter Id " + myRunnableTimer.getId());
				System.out.println("No of time stop has been called " + myRunnableTimer.count());
				System.out.println("In timer.task.record.calcuation of runnable");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				myRunnableTimer.record(registry.config().clock().monotonicTime() - nanoStart, TimeUnit.NANOSECONDS);
			}

		}

	}

	class MyCallable implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			long nanoStart = System.nanoTime();
			int sleepTime = ThreadLocalRandom.current().nextInt(40);
			try {
				System.out.println("Sleep time callable " + sleepTime);
				TimeUnit.SECONDS.sleep(sleepTime); // Do some work
				System.out.println("In timer.task.record.calcuation of callable");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				myCallableTimer.record(System.nanoTime() - nanoStart, TimeUnit.NANOSECONDS);
			}
			return 1;
		}

	}

	public void recordRunnable() {
		new Thread(new MyRunnable()).start();
		myRunnableTimer.record(Duration.ofMillis(100)); // Adding post processing time
	}

	public void recordCallable() {
		FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
		Thread t = new Thread(futureTask);
		t.start();
		myCallableTimer.record(new Runnable() {

			@Override
			public void run() {
				System.out.println("Adding extra time");// Do some extra work
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void recordSampler() {
		Sample sample = Timer.start(registry);
		int sleepTime = ThreadLocalRandom.current().nextInt(30);
		try {
			System.out.println("Sleep time sampler " + sleepTime);
			TimeUnit.SECONDS.sleep(sleepTime); // Do some work
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(sleepTime % 2 == 0 )
				sample.stop(registry.timer("sample.timer", "type", "even"));
			else
				sample.stop(registry.timer("sample.timer", "type", "odd"));
		}
	}

}
