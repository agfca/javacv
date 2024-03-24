package com.llrj.javacv.domain.cv;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 地铁站链路
 * @date 2023-02-23
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Slf4j
@Service
public class StationLinkDomain {

    private static String dash = "-";

    private static int offSet = 0;

    private final static int offSetX = 3;
    private final static int offSetY = 7;

    private StationLink getStationLink(StationPoint a, StationPoint b) {
        StationLink link = new StationLink();
        link.setName(a.getName() + dash + b.getName());
        // 每个连线平分成3份
        int sumX3 = ((a.getX() + b.getX()) - (2 * a.getX())) / 6;
        int sumY3 = ((a.getY() + b.getY()) - (2 * a.getY())) / 6;
        if ("广阳所-迎龙".equals(link.getName())){
            link.setX(a.getX() + 4 * sumX3);
            link.setY(a.getY() + 4 * sumY3);
        }
        else if ("花红-峡口".equals(link.getName())) {
            link.setX(a.getX() + 2 * sumX3);
            link.setY(a.getY() + 2 * sumY3);
        }
        else if ("龙门浩-阳光一百".equals(link.getName())) {
            link.setX(b.getX() - 2 * sumX3);
            link.setY(b.getY() - 2 * sumY3);
        }
        else if ("木洞站-木洞所".equals(link.getName())) {
            link.setX(b.getX() - 2 * sumX3);
            link.setY(b.getY() - 2 * sumY3);
        }
        else if (
                //"响水洞-苏家湾".equals(link.getName())||
                "龙门浩-弹子石".equals(link.getName())) {
            link.setX(b.getX() - 2 * sumX3);
            link.setY(b.getY() - 2 * sumY3);
        }
        else {
            link.setX((a.getX() + b.getX()) / 2);
            link.setY((a.getY() + b.getY()) / 2);
        }
        link.setX(link.getX() - offSetX);
        link.setY(link.getY() - offSetY);
        return link;
    }

    private StationLink getSpecialLinkFirst(StationPoint a, StationPoint b) {
        if (offSet == 0) {
            //通过 高峰寺-大佛寺 计算x轴差值
            offSet = (b.getY() - a.getY()) / 15;
        }
        StationLink link = new StationLink();
        link.setName(a.getName() + dash + b.getName());
        link.setX((a.getX()+b.getX())/2 - offSet);
        link.setY((a.getY()+b.getY())/2);
        return link;
    }

    private StationLink getSpecialLinkSecond(StationPoint a, StationPoint b) {
        StationLink link = new StationLink();
        link.setName(a.getName() + dash + b.getName());
        link.setX((a.getX()+b.getX())/2 + offSet);
        link.setY((a.getY()+b.getY())/2);
        return link;
    }

    private StationPoint getStationByName(List<StationPoint> stationList, String name){
        for (StationPoint temp : stationList) {
            if (name.equals(temp.getName())) {
                return temp;
            }
        }
        return null;
    }

    private void addLink(String nameA, String nameB, List<StationPoint> stationList, List<StationLink> stationLinkList) {
        StationPoint a = this.getStationByName(stationList, nameA);
        StationPoint b = this.getStationByName(stationList, nameB);
        if (a !=null && b!=null) {
            stationLinkList.add(this.getStationLink(a,b));
        }
    }

    /**
     * 特殊处理
     *  高峰寺-大佛寺  天星寺-虎啸   虎啸-海棠
     */
    private void addSpecialLink(String nameA, String nameB, List<StationPoint> stationList, List<StationLink> stationLinkList, boolean firstFlag) {
        StationPoint a = this.getStationByName(stationList, nameA);
        StationPoint b = this.getStationByName(stationList, nameB);
        if (a !=null && b!=null) {
            if (firstFlag) {
                stationLinkList.add(this.getSpecialLinkFirst(a,b));
            }else {
                stationLinkList.add(this.getSpecialLinkSecond(a,b));
            }
        }
    }

