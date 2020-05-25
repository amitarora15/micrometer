package com.amit.micrometer.metrics.timer;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timer")
public class TimerController {
	
	@Resource
	private CalculationTask task;
	
	@Resource
	private FunctionalTimerTask functionalTask;

	@GetMapping("/recordRunnable")
	public String recordRunnable() {
		task.recordRunnable();
		return "Runnable";
	}
	
	@GetMapping("/recordCallable")
	public String recordCallable() {
		task.recordCallable();
		return "Callable";
	}
	
	@GetMapping("/recordSampler")
	public String recordSampler() {
		task.recordSampler();
		return "Sampler";
	}
	
	@GetMapping("/functionalTimer1")
	public String functionalTimer1() {
		functionalTask.functionalTimer1();
		return "Functional";
	}
	
	@GetMapping("/functionalTimer2")
	public String functionalTimer2() {
		functionalTask.functionalTimer2();
		return "Functional";
	}
	
}
