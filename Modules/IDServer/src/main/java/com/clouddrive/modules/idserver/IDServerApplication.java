package com.clouddrive.modules.idserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IDServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IDServerApplication.class, args);
    }
}
