package com.bioquix.api_proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
	

    private final RestTemplate restTemplate = new RestTemplate();
    private final String api1 = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=API_KEY1";
    private final String api2 = "http://api.weatherapi.com/v1/current.json?key=API_KEY2&q=%s";

    private boolean api1Active = true;
    private boolean api2Active = true;
    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public ResponseEntity<?> getWeatherData(String city) {
        if (api1Active) {
            try {
                String url1 = String.format(api1, city);
                ResponseEntity<String> res1 = restTemplate.getForEntity(url1, String.class);
                sendStatusToHeartbeat("Using API1");
                return ResponseEntity.ok(res1.getBody());
            } catch (HttpClientErrorException.TooManyRequests e) {
                logger.warn("Rate limit hit on API1");
            } catch (HttpClientErrorException.Forbidden e) {
                api1Active = false;
                logger.error("API1 key invalid. Marked inactive.");
            } catch (ResourceAccessException e) {
                logger.warn("API1 timeout. Retrying...");
                // Retry once
                try {
                    String url1 = String.format(api1, city);
                    ResponseEntity<String> retry = restTemplate.getForEntity(url1, String.class);
                    sendStatusToHeartbeat("API1 Retry Success");
                    return retry;
                } catch (Exception ignored) {}
            } catch (Exception e) {
                logger.error("API1 failed unexpectedly.");
            }
        }

        if (api2Active) {
            try {
                String url2 = String.format(api2, city);
                ResponseEntity<String> res2 = restTemplate.getForEntity(url2, String.class);
                sendStatusToHeartbeat("Using API2");
                return ResponseEntity.ok(res2.getBody());
            } catch (HttpClientErrorException.Forbidden e) {
                api2Active = false;
                logger.error("API2 key invalid. Marked inactive.");
            } catch (Exception e) {
                logger.error("API2 failed.");
            }
        }

        sendStatusToHeartbeat("Fallback: Both APIs failed.");
        return ResponseEntity.ok("Stub: Weather unavailable.");
    }

    private void sendStatusToHeartbeat(String status) {
        try {
            restTemplate.postForEntity("http://heartbeat:8081/status", status, String.class);
        } catch (Exception e) {
            logger.warn("Could not send status to Heartbeat.");
        }
    }




}
