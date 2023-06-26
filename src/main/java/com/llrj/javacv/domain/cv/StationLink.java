package com.llrj.javacv.domain.cv;

import lombok.Data;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 地铁站
 * @date 2023-02-25
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
@Data
public class StationLink {
    public StationLink(){}
    public StationLink(int x, int y, String name){
        this.x = x;
        this.y = y;
        this.name = name;
    }

    private int x;
    private int y;
    private String name;
}
