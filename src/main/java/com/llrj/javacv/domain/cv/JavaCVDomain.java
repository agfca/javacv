package com.llrj.javacv.domain.cv;


import com.llrj.javacv.domain.config.AppConfig;
import com.llrj.javacv.domain.enums.ColorEnum;
import com.llrj.javacv.domain.enums.MonitorTypeEnum;
import com.llrj.javacv.domain.sms.ShortMsgDomain;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.CvArr;
import org.bytedeco.opencv.opencv_core.CvContour;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.IplConvKernel;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_imgproc.CvFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.opencv.global.opencv_core.cvClearMemStorage;
import static org.bytedeco.opencv.global.opencv_core.cvCopy;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvGet2D;
import static org.bytedeco.opencv.global.opencv_core.cvGetSize;
import static org.bytedeco.opencv.global.opencv_core.cvInRangeS;
import static org.bytedeco.opencv.global.opencv_core.cvMax;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvRect;
import static org.bytedeco.opencv.global.opencv_core.cvRelease;
import static org.bytedeco.opencv.global.opencv_core.cvReleaseImage;
import static org.bytedeco.opencv.global.opencv_core.cvResetImageROI;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_core.cvSetImageROI;
import static org.bytedeco.opencv.global.opencv_highgui.cvShowImage;
import static org.bytedeco.opencv.global.opencv_highgui.cvWaitKey;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_FONT_HERSHEY_COMPLEX;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_SHAPE_ELLIPSE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_SHAPE_RECT;
import static org.bytedeco.opencv.global.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCreateStructuringElementEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvErode;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvInitFont;
import static org.bytedeco.opencv.global.opencv_imgproc.cvMorphologyEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvPutText;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 图像识别
 * @date 2023-02-10
 * @copyright 2022 iwhalecloud . All rights reserved.
 * 设备（路由器）包含7种颜色：
 * 	绿色：设备正常状态
 * 	红色：紧急状态
 * 	橙色：重要状态
 * 	黄色：次要状态
 * 	天蓝色：警告，接口\链路状态告警，网管站告警
 * 	灰色：通知，其他告警\其他设备告警
 * 	蓝色：无法识别的设备
 * （1）	异常识别
 * 当设备出现红色或橙色颜色变化时，表示设备出现故障
 */

@Slf4j
@Service
public class JavaCVDomain {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ShortMsgDomain shortMsgDomain;

    @Autowired
    private StationDomain stationDomain;

    @Autowired
    private StationLinkDomain stationLinkDomain;

    static IplImage inputImage = null;
    static IplImage rgbImg = null;
    static IplImage hsvImg = null;

    static IplImage grayImage = null;

    //red
    static IplImage temp1 = null;
    static IplImage temp2 = null;

    static IplImage erosion_dst = null;
    static IplImage dilate_dst = null;

    //内存复用
    static CvMemStorage storage = CvMemStorage.create();

