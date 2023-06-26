package com.llrj.javacv.domain.cv;


import java.util.List;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 站点名称
 * @date 2023-02-23
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
public class StationNameConstant {
/*    public static int[][] allRange = new int[][]{
            {0,71},
            {71,186},
            {186,300},
            {300,394},
            {394,543},
            {543,700},
            {700,848},
            {848,1014},
            {1014,1151},
            {1151,1230},
    };*/
    public static int[][] xRange = new int[][]{
            // 开始  521， 结束 1910
            {471,592},
            {592,716},
            {716,844},
            {844,953},
            {953,1129},
            {1129,1318},
            {1318,1509},
            {1509,1711},
            {1711,1862},
            {1862,2000},
    };

    public static String[][] AllStationName = new String[][]{
            // 4个
            {"龙门浩", "弹子石", "高峰寺", "大佛寺"},
            // 2个
            {"阳光一百", "黄桷垭"},
            // 11个
            {"南坪", "响水洞", "苏家湾", "丹桂", "回龙湾", "金家岩", "鸡冠石", "花红", "峡口", "长生所", "长生站"},
            // 6个
            {"南岸备调", "融侨站", "纳溪沟", "百步梯", "茶园6608-1", "边界8808-1"},
            // 10个 "天文站","五步" 需要判断顺序
            {"柏子桥", "东港站", "广阳所", "迎龙", "天文站", "五步", "东泉所", "物资仓库", "茶园6608-2", "边界8808-2"},
            // 11个 ("莲池","恒旺") 需要判断顺序  ("茶园基地","鹿角所")
            {"四公里", "书房", "梓桐", "武堂", "莲池", "恒旺", "姜家", "光国", "茶园基地", "鹿角所", "界石所"},
            // 10个 ("双河站","木洞站")   ("鹿角站","海棠")
            {"双河站", "木洞站", "惠民", "天星寺", "虎啸", "南彭", "鹿角站", "海棠", "南湖所", "界石站"},
            // 11个 ("接龙站","接龙所")   ("南湖站","永隆")
            {"大正沟", "木洞所", "土桥", "李家沱", "走马羊", "花溪", "龙湖湾", "接龙站", "接龙所", "南湖站", "永隆"},
            // 8个
            {"白马山", "天明", "金竹", "广汇公司", "鱼洞站", "鱼洞客服", "安澜", "桥口坝"},
            // 1个
            {"一品所"},
    };

    /**
     * 通过点位 AllStationName ， 初始化几个区间的x轴范围
     */
    public static void initStationPointRange(List<StationPoint> stationPointList) {
        //  初始化这四个站点的x轴范围  {"龙门浩", "弹子石", "高峰寺", "大佛寺"},
        xRange[0][0] = stationPointList.get(0).getX() - 50;
        xRange[0][1] = (stationPointList.get(3).getX() + stationPointList.get(4).getX())/2;

        //  初始化这2个站点的x轴范围  {"阳光一百", "黄桷垭"},
        xRange[1][0] = (stationPointList.get(3).getX() + stationPointList.get(4).getX())/2;
        xRange[1][1] = (stationPointList.get(5).getX() + stationPointList.get(6).getX())/2;

        xRange[2][0] = (stationPointList.get(5).getX() + stationPointList.get(6).getX())/2;
        xRange[2][1] = (stationPointList.get(16).getX() + stationPointList.get(17).getX())/2;

        xRange[3][0] = (stationPointList.get(16).getX() + stationPointList.get(17).getX())/2;
        xRange[3][1] = (stationPointList.get(22).getX() + stationPointList.get(23).getX())/2;

        xRange[4][0] = (stationPointList.get(22).getX() + stationPointList.get(23).getX())/2;
        xRange[4][1] = (stationPointList.get(32).getX() + stationPointList.get(33).getX())/2;

        xRange[5][0] = (stationPointList.get(32).getX() + stationPointList.get(33).getX())/2;
        xRange[5][1] = (stationPointList.get(43).getX() + stationPointList.get(44).getX())/2;

        xRange[6][0] = (stationPointList.get(43).getX() + stationPointList.get(44).getX())/2;
        xRange[6][1] = (stationPointList.get(53).getX() + stationPointList.get(54).getX())/2;

        xRange[7][0] = (stationPointList.get(53).getX() + stationPointList.get(54).getX())/2;
        xRange[7][1] = (stationPointList.get(64).getX() + stationPointList.get(65).getX())/2;

        xRange[8][0] = (stationPointList.get(64).getX() + stationPointList.get(65).getX())/2;
        xRange[8][1] = (stationPointList.get(72).getX() + stationPointList.get(73).getX())/2;

        xRange[9][0] = (stationPointList.get(72).getX() + stationPointList.get(73).getX())/2;
        xRange[9][1] = stationPointList.get(73).getX() + 50;
    }

}
