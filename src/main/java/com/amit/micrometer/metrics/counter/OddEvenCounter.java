package com.amit.micrometer.metrics.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/counter")
public class OddEvenCounter {

	@Autowired
	public MeterRegistry registry;

	@GetMapping("/{id}")
	public String countEvenOddHits(@PathVariable Long id) {
		// registry.config().commonTags("env", "test"); - Common tags not to be defined
		// at this level
		Counter oddCounter = registry.counter("odd.even.counter", "type", "odd", "new.ignore.tags", "1");
		Counter evenCounter = registry.counter("odd.even.counter", "type", "even", "new.ignore.tags", "2");

		if (id % 2 == 0) {
			evenCounter.increment();
		} else {
			oddCounter.increment();
		}
		return "Counted";
	}

	

}
