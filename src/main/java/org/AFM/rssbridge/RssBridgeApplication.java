package org.AFM.rssbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "org.AFM.rssbridge")
public class RssBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssBridgeApplication.class, args);
    }
}
