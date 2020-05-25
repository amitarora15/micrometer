package com.amit.micrometer.metrics.counter;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/counter")
public class AccessAndExceptionCounter {
	
	@Autowired
	public MeterRegistry registry;
	
	@GetMapping("/access")
	public String exceptionCounter() {

		try {
			int random = ThreadLocalRandom.current().nextInt(1, 4);
			if (random % 3 == 1) {
				throw new IllegalArgumentException("Number is very less than 3");
			} else if (random % 3 == 2) {
				throw new RuntimeException("Number is less than 3");
			} else {
				Counter c = Counter.builder("user.access.counter").baseUnit("number").description("User Access count")
						.register(registry);
				System.out.println("Counter is equal to 3");
				c.increment();
			}
		} catch (IllegalArgumentException e) {
			logExceptionMetric(e.getClass().getName(), "LOW");
			System.err.println(e.getMessage());
		} catch (RuntimeException e) {
			logExceptionMetric(e.getClass().getName(), "HIGH");
			System.err.println(e.getMessage());
		}
		return "session";
	}

	private void logExceptionMetric(String exceptionName, String severity) {
		Counter c = Counter.builder("app.exceptions").baseUnit("number").description("Application Exceptions")
				.tag("exceptionName", exceptionName).tag("level", severity).register(registry);
		c.increment();
	}

}
