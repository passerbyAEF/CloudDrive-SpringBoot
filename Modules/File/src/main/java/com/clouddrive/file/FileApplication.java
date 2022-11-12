package com.clouddrive.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@SpringBootApplication(scanBasePackages = "com.clouddrive", exclude = {DataSourceAutoConfiguration.class})
public class FileApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

    @Value("${CloudDrive.SavePath}")
    String fileSavePath;
    @Value("${CloudDrive.BufferPath}")
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