    /*
    //1
    private static String lmxs = "龙门浩-响水洞";
    private static String lmyg = "龙门浩-阳光一百";
    private static String lmdz = "龙门浩-弹子石";

    private static String dzgf = "弹子石-高峰寺";
    private static String gfjg = "高峰寺-鸡冠石";
    private static String gfdf_1 = "高峰寺-大佛寺";
    private static String gfdf_2 = "高峰寺-大佛寺";

    private static String ygjg = "阳光一百-鸡冠石";
    private static String hjjg = "黄桷垭-鸡冠石";
    private static String hjcs = "黄桷垭-长生站";

    //2
    private static String npbz = "南坪-柏子桥";
    private static String npna = "南坪-南岸备调";
    private static String npxs = "南坪-响水洞";
    private static String xssj = "响水洞-苏家湾";

    private static String sjdg = "苏家湾-丹桂";
    private static String dgna = "丹桂-南岸备调";
    private static String dghl = "丹桂-回龙湾";
    private static String hljj = "回龙湾-金家岩";
    private static String jjna = "金家岩-南岸备调";
    private static String jjrq = "金家岩-融侨站";
    private static String jjjg = "金家岩-鸡冠石";


    //3
    private static String jgsg = "鸡冠石-四公里";
    private static String jgnx = "鸡冠石-纳溪沟";
    private static String jghh = "鸡冠石-花红";
    private static String jgxk = "花红-峡口";
    private static String xkbb = "峡口-百步梯";
    private static String xkcs = "峡口-长生所";
    private static String cstw = "长生站-天文站";
    private static String cscy = "长生站-茶园6608-1";

    private static String rqsg = "融侨站-四公里";
    private static String bbtw = "百步梯-天文站";
    private static String cycy1 = "茶园6608-1-茶园6608-2";
    private static String cybj1 = "茶园6608-1-边界8808-1";
    private static String bjbj = "边界8808-1-边界8808-2";

    //4
    private static String bzsg = "柏子桥-四公里";
    private static String dgsf = "东港站-书房";
    private static String gysf = "广阳所-书房";
    private static String gyyl = "广阳所-迎龙";
    private static String ylwb = "迎龙-五步";
    private static String yltw = "迎龙-天文站";
    private static String twcy = "天文站-茶园6608-2";
    private static String wbdq = "五步-东泉所";
    private static String cywz = "茶园6608-2-物资仓库";
    private static String cycy2 = "茶园6608-2-茶园基地";
    private static String cybj2 = "茶园6608-2-边界8808-2";


    //5
        private static String wbdq = "四公里-白马山";
    private static String wbdq = "四公里-书房";
    private static String wbdq = "书房-白马山";
    private static String wbdq = "书房-梓桐";
    private static String wbdq = "书房-武堂";
    private static String wbdq = "梓桐-双河站";
    private static String wbdq = "武堂-光国";
    private static String wbdq = "莲池-恒旺";
    private static String wbdq = "莲池-光国";
    private static String wbdq = "姜家-天星寺";
    private static String wbdq = "光国-虎啸";
    private static String wbdq = "光国-茶园基地";
    private static String wbdq = "茶园基地-鹿角所";
    private static String wbdq = "鹿角所-鹿角站";
    private static String wbdq = "界石所-界石站";

    //6
        private static String wbdq = "双河站-木洞站";
    private static String wbdq = "木洞站-木洞所";
    private static String wbdq = "惠民-天星寺";
    private static String wbdq = "天星寺-虎啸";
    private static String wbdq = "天星寺-虎啸";

    private static String wbdq = "虎啸-海棠";
    private static String wbdq = "虎啸-海棠";

    private static String wbdq = "虎啸-南彭";
    private static String wbdq = "南彭-鹿角站";
    private static String wbdq = "鹿角站-海棠";
    private static String wbdq = "鹿角站-界石站";
    private static String wbdq = "海棠-龙湖湾";

    //7
        private static String wbdq = "南湖所-南湖站";
    private static String wbdq = "南湖所-界石站";
    private static String wbdq = "界石站-桥口坝";

    private static String wbdq = "大正沟-白马山";
    private static String wbdq = "大正沟-天明";
    private static String wbdq = "土桥-李家沱";
    private static String wbdq = "土桥-走马羊";
    private static String wbdq = "李家沱-走马羊";
    private static String wbdq = "走马羊-花溪";
    private static String wbdq = "花溪-龙湖湾";
    private static String wbdq = "龙湖湾-鱼洞站";
    private static String wbdq = "接龙站-接龙所";
    private static String wbdq = "接龙站-南湖站";
    private static String wbdq = "南湖站-永隆";
    private static String wbdq = "永隆-安澜";


    //8
        private static String wbdq = "白马山-天明";
    private static String wbdq = "天明-金竹";
    private static String wbdq = "金竹-鱼洞站";
    private static String wbdq = "广汇公司-鱼洞站";
    private static String wbdq = "鱼洞站-鱼洞客服";
    private static String wbdq = "鱼洞客服-桥口坝";
    private static String wbdq = "安澜-桥口坝";
    private static String wbdq = "桥口坝-一品所";
    */

