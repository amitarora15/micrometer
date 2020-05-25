package com.amit.micrometer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Configuration
public class HealthMetricsConfiguration {

	private CompositeHealthIndicator healthIndicator;

	public HealthMetricsConfiguration(HealthAggregator healthAggregator, List<HealthIndicator> healthIndicators,
			MeterRegistry registry) {

		Map<String, HealthIndicator> map = new HashMap<>();
		for (Integer i = 0; i < healthIndicators.size(); i++) {
			map.put(i.toString(), healthIndicators.get(i));
		}
		healthIndicator = new CompositeHealthIndicator(healthAggregator, map);

		// presumes there is a common tag applied elsewhere that adds tags for app, etc.
		registry.gauge("micrometer.health", null, healthIndicator, health -> {
			Status status = health.health().getStatus();
			switch (status.getCode()) {
			case "UP":
				return 3;
			case "OUT_OF_SERVICE":
				return 2;
			case "DOWN":
				return 1;
			case "UNKNOWN":
			default:
				return 0;
			}
		});

		registry.gauge("micrometer.health.new", Tags.of("status", "up"), healthIndicator,
				health -> "UP".equals(healthIndicator.health().getStatus().getCode()) ? 1 : 0);

		registry.gauge("micrometer.health.new", Tags.of("status", "down"), healthIndicator,
				health -> "DOWN".equals(healthIndicator.health().getStatus().getCode()) ? 1 : 0);
	}
}
