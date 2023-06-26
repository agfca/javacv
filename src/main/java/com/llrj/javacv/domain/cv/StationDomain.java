package com.llrj.javacv.domain.cv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 地铁站处理
 * @date 2023-03-23
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Slf4j
@Service
public class StationDomain {


    public void findStationName(List<StationPoint> stationList) {
        // 排序
        //stationList = stationList.stream().sorted(Comparator.comparing(Station::getX)).collect(Collectors.toList());
        List<List<StationPoint>> allStationList = this.getAllStationList();
        for (StationPoint station : stationList) {
            int index = this.getIndex(station.getX());
            if (0<=index && index<10) {
                //y 的排序处理， 特定的几个需要再次判断 x轴大小  ， 10个分区已经处理， 还差每个分区的排序
                allStationList.get(index).add(station);
            }
        }
        this.sortByY(allStationList);
        // 几个特殊的排序
        this.sortSpecial(allStationList);
    }


    private void sortByY(List<List<StationPoint>> allStationList){
        for (int index = 0; index < allStationList.size(); index++) {
            // 排序， 然后取出
            allStationList.set(index, allStationList.get(index).stream().sorted(Comparator.comparing(StationPoint::getY)).collect(Collectors.toList()));
            for (int i = 0; i < allStationList.get(index).size(); i++) {
                allStationList.get(index).get(i).setName(this.getName(index,i));
            }
        }
    }

    private int getIndex(int x) {
        for (int i = 0; i < StationNameConstant.xRange.length; i++) {
            if (StationNameConstant.xRange[i][0] <= x && x < StationNameConstant.xRange[i][1]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param index  [0-10)
     * @return
     */
    private String getName(int index,int size) {
        try{
            return StationNameConstant.AllStationName[index][size];
        }catch (Exception e){
            return "";
        }
    }

    private void sortSpecialImpl(List<List<StationPoint>> allStationList, int index1, int index2){
        if (allStationList.get(index1).get(index2).getX() > allStationList.get(index1).get(index2+1).getX()) {
            allStationList.get(index1).get(index2).setName(StationNameConstant.AllStationName[index1][index2+1]);
            allStationList.get(index1).get(index2+1).setName(StationNameConstant.AllStationName[index1][index2]);
        }
    }
    private void sortSpecial(List<List<StationPoint>> allStationList){
        this.sortSpecialImpl(allStationList,4, 4);
        this.sortSpecialImpl(allStationList,5, 4);
        this.sortSpecialImpl(allStationList,5, 8);
        this.sortSpecialImpl(allStationList,6, 0);
        this.sortSpecialImpl(allStationList,6, 6);
        this.sortSpecialImpl(allStationList,7, 7);
        this.sortSpecialImpl(allStationList,7, 9);
    }

    public List<List<StationPoint>> getAllStationList() {
        List<StationPoint> stationList0 = new ArrayList<>(4);
        List<StationPoint> stationList1 = new ArrayList<>(2);
        List<StationPoint> stationList2 = new ArrayList<>(11);
        List<StationPoint> stationList3 = new ArrayList<>(6);
        List<StationPoint> stationList4 = new ArrayList<>(10);
        List<StationPoint> stationList5 = new ArrayList<>(11);
        List<StationPoint> stationList6 = new ArrayList<>(10);
        List<StationPoint> stationList7 = new ArrayList<>(11);
        List<StationPoint> stationList8 = new ArrayList<>(8);
        List<StationPoint> stationList9 = new ArrayList<>(1);

        List<List<StationPoint>> allStationList = new ArrayList<>(10);
        allStationList.add(stationList0);
        allStationList.add(stationList1);
        allStationList.add(stationList2);
        allStationList.add(stationList3);
        allStationList.add(stationList4);
        allStationList.add(stationList5);
        allStationList.add(stationList6);
        allStationList.add(stationList7);
        allStationList.add(stationList8);
        allStationList.add(stationList9);
        return allStationList;
    }

}
