package com.amit.micrometer.metrics.summary;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/summary")
public class DistributionSummaryContoller {

	@Resource
	public MeterRegistry registry;

	@GetMapping
	public String getSummary() {
		DistributionSummary summary = DistributionSummary.builder("request.size").baseUnit("bytes").maximumExpectedValue(60L).minimumExpectedValue(1L)
				.publishPercentileHistogram().sla(20, 50).scale(100).publishPercentiles(0.95, 0.99).register(registry);
		summary.record(ThreadLocalRandom.current().nextDouble(0.6));
		summary.takeSnapshot().outputSummary(System.out, 100);
		return "Summary";
	}

}
