package com.amit.micrometer.metrics.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class FunctionalTimerTask {

	
	@Autowired
	public MeterRegistry registry;

	class Timer {
		
		Map<Integer, String> map = new HashMap<>();

		double timeElapsed = 0;
		

		public void add() {
			double time = ThreadLocalRandom.current().nextDouble(100.0);
			int key = ThreadLocalRandom.current().nextInt(100);
			map.put(key, "a" + key);
			timeElapsed += time;
		}

		public Long getCount() {
			return Long.valueOf(map.size());
		}

		public Double getTimeElapsed() {
			return timeElapsed;
		}

	}

	public void functionalTimer1() {
		Timer timer = new Timer();
		timer.add();
		timer.add();
		FunctionTimer.builder("functional.timer1", timer, t -> timer.getCount(), t -> timer.getTimeElapsed(),
				TimeUnit.SECONDS).register(registry);
	}
	
	public void functionalTimer2() {
		Timer timer = new Timer();
		timer.add();
		timer.add();
		timer.add();
		timer.add();
		registry.more().timer("functional.timer2", null, timer, t -> timer.getCount(), t -> timer.getTimeElapsed(),
				TimeUnit.SECONDS);
	}

}
