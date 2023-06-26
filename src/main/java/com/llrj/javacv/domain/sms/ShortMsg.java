package com.llrj.javacv.domain.sms;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 短信
 * @date 2023-03-07
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Data
public class ShortMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private String monitorType;
    private Boolean normalFlag;
    private String msg;
    private Integer sendNum;

}


