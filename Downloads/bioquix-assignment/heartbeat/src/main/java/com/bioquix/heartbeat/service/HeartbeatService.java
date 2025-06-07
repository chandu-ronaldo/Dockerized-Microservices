package com.bioquix.heartbeat.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
public class HeartbeatService {
	
	  private final RestTemplate restTemplate = new RestTemplate();
	    private final String apiHealthUrl = "http://api-proxy:8080/weather/health";
	    private final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

	    @Scheduled(fixedRate = 5000)
	    public void pingApiProxy() {
	        try {
	            ResponseEntity<String> response = restTemplate.getForEntity(apiHealthUrl, String.class);
	            logger.info("[HEARTBEAT OK] " + response.getBody());
	        } catch (Exception e) {
	            logger.error("[HEARTBEAT FAIL] API Proxy not responding.");
	        }
	    }

	    public void logStatus(String status) {
	        logger.info("[STATUS RECEIVED FROM API PROXY] " + status);
	    }

}
