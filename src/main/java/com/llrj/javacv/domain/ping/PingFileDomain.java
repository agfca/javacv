package com.llrj.javacv.domain.ping;

import com.llrj.javacv.domain.config.AppConfig;
import com.llrj.javacv.domain.enums.MonitorTypeEnum;
import com.llrj.javacv.domain.sms.ShortMsgDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description ping 文件识别
 * @date 2023-02-10
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
@Slf4j
@Service
public class PingFileDomain {

    @Autowired
    private ShortMsgDomain shortMsgDomain;

    @Autowired
    private AppConfig appConfig;

    //文本格式：Charset charset = Charset.defaultCharset();
    Charset charset = Charset.forName("GBK");


    // ping 192.168.0.1 -t >d:\ping\192.168.0.1.txt
    public void main() {
        // 读取ping文件夹
        File path = new File(appConfig.getPingFileDir());
        //列出该目录下所有文件和文件夹
        File[] files = path.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            return;
        }
        // 是否所有都正常
        boolean allNormalFlag = true;
        StringBuilder msg = new StringBuilder();

        for (File file : files) {
            // 读取文件最后一行
            String content = this.lastLineReader(file);
            if (!appConfig.getIpNormalStr().equals(content)) {
                allNormalFlag = false;
            }
            // 获取去除后缀文件名
            String ipAddress = file.getName().replaceAll("[.][^.]+$", "");
            msg.append(ipAddress).append(":").append(content).append("\n");
        }
        /*
        1.全部正常：通知一次
        2.访问失败：通知三次
         */
        shortMsgDomain.sendMsgAndEmail(MonitorTypeEnum.IP_MONITOR, allNormalFlag, msg.toString(), null);
    }


    private String lastLineReader(File file) {
        String lastLine = "";
        try (ReversedLinesFileReader reversedLinesReader = new ReversedLinesFileReader(file, charset)) {
            lastLine = reversedLinesReader.readLine();
        } catch (Exception e) {
            log.error("文件读取错误:{}", e.getMessage(), e);
        }
        /*
        来自 192.168.0.1 的回复: 字节=32 时间<1ms TTL=64
        文件停止写入:  输出统计结果、ctrl+c关闭
        1.   最短 = 1ms，最长 = 112ms，平均 = 8ms
        2.Control-C

        请求超时
         */
        if (lastLine.matches(".*来自 \\S+? 的回复: 字节\\S+? 时间\\S+? TTL.+?")) {
            return appConfig.getIpNormalStr();
        }
        else if("Control-C".equals(lastLine) || lastLine.matches(".*最短.*最长.*平均.*")) {
            return appConfig.getIpStopStr();
        }
        else {
            return lastLine;
        }
    }


}
