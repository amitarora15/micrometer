package com.amit.micrometer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;

@Configuration
@EnableAspectJAutoProxy
public class MicroMeterConfiguration {

	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsCommonTag(@Value("${application.region}") String region) {
		return registry -> registry.config().commonTags("region", region);
	}

	@Bean
	MeterFilter denyJvmFilter() {
		return MeterFilter.denyNameStartsWith("tomcat");
	}

	@Bean
	MeterFilter renameTagFilter() {
		return MeterFilter.renameTag("app.exceptions", "exceptionName", "exception.name");
	}

	@Bean
	MeterFilter replaceTagValuesFilter() {
		return MeterFilter.replaceTagValues("level", t -> t.toLowerCase(), "LOW");
	}

	@Bean
	MeterFilter ignoreTagFilter() {
		return MeterFilter.ignoreTags("new.ignore.tags");
	}

	@Bean
	MeterFilter commonTagFilter() {
		List<Tag> tags = new ArrayList<>();
		tags.add(new ImmutableTag("cloud", "GCP"));
		return MeterFilter.commonTags(tags);
	}

	@Bean
	MeterFilter denyFilter() {

		return new MeterFilter() {

			@Override
			public MeterFilterReply accept(Meter.Id id) {
				if (id.getName().contains("cpu")) {
					return MeterFilterReply.DENY;
				}
				return MeterFilterReply.NEUTRAL;
			}
		};
	}

	@Bean
	MeterFilter transformFilter() {

		return new MeterFilter() {

			@Override
			public Meter.Id map(Meter.Id id) {
				if (id.getName().contains("counter")) {
					return id.withName("extra." + id.getName()).withBaseUnit("counter count")
							.withTag(new ImmutableTag("counter.test", "0"));
				}
				return id;
			}
		};
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

}
