package com.bioquix.heartbeat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bioquix.heartbeat.service.HeartbeatService;

@RestController
public class HeartbeatController {

    @Autowired
    private HeartbeatService heartbeatService;

    @PostMapping("/status")
    public ResponseEntity<String> receiveStatus(@RequestBody String status) {
        heartbeatService.logStatus(status);
        return ResponseEntity.ok("Status received");
    }

}
