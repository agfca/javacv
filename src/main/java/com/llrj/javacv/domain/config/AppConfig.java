package com.llrj.javacv.domain.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 配置文件
 * @date 2023-03-06
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Configuration
@Data
public class AppConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    // ping文件夹
    private String pingFileDir = "D:\\ping";

    // ping文件短信发送
    @Value("${pingSmsTo:}")
    private String pingSmsTo;
    private String ipNormalStr = "访问正常";
    private String ipStopStr = "已停止监测";

    // 图像识别文件夹
    private String opencvDir = "D:\\opencv";
    private String opencvNormalStr = "站点、线路正常";
    private String opencvErrorStr = "站点识别错误";

    /*
     网络拓扑图异常消息接收人员有两个：陈曦c-xi和夏荣x-rong（测试阶段只发给c-xi帐号，正式上线后发给c-xi和x-rong）
     Ping值异常消息接收人员有一个：黄亮h-liang

     i国网消息提醒接口sceneID和token。
     sceneId:10006
     token:32ae73f15919e44e65555ee2aec1b9f1
 */
    // opencv短信发送
    @Value("${opencvSmsTo:}")
    private String opencvSmsTo;
    // opencv邮件发送
    @Value("${opencvEmailTo:}")
    private String opencvEmailTo;
    // 发送人
    @Value("${spring.mail.username:}")
    private String opencvEmailFrom;


}
