package com.api.client.api.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CronJob {
	// run every 2 mins
	// 1000 ms * 60 s * 2 mins
	@Scheduled(fixedRate = 1000*60*2)
	public void fixedRateSch() { 
		System.out.println("000");
	}
}
