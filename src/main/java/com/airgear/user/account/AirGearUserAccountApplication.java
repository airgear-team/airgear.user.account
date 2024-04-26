package com.airgear.user.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.airgear.user.account", "com.airgear.model"})
public class AirGearUserAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirGearUserAccountApplication.class, args);
    }

}
