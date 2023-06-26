package com.llrj.javacv.domain.test;

import com.llrj.javacv.domain.cv.ColorConstant;
import com.llrj.javacv.domain.cv.StationPoint;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.CvContour;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.IplConvKernel;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvGetSize;
import static org.bytedeco.opencv.global.opencv_core.cvInRangeS;
import static org.bytedeco.opencv.global.opencv_core.cvMax;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_highgui.cvShowImage;
import static org.bytedeco.opencv.global.opencv_highgui.cvWaitKey;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.opencv.global.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCreateStructuringElementEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvErode;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvMorphologyEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.opencv.imgproc.Imgproc.CV_SHAPE_ELLIPSE;
import static org.opencv.imgproc.Imgproc.CV_SHAPE_RECT;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description TODO
 * @date 2023-02-14
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
public class Test {


    public static void main(String[] args) {
        //1.读取图片
        //IplImage orgImg = cvLoadImage("C:\\Users\\guofucheng\\Desktop\\temp\\temp1.jpg");
        IplImage orgImg = cvLoadImage("C:\\Users\\guofucheng\\Desktop\\temp\\opencv\\moban.jpg");

        //2.创建一个等大小的hsv预备底片 （宽、高、色深8bit 10bit、channel 每个像素能存放的rgb值）
        IplImage hsv = IplImage.create(orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels());
        //3.rgb->hsv
        cvCvtColor(orgImg, hsv, CV_BGR2HSV);
        //4.和上面的 IplImage.create 相同， 创建 像素只有一个通道的 底片
        IplImage temp1 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp2 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage temp3 = cvCreateImage(cvGetSize(orgImg), 8, 1);
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //5.将hsv图片过滤色域到 底片，可以处理多个色域范围
        //阈值化
        //过滤绿色、红色、青色（蓝色）
        cvInRangeS(hsv, ColorConstant.green_min, ColorConstant.green_max, temp1);
        cvInRangeS(hsv, ColorConstant.red_min2, ColorConstant.red_max2, temp2);
        cvInRangeS(hsv, ColorConstant.cyan_min, ColorConstant.cyan_max, temp3);
        // todo 可以合并多个取值  获取多个阈值
        //6.叠加不同的色域范围图像
        cvMax(temp1, temp2, imgThreshold);
        cvMax(temp3, imgThreshold, imgThreshold);

        cvShowImage("imgThreshold", imgThreshold);
        cvWaitKey(0);

        //形态学闭处理
        //7.创建 像素只有一个通道的 底片
        IplImage Morphology_result  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);

        // IplConvKernel  一个用于膨胀活腐蚀的结构元素  cvCreateStructuringElementEx 创建一个平面结构（cols列，rows行，图像偏移量xy，形状）
        // CV_SHAPE_RECT  矩形
        // 开运算
        IplConvKernel kernelCross = cvCreateStructuringElementEx(5, 5,0,0, CV_SHAPE_ELLIPSE);
        //闭运算
        //IplConvKernel kernelCross = cvCreateStructuringElementEx(5, 5,0,0, CV_SHAPE_RECT);
        //8.开运算:是先腐蚀,后膨胀的过程,可以去掉小的对象
        //  闭运算:是先膨后腐蚀的顺序 ,可以填充图像中细小的空洞  iterations 处理次数
        cvMorphologyEx(imgThreshold, Morphology_result, Morphology_result, kernelCross, MORPH_CLOSE, 1);
        cvShowImage("Morphology_result", Morphology_result);
        cvWaitKey(0);


        //膨胀腐蚀
        IplImage erosion_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplImage dilate_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplConvKernel kernel=cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);
        //腐蚀
        cvErode(Morphology_result, erosion_dst, kernel,3);
        cvShowImage("erosion_dst", erosion_dst);
        cvWaitKey(0);
        //膨胀
        cvDilate(erosion_dst, dilate_dst, kernel,4);
        cvShowImage("dilate_dst", dilate_dst);
        cvWaitKey(0);

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, contours, Loader.sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        CvRect boundingBox;
        int index = 1;

        List<StationPoint> stationList = new ArrayList<>(100);
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            cvRectangle(orgImg , cvPoint(boundingBox.x(), boundingBox.y()),
                    cvPoint(boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar(0, 255, 255, 255), 2, 0, 0);
            System.out.println("index" + index + "x=" + boundingBox.x() + ";y=" + boundingBox.y());
            /*System.out.println("boundingBox_index" + index + ".x     :     " + boundingBox.x());
            System.out.println("boundingBox_index" + index + ".y     :     " + boundingBox.y());*/
            /*System.out.println("boundingBox_index" + index + ".width     :     " + boundingBox.width());
            System.out.println("boundingBox_index" + index + ".height     :     " + boundingBox.height());*/
            index++;
        }
        cvShowImage("Contours", orgImg);
        cvWaitKey(0);


    }
}
