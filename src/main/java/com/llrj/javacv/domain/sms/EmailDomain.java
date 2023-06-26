package com.llrj.javacv.domain.sms;

import com.llrj.javacv.domain.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 发送邮件
 * @date 2023-04-01
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Service
@Slf4j
public class EmailDomain {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private AppConfig appConfig;

    public void sendEmailWithImage(String[] to, String subject, String text, String pathToImage) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setFrom(appConfig.getEmailFrom());
        helper.setSubject(subject);
        helper.setText(text, true);
        FileSystemResource file = new FileSystemResource(new File(pathToImage));
        helper.addInline("Image", file);
        javaMailSender.send(message);
        log.info("邮件发送成功,to:{}, image:{}", String.join(",", to) ,pathToImage);
    }

    public boolean someMethod(String imgFilePath) {
        //String imgFilePath = "C:\\Users\\guofucheng\\Desktop\\temp\\opencv\\test.jpg";
        try {
            this.sendEmailWithImage(appConfig.getEmailTo().split(","),
                    "【符合保密要求，可在手机端查阅】sjw",
                    "<html><body><img src='cid:Image'></body></html>",
                    imgFilePath);
            return true;
        }
        catch (Exception e) {
            log.error("邮件发送失败：{}", e.getMessage());
            return false;
        }
    }

}