    public List<StationLink> getLinkListByName(List<StationPoint> stationList) {
        List<StationLink> stationLinkList = new ArrayList<>(100);
        //1.
        this.addLink(StationNameConstant.AllStationName[0][0], StationNameConstant.AllStationName[2][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[0][0], StationNameConstant.AllStationName[1][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[0][0], StationNameConstant.AllStationName[0][1],stationList, stationLinkList);

        this.addLink(StationNameConstant.AllStationName[0][1], StationNameConstant.AllStationName[0][2],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[0][2], StationNameConstant.AllStationName[2][6],stationList, stationLinkList);
        // todo 高峰寺-大佛寺
        this.addSpecialLink(StationNameConstant.AllStationName[0][2], StationNameConstant.AllStationName[0][3],stationList, stationLinkList, true);
        this.addSpecialLink(StationNameConstant.AllStationName[0][2], StationNameConstant.AllStationName[0][3],stationList, stationLinkList,false);

        this.addLink(StationNameConstant.AllStationName[1][0], StationNameConstant.AllStationName[2][6],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[1][1], StationNameConstant.AllStationName[2][6],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[1][1], StationNameConstant.AllStationName[2][10],stationList, stationLinkList);

        //2.
        this.addLink(StationNameConstant.AllStationName[2][0], StationNameConstant.AllStationName[4][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][0], StationNameConstant.AllStationName[3][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][0], StationNameConstant.AllStationName[2][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][1], StationNameConstant.AllStationName[2][2],stationList, stationLinkList);

        this.addLink(StationNameConstant.AllStationName[2][2], StationNameConstant.AllStationName[2][3],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][3], StationNameConstant.AllStationName[3][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][3], StationNameConstant.AllStationName[2][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][4], StationNameConstant.AllStationName[2][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][5], StationNameConstant.AllStationName[3][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][5], StationNameConstant.AllStationName[3][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][5], StationNameConstant.AllStationName[2][6],stationList, stationLinkList);

        //3.
        this.addLink(StationNameConstant.AllStationName[2][6], StationNameConstant.AllStationName[5][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][6], StationNameConstant.AllStationName[3][2],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][6], StationNameConstant.AllStationName[2][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][7], StationNameConstant.AllStationName[2][8],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][8], StationNameConstant.AllStationName[3][3],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][8], StationNameConstant.AllStationName[2][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][10], StationNameConstant.AllStationName[4][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[2][10], StationNameConstant.AllStationName[3][4],stationList, stationLinkList);

