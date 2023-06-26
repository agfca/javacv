package com.llrj.javacv.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MonitorTypeEnum {

    IP_MONITOR("01", "IP监测"),
    IMG_MONITOR("02", "拓扑图监测");

    private final String code;
    private final String desc;

    MonitorTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final String[] ARRAYS = Arrays.stream(values()).map(MonitorTypeEnum::getCode).toArray(String[]::new);

}
