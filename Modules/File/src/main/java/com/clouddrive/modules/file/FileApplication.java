package com.clouddrive.modules.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.FileNotFoundException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(basePackages = "com.clouddrive")
@EnableDiscoveryClient
public class FileApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

    @Value("${clouddrive.save-path}")
    String fileSavePath;
    @Value("${clouddrive.buffer-path}")
    String fileBufferPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!new File(fileSavePath).exists()) {
            throw new FileNotFoundException(fileSavePath + "不存在！");
        }
        if (!new File(fileBufferPath).exists()) {
            throw new FileNotFoundException(fileBufferPath + "不存在！");
        }
    }
}