        this.addLink(StationNameConstant.AllStationName[3][1], StationNameConstant.AllStationName[5][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[3][3], StationNameConstant.AllStationName[4][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[3][4], StationNameConstant.AllStationName[4][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[3][4], StationNameConstant.AllStationName[3][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[3][5], StationNameConstant.AllStationName[4][10],stationList, stationLinkList);

        //4
        this.addLink(StationNameConstant.AllStationName[4][0], StationNameConstant.AllStationName[5][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][1], StationNameConstant.AllStationName[5][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][2], StationNameConstant.AllStationName[5][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][2], StationNameConstant.AllStationName[4][3],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][3], StationNameConstant.AllStationName[4][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][3], StationNameConstant.AllStationName[4][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][3], StationNameConstant.AllStationName[4][6],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][4], StationNameConstant.AllStationName[5][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][5], StationNameConstant.AllStationName[4][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][6], StationNameConstant.AllStationName[4][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][8], StationNameConstant.AllStationName[4][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][9], StationNameConstant.AllStationName[5][8],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[4][9], StationNameConstant.AllStationName[4][10],stationList, stationLinkList);

        //5
        this.addLink(StationNameConstant.AllStationName[5][0], StationNameConstant.AllStationName[8][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][0], StationNameConstant.AllStationName[5][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][1], StationNameConstant.AllStationName[8][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][1], StationNameConstant.AllStationName[5][2],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][1], StationNameConstant.AllStationName[5][3],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][2], StationNameConstant.AllStationName[6][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][3], StationNameConstant.AllStationName[5][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][4], StationNameConstant.AllStationName[5][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][5], StationNameConstant.AllStationName[5][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][6], StationNameConstant.AllStationName[6][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][7], StationNameConstant.AllStationName[6][6],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][7], StationNameConstant.AllStationName[5][8],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][8], StationNameConstant.AllStationName[5][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][9], StationNameConstant.AllStationName[6][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[5][10], StationNameConstant.AllStationName[6][12],stationList, stationLinkList);


        //新增4个站 大田[4][4]+柳银站[6][2]、石龙站[6][5]、炒油场[6][7]
        //武堂-柳银站
        this.addSpecialLink(StationNameConstant.AllStationName[5][3], StationNameConstant.AllStationName[6][2], stationList, stationLinkList, true);
        this.addSpecialLink(StationNameConstant.AllStationName[5][3], StationNameConstant.AllStationName[6][2], stationList, stationLinkList, false);
        //木洞-柳银站
        this.addLink(StationNameConstant.AllStationName[6][1], StationNameConstant.AllStationName[6][2],stationList, stationLinkList);
        //天星寺-石龙站
        this.addLink(StationNameConstant.AllStationName[6][4], StationNameConstant.AllStationName[6][5],stationList, stationLinkList);
        //炒油场-土桥   炒油场-走马羊
        this.addLink(StationNameConstant.AllStationName[6][7], StationNameConstant.AllStationName[7][2],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][7], StationNameConstant.AllStationName[7][4],stationList, stationLinkList);
        //6
        this.addLink(StationNameConstant.AllStationName[6][0], StationNameConstant.AllStationName[6][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][1], StationNameConstant.AllStationName[7][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][3], StationNameConstant.AllStationName[6][4],stationList, stationLinkList);
        //todo 天星寺-虎啸   虎啸-海棠
        this.addSpecialLink(StationNameConstant.AllStationName[6][4], StationNameConstant.AllStationName[6][6], stationList, stationLinkList, true);
        this.addSpecialLink(StationNameConstant.AllStationName[6][4], StationNameConstant.AllStationName[6][6], stationList, stationLinkList, false);
        this.addSpecialLink(StationNameConstant.AllStationName[6][6], StationNameConstant.AllStationName[6][10], stationList, stationLinkList, true);
        this.addSpecialLink(StationNameConstant.AllStationName[6][6], StationNameConstant.AllStationName[6][10], stationList, stationLinkList, false);

        this.addLink(StationNameConstant.AllStationName[6][6], StationNameConstant.AllStationName[6][8],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][8], StationNameConstant.AllStationName[6][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][9], StationNameConstant.AllStationName[6][10],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][9], StationNameConstant.AllStationName[6][12],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][10], StationNameConstant.AllStationName[7][6],stationList, stationLinkList);


        //7
        this.addLink(StationNameConstant.AllStationName[6][11], StationNameConstant.AllStationName[7][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][11], StationNameConstant.AllStationName[6][12],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[6][12], StationNameConstant.AllStationName[8][7],stationList, stationLinkList);

        this.addLink(StationNameConstant.AllStationName[7][0], StationNameConstant.AllStationName[8][0],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][0], StationNameConstant.AllStationName[8][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][2], StationNameConstant.AllStationName[7][3],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][2], StationNameConstant.AllStationName[7][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][3], StationNameConstant.AllStationName[7][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][4], StationNameConstant.AllStationName[7][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][5], StationNameConstant.AllStationName[7][6],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][6], StationNameConstant.AllStationName[8][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][7], StationNameConstant.AllStationName[7][8],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][7], StationNameConstant.AllStationName[7][9],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][9], StationNameConstant.AllStationName[7][10],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[7][10], StationNameConstant.AllStationName[8][6],stationList, stationLinkList);

        //8
        this.addLink(StationNameConstant.AllStationName[8][0], StationNameConstant.AllStationName[8][1],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][1], StationNameConstant.AllStationName[8][2],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][2], StationNameConstant.AllStationName[8][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][3], StationNameConstant.AllStationName[8][4],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][4], StationNameConstant.AllStationName[8][5],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][5], StationNameConstant.AllStationName[8][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][6], StationNameConstant.AllStationName[8][7],stationList, stationLinkList);
        this.addLink(StationNameConstant.AllStationName[8][7], StationNameConstant.AllStationName[9][0],stationList, stationLinkList);

        return stationLinkList;
    }
// todo  除了 3*2其中特殊的， 其他都进行处理例外



}
