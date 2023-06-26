package com.llrj.javacv.domain.sms;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 短信dto
 * @date 2023-03-09
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Data
public class SMSDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
    {
    "touser": "liumy4436",
    "subject": "我是标题",
    "content": "2022 年 05 月 06 日 14:47:41",
    "type": "1",
    "token": "1688d5d6b489ddf4xxxx798d3ff16e36d"
}
     */
    private String touser;
    private String subject;
    private String content;
    private String type;
    private String token;
}
