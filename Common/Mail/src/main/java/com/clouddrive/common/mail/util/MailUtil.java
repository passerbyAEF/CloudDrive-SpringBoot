package com.clouddrive.common.mail.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailUtil {
    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) throws MailException {
        //1. JavaMailSender对象创建MimeMessage
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);//【true】表示支持html格式的文本

        //3. JavaMailSender对象发送构建好的message
        mailSender.send(message);
    }
}
