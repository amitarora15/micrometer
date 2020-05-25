package com.amit.micrometer.metrics.counter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/counter")
public class CacheFunctionalCounter {

	@Autowired
	public MeterRegistry registry;

	private Map<Integer, Integer> nameCache = new HashMap<>();

	private AtomicInteger integerCounter = new AtomicInteger(1);

	@GetMapping("/cache")
	public String cacheStats() {
		populateNameCache();
		registry.more().counter("name.cache.hits", null, nameCache, t -> t.size());
		FunctionCounter.builder("integer.cache.hits", integerCounter, t -> t.get()).baseUnit("elements")
				.register(registry);
		return "Cache";
	}

	public void populateNameCache() {
		int random = ThreadLocalRandom.current().nextInt(100);
		int itr = ThreadLocalRandom.current().nextInt(3);
		for (int i = 0; i < itr; i++) {
			nameCache.put(random, random + 10);
			integerCounter.incrementAndGet();
		}
	}

}
