package com.llrj.javacv.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ColorEnum {

    //this.getCyanAndBlue(hsv);//13
    //this.getYellow(hsv);//1
    //this.getGreen(hsv);//51
    //this.getRedAndOrange(hsv);//9
    //一共74 todo 差灰色

    RED_AND_ORANGE("红橙"),
    RED("红"),
    ORANGE("橙"),
    YELLOW("黄"),
    GREEN("绿"),
    CYAN_AND_BLUE("青蓝"),
    GRAY("灰"),
    ;

    private final String desc;

    ColorEnum(String desc) {
        this.desc = desc;
    }

    private static final String[] ARRAYS = Arrays.stream(values()).map(ColorEnum::getDesc).toArray(String[]::new);

}
