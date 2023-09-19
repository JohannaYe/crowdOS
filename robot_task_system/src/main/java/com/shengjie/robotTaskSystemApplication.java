package com.shengjie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableScheduling
public class robotTaskSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(robotTaskSystemApplication.class,args);
        log.info("项目启动成功...");
    }
}
