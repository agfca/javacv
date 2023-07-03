package com.llrj.javacv.domain.sms;

import com.llrj.javacv.domain.enums.MonitorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 发送短息
 * @date 2023-02-19
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Slf4j
@Service
public class ShortMsgDomain {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailDomain emailDomain;

    private String token = "32ae73f15919e44e65555ee2aec1b9f1";
    private String sendTextMessageUrl = "http://10.186.42.172:9093/message/sendTextMessage";
    private String sendPlainTextMessageUrl = "http://10.186.42.172:9093/message/sendPlainTextMessage";

    Map<String, ShortMsg> monitorMsgMap = new HashMap<>();
    Map<String, ShortMsg> monitorEmailMap = new HashMap<>();

    // 正常、短信发送一次
    int msgNormalNum = 1;
    // 异常、短信发送三次
    int msgErrorNum = 1;
    // 邮件 发送一次
    int emailNum = 1;


    /*
{
    "touser": "liumy4436",
    "subject": "我是标题",
    "content": "2022 年 05 月 06 日 14:47:41",
    "type": "1",
    "token": "1688d5d6b489ddf4xxxx798d3ff16e36d"
}
*/
    public void sendMsgAndEmail(MonitorTypeEnum monitorEnum, boolean allNormal, String users, String msg, String imgFile) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("touser", users);
        paramsMap.put("content", msg);
        paramsMap.put("type", "1");
        paramsMap.put("token", token);
        paramsMap.put("sceneId", "10006");
        switch (monitorEnum) {
            // IP
            case IP_MONITOR:
                paramsMap.put("subject", MonitorTypeEnum.IP_MONITOR.getDesc());
                break;
            // 拓扑图
            case IMG_MONITOR:
                paramsMap.put("subject", MonitorTypeEnum.IMG_MONITOR.getDesc());
                break;
            default:
        }
        // 同一类监控：未发短信、短信信息不同、短信未发三次
        if (this.checkSendMsg(monitorEnum, msg)) {
            // 发送短信, 成功则记录到 map
            if(this.sendSms(sendPlainTextMessageUrl,paramsMap)){
                this.addMsgMapRecord(monitorEnum, msg, allNormal);
            }
            // 发送邮件
            if (!StringUtils.isEmpty(imgFile) && this.checkSendEmail(monitorEnum, msg)) {
                if (emailDomain.sendEmail(imgFile)) {
                    this.addEmailMapRecord(monitorEnum, msg, allNormal);
                }
            }
        }
    }

    public boolean sendSms(String url, Map<String,String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36");
        headers.add("accept", MediaType.ALL_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        log.info("发送短信, 调用URL:{}, requestBody:{}", url, body);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return true;
        }
        catch (Exception e) {
            log.error("发送短信失败:{}", e.getMessage());
            return false;
        }
    }

    private boolean checkSendMsg(MonitorTypeEnum monitorEnum, String msg) {
        ShortMsg history = monitorMsgMap.get(monitorEnum.getCode());
        return Objects.isNull(history)
                || !history.getMsg().equals(msg)
                // 异常， 三次
                || (!history.getNormalFlag() && history.getSendNum() < msgErrorNum)
                // 正常，一次
                || (history.getNormalFlag() && history.getSendNum() < msgNormalNum);
    }

    /**
     * 添加短信 map 记录
     */
    private void addMsgMapRecord(MonitorTypeEnum monitorEnum, String msg, Boolean normalFlag) {
        ShortMsg history = monitorMsgMap.get(monitorEnum.getCode());
        int sendNum = (Objects.isNull(history) || !history.getMsg().equals(msg)) ? 1 : history.getSendNum() + 1;
        ShortMsg shortMsg = new ShortMsg();
        shortMsg.setMsg(msg);
        shortMsg.setMonitorType(monitorEnum.getCode());
        shortMsg.setNormalFlag(normalFlag);
        shortMsg.setSendNum(sendNum);
        monitorMsgMap.put(monitorEnum.getCode(), shortMsg);
    }


    private boolean checkSendEmail(MonitorTypeEnum monitorEnum, String msg) {
        ShortMsg history = monitorEmailMap.get(monitorEnum.getCode());
        return Objects.isNull(history)
                || !history.getMsg().equals(msg)
                // 异常 或者 正常，都只发送一次
                || (history.getNormalFlag() && history.getSendNum() < emailNum);
    }

    /**
     * 添加邮件 map 记录， 只发送一次
     */
    private void addEmailMapRecord(MonitorTypeEnum monitorEnum, String msg, Boolean normalFlag) {
        ShortMsg history = monitorEmailMap.get(monitorEnum.getCode());
        int sendNum = (Objects.isNull(history) || !history.getMsg().equals(msg)) ? 1 : history.getSendNum() + 1;
        ShortMsg shortMsg = new ShortMsg();
        shortMsg.setMsg(msg);
        shortMsg.setMonitorType(monitorEnum.getCode());
        shortMsg.setNormalFlag(normalFlag);
        shortMsg.setSendNum(sendNum);
        monitorEmailMap.put(monitorEnum.getCode(), shortMsg);
    }

}