    /*    static IplConvKernel kernelNoWarn = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);
        static IplConvKernel kernelRed = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);
        static IplConvKernel kernelOrange = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);*/
    // 几个识别颜色点位的结构元素都相同
    static IplConvKernel kernel = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_RECT);
    static IplConvKernel kernelLink = cvCreateStructuringElementEx(3, 3, 0, 0, CV_SHAPE_RECT);
    static IplConvKernel kernelLink2 = cvCreateStructuringElementEx(2, 2, 0, 0, CV_SHAPE_RECT);

    // 上一次图片路径
    private static String lastTimeImgFilePath = null;

    // 调试显示图片
    private static void debugShowImg(String name, CvArr image) {
        if (false) {
            cvShowImage(name, image);
            cvWaitKey(0);
        }
    }
    // 调试显示链路图片
    private static void debugShowLinkImg(String name, CvArr image) {
        if (false) {
            cvShowImage(name, image);
            cvWaitKey(0);
        }
    }

    private static int cvErrorNum = 0;
    private static final int cvErrorSmsSendNum = 3;
    // 图像识别错误发送短信, 是否暂停
    private boolean checkCvErrorSmsSend(List<StationPoint> stationPointList) {
        //识别到的站点不是78个，发送图片识别错误短信
        if (stationPointList.size() == 78) {
            cvErrorNum = 0;
            return false;
        }
        else {
            //多次识别错误， 发送报警信息
            if (++cvErrorNum >= cvErrorSmsSendNum) {
                shortMsgDomain.sendMsgAndEmail(MonitorTypeEnum.IMG_MONITOR, false, appConfig.getOpencvErrorStr(), lastTimeImgFilePath);
            }
            log.info("识别错误,次数:{}，识别数量:{}", cvErrorNum, stationPointList.size());
            return true;
        }
    }

    public void main() {
        String imgFilePath = FileUtils.getLatestImgFilePath(appConfig.getOpencvDir());
        if (imgFilePath == null || imgFilePath.equals(lastTimeImgFilePath)) {
            return;
        }
        // 是否开启：重复图片名称不处理
        lastTimeImgFilePath = imgFilePath;

        //1.加载图片文件， 裁剪  并转换成hsv 图片
        initHSVImg(imgFilePath);
        if (hsvImg == null) {
            return;
        }
        //2.获取站点
        List<StationPoint> stationPointList = this.getStationPointList();
        if (this.checkCvErrorSmsSend(stationPointList)) {
            return;
        }

        //x轴排序,  通过x排序， 可以避免 重复站点。。
        stationPointList = stationPointList.stream().sorted(Comparator.comparing(StationPoint::getX)).collect(Collectors.toList());
        // 通过点位， 初始化区间范围
        StationNameConstant.initStationPointRange(stationPointList);
        //找到点位对应名字
        stationDomain.findStationName(stationPointList);
        //获取红色地铁线
        Set<String> warnLinkNameSet = this.getWarnLinkNameSet(stationPointList);
        //过滤      RED_AND_ORANGE("红橙"),
        List<String> redStationNameList = stationPointList.stream()
                .filter(station -> ColorEnum.RED.getDesc().equals(station.getColor()))
                .map(StationPoint::getName)
                .collect(Collectors.toList());
        /*List<String> orangeStationNameList  = stationPointList.stream()
                .filter(station -> ColorEnum.ORANGE.getDesc().equals(station.getColor()))
                .map(Station::getName)
                .collect(Collectors.toList());*/
        //链路故障—紧急：龙洲湾-鱼洞站、鱼洞站-鱼洞客服，共2条；
        //设备故障—紧急：长生站、峡口、鹿角站，共3条
        String msg = "";
        if (warnLinkNameSet.isEmpty() && redStationNameList.isEmpty()) {
            msg += appConfig.getOpencvNormalStr();
        }
        else {
            if (!warnLinkNameSet.isEmpty()) {
                msg += "链路故障—紧急：" + String.join("、", warnLinkNameSet) + "，共" + warnLinkNameSet.size() + "条；\n";
            }
            if (!redStationNameList.isEmpty()) {
                msg += "设备故障—紧急：" + String.join("、", redStationNameList) + "，共" + redStationNameList.size() + "条；";
            }
        }
        /*
        1.全部正常：通知一次
        2.访问失败：通知三次
         */
        shortMsgDomain.sendMsgAndEmail(MonitorTypeEnum.IMG_MONITOR, redStationNameList.isEmpty() && warnLinkNameSet.isEmpty(), msg, imgFilePath);
    }

    /**
     * 裁剪图片， 并转换成hsv 图片
     */
    private static void initHSVImg(String imgFilePath) {
        try {
            if (inputImage != null) {
                cvReleaseImage(inputImage);
            }
            //1.读取图片 需要满足图片分辨率 （1920*1080）
            inputImage = cvLoadImage(imgFilePath);
            // x 300- 1750
            // y 0-1050
            int x = 300;
            int y = 50;
            int width = 1450;
            int height = 1030;
            if (rgbImg == null) {
                rgbImg = IplImage.create(width, height, inputImage.depth(), inputImage.nChannels());
            }
            cvSetImageROI(inputImage, cvRect(x, y, width, height));
            // 截取图片
            cvCopy(inputImage, rgbImg);
            // 取消ROI
            cvResetImageROI(inputImage);
            //2.创建一个等大小的hsv预备底片 （宽、高、色深8bit 10bit、channel 每个像素能存放的rgb值）
            if (hsvImg == null) {
                hsvImg = IplImage.create(rgbImg.width(), rgbImg.height(), rgbImg.depth(), rgbImg.nChannels());
            }
            //3.rgb->hsvImg
            cvCvtColor(rgbImg, hsvImg, CV_BGR2HSV);

            if (grayImage == null) {
                grayImage = cvCreateImage(cvGetSize(hsvImg), 8, 1);
            }
            if (erosion_dst == null) {
                erosion_dst = cvCreateImage(cvGetSize(hsvImg), 8, 1);
            }
            if (dilate_dst == null) {
                dilate_dst = cvCreateImage(cvGetSize(hsvImg), 8, 1);
            }
            if (temp1 == null) {
                temp1 = cvCreateImage(cvGetSize(hsvImg), 8, 1);
            }
            if (temp2 == null) {
                temp2 = cvCreateImage(cvGetSize(hsvImg), 8, 1);
            }
        }
        catch (Exception e) {
            log.error("裁剪图片错误:{}", e.getMessage());
        }
    }

    private List<StationPoint> getStationPointList() {
        //找到每个颜色的点位,一共78个
        List<StationPoint> stationList = new ArrayList<>(100);
        //this.getColorTest(hsvImg);
        //this.getCyanAndBlue(hsvImg, stationList);//12
        this.getNoWarn(stationList);//60
        //this.getYellow(hsvImg, stationList);//1
        //this.getGreen(hsvImg, stationList);//51
        this.getRed(stationList);
        this.getOrange(stationList);// 16
        //一共74 todo 差灰色
        return stationList;
    }

    /**
     * 通过站点， 获取站点连线中间点位
     */
    private Set<String> getWarnLinkNameSet(List<StationPoint> stationPointList) {
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        //蓝绿色
/*        IplImage grayImage = cvCreateImage(cvGetSize(rgbImg), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(rgbImg, ColorConstant.yellow_green_cyan_blue_min, ColorConstant.yellow_green_cyan_blue_max, grayImage);*/
        //红色
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(hsvImg, ColorConstant.red_min1_link, ColorConstant.red_max1_link, temp1);
        //5.将hsv图片过滤色域到 底片
/*        cvInRangeS(hsvImg, ColorConstant.red_min2_link, ColorConstant.red_min2_link, temp2);
        cvMax(temp1, temp2, temp1);*/
        //橙色
/*        IplImage grayImage = cvCreateImage(cvGetSize(rgbImg), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(rgbImg, ColorConstant.orange_min, ColorConstant.orange_max, grayImage);*/

        //创建一个结构元素
        //IplConvKernel kernelLink=cvCreateStructuringElementEx(3,3,0,0,CV_SHAPE_RECT);
        //腐蚀
        /*IplImage erosion_dst = cvCreateImage(cvGetSize(rgbImg), 8, 1);
        cvErode(grayImage, erosion_dst, kernelLink,1);*/
        //膨胀 todo  调整像素
        debugShowLinkImg("l1", temp1);
        cvDilate(temp1, dilate_dst, kernelLink, 4);
/*     cvShowImage("dilate_dst", dilate_dst);
        cvWaitKey(0);*/
/*      cvErode(dilate_dst, erosion_dst, kernelLink2,1);
        cvShowImage("erosion_dst", erosion_dst);
        cvWaitKey(0);*/
        debugShowLinkImg("l2", dilate_dst);

        List<StationLink> stationLinkList = stationLinkDomain.getLinkListByName(stationPointList);
        Set<String> linkNameSet = new LinkedHashSet<>(stationLinkList.size());
        stationLinkList.forEach(stationLink -> {
            // 获取指定像素点的颜色值,判断是否有颜色
            if (cvGet2D(dilate_dst, stationLink.getY(), stationLink.getX()).val(0) != 0) {
                linkNameSet.add(stationLink.getName());
            }
        });
        return linkNameSet;
    }

    private void getNoWarn(List<StationPoint> stationList) {
        CvRect boundingBox;
        CvSeq ptr = null;
        try {
            debugShowImg("rgbImg",rgbImg);
            //2.将hsv图片过滤色域到 底片
            cvInRangeS(hsvImg, ColorConstant.yellow_green_cyan_blue_min, ColorConstant.yellow_green_cyan_blue_max, grayImage);

            debugShowImg("a",grayImage);
            //4.腐蚀
            cvErode(grayImage, erosion_dst, kernel, 5);
            debugShowImg("a1",erosion_dst);
            //5.膨胀
            cvDilate(erosion_dst, dilate_dst, kernel, 3);
            debugShowImg("a2",dilate_dst);
            //6.查找轮廓并生成轮廓数组, 画出轮廓矩形
            cvClearMemStorage(storage);
            ptr = new CvSeq();
            cvFindContours(dilate_dst, storage, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
            for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
                //使用矩形包装ptr
                boundingBox = cvBoundingRect(ptr, 0);
                //log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
                stationList.add(new StationPoint(boundingBox.x() + boundingBox.width() / 2, boundingBox.y() + boundingBox.height() / 2, "", ColorEnum.GREEN.getDesc()));
            }
        }
        catch (Exception e) {
            log.error("不警告颜色（黄、绿、青、蓝）识别错误:{}", e.getMessage());
        }
        finally {
            if (ptr != null) {
                cvRelease(ptr);
            }
            boundingBox = null;
        }
    }

    private void getRed(List<StationPoint> stationList) {
        CvRect boundingBox;
        CvSeq ptr = null;
        try {
            //2.将hsv图片过滤色域到底片temp1和temp2中
            // 通过cvInRangeS函数将hsv图像中不在ColorConstant.red_min1到ColorConstant.red_max1范围内的像素置为0，其他像素置为255，然后将结果保存在temp1中
            cvInRangeS(hsvImg, ColorConstant.red_min1, ColorConstant.red_max1, temp1);
            // 通过cvInRangeS函数将hsv图像中不在ColorConstant.red_min2到ColorConstant.red_max2范围内的像素置为0，其他像素置为255，然后将结果保存在temp2中
            cvInRangeS(hsvImg, ColorConstant.red_min2, ColorConstant.red_max2, temp2);
            //3.叠加不同的色域范围图像，将temp1和temp2中所有像素的最大值保存在temp1中
            cvMax(temp1, temp2, temp1);

            debugShowImg("b",temp1);
            //5.对temp1进行腐蚀，将结果保存在erosion_dst中
            cvErode(temp1, erosion_dst, kernel, 5);
            debugShowImg("b1",erosion_dst);

            //6.对erosion_dst进行膨胀，将结果保存在dilate_dst中
            cvDilate(erosion_dst, dilate_dst, kernel, 5);
            debugShowImg("b2",dilate_dst);

            //7.查找轮廓并生成轮廓数组, 画出轮廓矩形
            // 创建一个CvSeq指针ptr用于存储轮廓信息
            cvClearMemStorage(storage);
            ptr = new CvSeq();
            cvFindContours(dilate_dst, storage, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
            for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
                //使用矩形包装ptr
                boundingBox = cvBoundingRect(ptr, 0);
                //log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
                stationList.add(new StationPoint(boundingBox.x() + boundingBox.width() / 2, boundingBox.y() + boundingBox.height() / 2, "", ColorEnum.RED.getDesc()));
            }
        }
        catch (Exception e) {
            log.error("红色识别错误:{}", e.getMessage());
        }
        finally {
            if (ptr != null) {
                cvRelease(ptr);
            }
            boundingBox = null;
        }
    }

    private void getOrange(List<StationPoint> stationList) {
        CvRect boundingBox;
        CvSeq ptr = null;
        try {
            //2.将hsv图片过滤色域到 底片
            cvInRangeS(hsvImg, ColorConstant.orange_min, ColorConstant.orange_max, grayImage);
            debugShowImg("c",grayImage);
            //4.腐蚀
            cvErode(grayImage, erosion_dst, kernel, 3);
            debugShowImg("c1",erosion_dst);

            //5.膨胀
            cvDilate(erosion_dst, dilate_dst, kernel, 5);
            debugShowImg("c2",dilate_dst);

            //6.查找轮廓并生成轮廓数组, 画出轮廓矩形
            cvClearMemStorage(storage);
            ptr = new CvSeq();
            cvFindContours(dilate_dst, storage, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
            for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
                //使用矩形包装ptr
                boundingBox = cvBoundingRect(ptr, 0);
                //log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
                stationList.add(new StationPoint(boundingBox.x() + boundingBox.width() / 2, boundingBox.y() + boundingBox.height() / 2, "", ColorEnum.ORANGE.getDesc()));
            }
        }
        catch (Exception e) {
            log.error("橙色识别错误:{}", e.getMessage());
        }
        finally {
            if (ptr != null) {
                cvRelease(ptr);
            }
            boundingBox = null;
        }
    }


    private void getCyanAndBlue(IplImage orgHsv, List<StationPoint> stationList) {
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(orgHsv, ColorConstant.cyan_blue_min, ColorConstant.cyan_blue_max, temp1);
        cvShowImage("imgThreshold", temp1);
        cvWaitKey(0);

        //创建一个结构元素
        IplConvKernel kernel = cvCreateStructuringElementEx(4, 4, 1, 1, CV_SHAPE_RECT);
        //腐蚀
        IplImage erosion_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvErode(temp1, erosion_dst, kernel, 4);

        cvShowImage("imgThreshold", erosion_dst);
        cvWaitKey(0);

        //膨胀
        IplImage dilate_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvDilate(erosion_dst, dilate_dst, kernel, 5);

        cvShowImage("imgThreshold", dilate_dst);
        cvWaitKey(0);

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        CvRect boundingBox;
        int index = 1;
        for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            //log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            index++;
            stationList.add(new StationPoint(boundingBox.x(), boundingBox.y(), "", ColorEnum.CYAN_AND_BLUE.getDesc()));
        }
    }

    private void getYellow(IplImage orgHsv, List<StationPoint> stationList) {
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(orgHsv, ColorConstant.yellow_min, ColorConstant.yellow_max, temp1);

        cvShowImage("imgThreshold", temp1);
        cvWaitKey(0);

        //创建一个结构元素
        IplConvKernel kernel = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_RECT);
        //腐蚀
        IplImage erosion_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvErode(temp1, erosion_dst, kernel, 5);

        cvShowImage("imgThreshold", erosion_dst);
        cvWaitKey(0);

        //膨胀
        IplImage dilate_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvDilate(erosion_dst, dilate_dst, kernel, 5);

        cvShowImage("imgThreshold", dilate_dst);
        cvWaitKey(0);

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        CvRect boundingBox;
        int index = 1;
        for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            index++;
            stationList.add(new StationPoint(boundingBox.x(), boundingBox.y(), "", ColorEnum.YELLOW.getDesc()));
        }

    }

    private void getGreen(IplImage orgHsv, List<StationPoint> stationList) {
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(orgHsv, ColorConstant.green_min, ColorConstant.green_max, temp1);
        //创建一个结构元素
        IplConvKernel kernel = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_RECT);
        //腐蚀
        IplImage erosion_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvErode(temp1, erosion_dst, kernel, 3);
        //膨胀
        IplImage dilate_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvDilate(erosion_dst, dilate_dst, kernel, 3);
        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        CvRect boundingBox;
        int index = 1;
        for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
            //使用矩形包装ptr
            boundingBox = cvBoundingRect(ptr, 0);
            //log.info("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            index++;
            stationList.add(new StationPoint(boundingBox.x(), boundingBox.y(), "", ColorEnum.GREEN.getDesc()));
        }
    }

    private void getColorTest(IplImage orgHsv) {
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        //5.将hsv图片过滤色域到 底片
        cvInRangeS(orgHsv, ColorConstant.cyan_blue_min, ColorConstant.cyan_blue_max, temp1);
        cvShowImage("imgThreshold", temp1);
        cvWaitKey(0);
        //创建一个结构元素
        // 矩形: MORPH_RECT
        //交叉形: MORPH_CROSS  这个肯定不行
        //椭圆形: MORPH_ELLIPSE
        IplConvKernel kernel = cvCreateStructuringElementEx(4, 4, 1, 1, CV_SHAPE_RECT);
        //腐蚀
        IplImage erosion_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvErode(temp1, erosion_dst, kernel, 4);
        cvShowImage("erosion_dst", erosion_dst);
        cvWaitKey(0);
        //膨胀
        IplImage dilate_dst = cvCreateImage(cvGetSize(orgHsv), 8, 1);
        cvDilate(erosion_dst, dilate_dst, kernel, 5);
        cvShowImage("dilate_dst", dilate_dst);
        cvWaitKey(0);

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, ptr, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        CvRect boundingBox;
        List<StationPoint> stationList = new ArrayList<>(100);

        int index = 1;
        //字体
        CvFont font = new CvFont();
        cvInitFont(font, CV_FONT_HERSHEY_COMPLEX, 0.5, 0.5, 1, 2, 8);
        for (; ptr != null && ptr.address() > 0L; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            // cvRectangle函数参数： 图片， 左上角， 右下角， 颜色， 线条粗细， 线条类型，点类型
            cvRectangle(dilate_dst, cvPoint(boundingBox.x(), boundingBox.y()),
                    cvPoint(boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar(0, 255, 255, 255), 2, 0, 0);
            cvPutText(dilate_dst, String.valueOf(index), cvPoint(boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height())
                    , font, cvScalar(0, 0, 0, 0));
            System.out.println("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            index++;
            // todo 获取对应点位的颜色
            stationList.add(new StationPoint(boundingBox.x(), boundingBox.y(), "", "color"));
        }
        cvShowImage("Contours", dilate_dst);
        cvWaitKey(0);
        //this.findStation(stationList);
    }

    public void main1() {
        //1.读取图片
        //IplImage orgImg = cvLoadImage("C:\\Users\\guofucheng\\Desktop\\temp\\temp1.jpg");
        IplImage orgImg = cvLoadImage("D:\\opencv\\20240324102739.jpg");
        //Mat myMat = imread("C:\\Users\\guofucheng\\Desktop\\temp\\opencv\\moban.jpg");
        //myMat.ptr().

        //2.创建一个等大小的hsv预备底片 （宽、高、色深8bit 10bit、channel 每个像素能存放的rgb值）
        IplImage hsv = IplImage.create(orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels());
        //3.rgb->hsv
        cvCvtColor(orgImg, hsv, CV_BGR2HSV);
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp2 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp3 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp4 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp5 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp6 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //5.将hsv图片过滤色域到 底片，可以处理多个色域范围
        //阈值化
        //过滤绿色、红色、青色（蓝色）
        cvInRangeS(hsv, ColorConstant.green_min, ColorConstant.green_max, temp1);
        cvInRangeS(hsv, ColorConstant.red_min1, ColorConstant.red_max1, temp2);
        cvInRangeS(hsv, ColorConstant.red_min2, ColorConstant.red_max2, temp2);
        cvInRangeS(hsv, ColorConstant.purple_min, ColorConstant.purple_max, temp3);
        cvInRangeS(hsv, ColorConstant.blue_min, ColorConstant.blue_max, temp4);
        cvInRangeS(hsv, ColorConstant.cyan_min, ColorConstant.cyan_max, temp5);
        cvInRangeS(hsv, ColorConstant.orange_min, ColorConstant.orange_max, temp6);
        //6.叠加不同的色域范围图像
        cvMax(temp1, temp2, imgThreshold);
        cvMax(temp3, imgThreshold, imgThreshold);
        cvMax(temp4, imgThreshold, imgThreshold);
        cvMax(temp5, imgThreshold, imgThreshold);
        cvMax(temp6, imgThreshold, imgThreshold);

        cvShowImage("imgThreshold", imgThreshold);
        cvWaitKey(0);

        //形态学闭处理
        //7.创建 像素只有一个通道的 底片
        IplImage Morphology_result = IplImage.create(orgImg.width(), orgImg.height(), IPL_DEPTH_8U, 1);

        // IplConvKernel  一个用于膨胀活腐蚀的结构元素  cvCreateStructuringElementEx 创建一个平面结构（cols列，rows行，图像偏移量xy，形状）
        // CV_SHAPE_RECT  矩形
        // 开运算
        IplConvKernel kernelCross = cvCreateStructuringElementEx(8, 8, 0, 0, CV_SHAPE_ELLIPSE);
        //闭运算
        //IplConvKernel kernelCross = cvCreateStructuringElementEx(5, 5,0,0, CV_SHAPE_RECT);
        //8.开运算:是先腐蚀,后膨胀的过程,可以去掉小的对象
        //  闭运算:是先膨后腐蚀的顺序 ,可以填充图像中细小的空洞  iterations 处理次数
        cvMorphologyEx(imgThreshold, Morphology_result, Morphology_result, kernelCross, MORPH_OPEN, 1);
        cvShowImage("Morphology_result", Morphology_result);
        cvWaitKey(0);


        //创建一个结构元素
        IplConvKernel kernel = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_RECT);
        //腐蚀
        IplImage erosion_dst = IplImage.create(orgImg.width(), orgImg.height(), IPL_DEPTH_8U, 1);
        cvErode(Morphology_result, erosion_dst, kernel, 3);
        cvShowImage("erosion_dst", erosion_dst);
        cvWaitKey(0);
        //膨胀
        IplImage dilate_dst = IplImage.create(orgImg.width(), orgImg.height(), IPL_DEPTH_8U, 1);
        cvDilate(erosion_dst, dilate_dst, kernel, 4);
        cvShowImage("dilate_dst", dilate_dst);
        cvWaitKey(0);

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        CvRect boundingBox;
        int index = 1;

        List<StationPoint> stationList = new ArrayList<>(100);

        //字体
        CvFont font = new CvFont();
        cvInitFont(font, CV_FONT_HERSHEY_COMPLEX, 0.5, 0.5, 1, 2, 8);
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            // cvRectangle函数参数： 图片， 左上角， 右下角， 颜色， 线条粗细， 线条类型，点类型
            cvRectangle(orgImg, cvPoint(boundingBox.x(), boundingBox.y()),
                    cvPoint(boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar(0, 255, 255, 255), 2, 0, 0);

            cvPutText(orgImg, String.valueOf(index), cvPoint(boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height())
                    , font, cvScalar(0, 0, 0, 0));


            System.out.println("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            /*System.out.println("boundingBox_index" + index + ".x     :     " + boundingBox.x());
            System.out.println("boundingBox_index" + index + ".y     :     " + boundingBox.y());*/
            /*System.out.println("boundingBox_index" + index + ".width     :     " + boundingBox.width());
            System.out.println("boundingBox_index" + index + ".height     :     " + boundingBox.height());*/
            index++;
            // 获取对应点位的颜色
            stationList.add(new StationPoint(boundingBox.x(), boundingBox.y(), "", "new Scalar()"));
        }
        cvShowImage("Contours", orgImg);
        cvWaitKey(0);
        stationDomain.findStationName(stationList);
    }

}
