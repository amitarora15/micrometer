package com.amit.micrometer.metrics.gauge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/gauge")
public class GaugeOperations {

	@Autowired
	public MeterRegistry registry;

	private List<Integer> list = new ArrayList<>();

	private Map<Integer, Integer> map = new HashMap<>();

	private AtomicInteger integerNumber = new AtomicInteger(0);

	private enum OPER {
		ADD, READ, CLEAR
	};

	@GetMapping
	public String gaugeIntegerAndList() {
		integerNumber = registry.gauge("user.session.gauge", integerNumber);
		list = registry.gauge("myapp.gauge.list", list, l -> l.size());
	//	registry.config().meterFilter(MeterFilter.ignoreTags("ignore.tags")); -> no effect here
		// list = registry.gauge("myapp.gauge.list", list, List::size);
		// list = registry.gaugeCollectionSize("myapp.gauge.list", null, list);
		int random = ThreadLocalRandom.current().nextInt(1, 10);
		System.out.println("Random is " + random);
		if (random % 2 == 0) {
			integerNumber.addAndGet(random * 7);
			list.add(random);
			list.add(random*2);
		} else {
			integerNumber.addAndGet(-1 * random * 8);
			if (list.size() > 0)
				list.remove(0);
		}
		return "GaugedIntegerAndList";
	}

	@GetMapping("/map")
	public String gaugeMap() {

		int random = ThreadLocalRandom.current().nextInt(1, 4);
		OPER operation;
		map.put(random, random*random);
		if (random % 3 == 0) {
			operation = OPER.ADD;
			System.out.println("Operation is ADD in map - " + map);
		} else if (random % 3 == 1) {
			operation = OPER.READ;
			System.out.println("Operation is READ from map - " + map);
		} else {
			System.out.println("Operation is CLEAR map - " + map);
			operation = OPER.CLEAR;
			map.clear();
		}
		Gauge.builder("myapp.gauge.map", map, Map::size).baseUnit("No of acceess")
				.description("Check and records size and tag by access").tag("operation", operation.toString())
				.register(registry);
		return "map";
	}

}
