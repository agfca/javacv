package com.llrj.javacv.domain.cv;

import org.bytedeco.opencv.opencv_core.CvScalar;

import static org.bytedeco.opencv.global.opencv_core.cvScalar;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 颜色常量
 * @date 2023-02-16
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
public class ColorConstant {
    private ColorConstant() {
    }
    // 多个颜色组合的范围
/*    public static CvScalar g_min = cvScalar(0, 43, 46, 0);
    public static CvScalar g_max= cvScalar(150, 255, 220, 0);*/

    //hsv红色范围  0-10  色相、饱和度、明度
    public static CvScalar red_min1_link = cvScalar(0, 20, 30, 0);
    public static CvScalar red_max1_link = cvScalar(10, 80, 150, 0);
    //hsv红色范围  156-180
    public static CvScalar red_min2_link = cvScalar(156, 35, 46, 0);
    public static CvScalar red_max2_link = cvScalar(180, 255, 180, 0);


    //hsv红色范围  0-10  todo  这个范围的红色不过滤也能执行
    public static CvScalar red_min1 = cvScalar(0, 43, 46, 0);
    public static CvScalar red_max1 = cvScalar(10, 255, 255, 0);
    //hsv红色范围  156-180 todo 前面是紫， 需要剔除
    public static CvScalar red_min2 = cvScalar(156, 43, 46, 0);
    public static CvScalar red_max2 = cvScalar(180, 255, 255, 0);
    //橙色:11-25
    public static CvScalar orange_min = cvScalar(11, 43, 46, 0);
    public static CvScalar orange_max = cvScalar(25, 255, 255, 0);

    //红色+橙色:0-25
    public static CvScalar red_orange_min = cvScalar(0, 43, 46, 0);
    public static CvScalar red_orange_max = cvScalar(25, 255, 255, 0);

    //黄色:26-34
    public static CvScalar yellow_min = cvScalar(26, 43, 46, 0);
    public static CvScalar yellow_max = cvScalar(34, 255, 255, 0);
    //绿色:35-77
    public static CvScalar green_min = cvScalar(35, 43, 46, 0);
    public static CvScalar green_max = cvScalar(77, 255, 255, 0);
    //青色:78-99
    public static CvScalar cyan_min = cvScalar(78, 43, 46, 0);
    public static CvScalar cyan_max = cvScalar(99, 255, 255, 0);
    //蓝色:100-124
    public static CvScalar blue_min = cvScalar(100, 43, 46, 0);
    public static CvScalar blue_max = cvScalar(124, 255, 255, 0);

    //黄色+绿色+青色:26-34
    public static CvScalar yellow_green_cyan_blue_min = cvScalar(26, 43, 46, 0);
    public static CvScalar yellow_green_cyan_blue_max = cvScalar(124, 255, 255, 0);


    //青色+蓝色:78-99
    public static CvScalar cyan_blue_min = cvScalar(78, 43, 46, 0);
    public static CvScalar cyan_blue_max = cvScalar(124, 255, 255, 0);

    //紫色:125-155
    public static CvScalar purple_min = cvScalar(125, 43, 46, 0);
    public static CvScalar purple_max = cvScalar(155, 255, 255, 0);


}
