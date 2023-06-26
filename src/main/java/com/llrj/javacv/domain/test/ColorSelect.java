package com.llrj.javacv.domain.test;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.CvContour;
import org.bytedeco.opencv.opencv_core.CvMemStorage;
import org.bytedeco.opencv.opencv_core.CvRect;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.CvSeq;
import org.bytedeco.opencv.opencv_core.IplConvKernel;
import org.bytedeco.opencv.opencv_core.IplImage;

import static org.bytedeco.opencv.global.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.opencv.global.opencv_core.cvCreateImage;
import static org.bytedeco.opencv.global.opencv_core.cvGetSize;
import static org.bytedeco.opencv.global.opencv_core.cvInRangeS;
import static org.bytedeco.opencv.global.opencv_core.cvMax;
import static org.bytedeco.opencv.global.opencv_core.cvMaxS;
import static org.bytedeco.opencv.global.opencv_core.cvPoint;
import static org.bytedeco.opencv.global.opencv_core.cvScalar;
import static org.bytedeco.opencv.global.opencv_highgui.cvShowImage;
import static org.bytedeco.opencv.global.opencv_highgui.cvWaitKey;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_NONE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_CCOMP;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_TREE;
import static org.bytedeco.opencv.global.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCreateStructuringElementEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvCvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.cvDilate;
import static org.bytedeco.opencv.global.opencv_imgproc.cvErode;
import static org.bytedeco.opencv.global.opencv_imgproc.cvFindContours;
import static org.bytedeco.opencv.global.opencv_imgproc.cvMorphologyEx;
import static org.bytedeco.opencv.global.opencv_imgproc.cvRectangle;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_NONE;
import static org.opencv.imgproc.Imgproc.CV_SHAPE_RECT;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.pointPolygonTest;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description TODO
 * @date 2023-02-08
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
public class ColorSelect {

    //hsv绿色范围
/*    public static CvScalar g_min = cvScalar(35, 43, 46, 0);  //HSV色域
    public static CvScalar g_max= cvScalar(77, 255, 220, 0); //HSV色域*/


    public static CvScalar g_min = cvScalar(0, 43, 46, 0);  //HSV色域
    public static CvScalar g_max= cvScalar(150, 255, 220, 0); //HSV色域

    /**
     * 1.是否可以将整块路线图挖出
     * 2.获取五种颜色范围
     * 3.判断点数是否正确
     * 4.获取点位list, 并调整(需要根据图片， 调整一部分点位顺序， 其中list 获取是 从下往上， 从右到左 读取)
     * 5.
     * @param args
     */

    public static void main1(String[] args) {
        //读入图片
        IplImage orgImg = cvLoadImage("C:\\Users\\guofucheng\\Desktop\\temp\\temp1.jpg");

        cvShowImage( "orgImg", orgImg );
        cvWaitKey(0);

        test(orgImg);

        //rgb->hsv
        IplImage hsv = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels() );

        cvShowImage( "hsv", hsv );
        cvWaitKey(0);
        cvCvtColor( orgImg, hsv, CV_BGR2HSV );
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

        //阈值化
        cvInRangeS(hsv, g_min, g_max, imgThreshold);
        // todo 可以合并多个取值  获取多个阈值
        //cvMax();

        cvShowImage( "imgThreshold", imgThreshold );
        cvWaitKey(0);

        //形态学闭处理
        IplImage Morphology_result  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);

        cvShowImage( "Morphology_result", Morphology_result );
        cvWaitKey(0);

        IplConvKernel kernelCross = cvCreateStructuringElementEx(21, 21,7,7, CV_SHAPE_RECT);
        cvMorphologyEx(imgThreshold, Morphology_result, Morphology_result, kernelCross, MORPH_CLOSE, 1);
        //膨胀腐蚀
        IplImage erosion_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplImage dilate_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplConvKernel kernel=cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);
        cvErode( Morphology_result, erosion_dst, kernel,3);   //腐蚀
        cvDilate( erosion_dst, dilate_dst, kernel,4);   //膨胀

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, contours, Loader.sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        CvRect boundingBox;
        int index = 1;
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg , cvPoint( boundingBox.x(), boundingBox.y() ),
                    cvPoint( boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar( 0, 255, 255, 255 ), 2, 0, 0 );
            System.out.println("boundingBox_index" + index + ".x     :     " + boundingBox.x());
            System.out.println("boundingBox_index" + index + ".y     :     " + boundingBox.y());
            /*System.out.println("boundingBox_index" + index + ".width     :     " + boundingBox.width());
            System.out.println("boundingBox_index" + index + ".height     :     " + boundingBox.height());*/
            index++;
        }
        cvShowImage( "Contours", orgImg );
        cvWaitKey(0);
    }

    private static void test(IplImage dilate_dst) {
        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, contours, Loader.sizeof(CvContour.class) , CV_RETR_TREE, CV_CHAIN_APPROX_NONE, cvPoint(0,0));

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvRect boundingBox;
        int index = 1;
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            cvRectangle( dilate_dst , cvPoint( boundingBox.x(), boundingBox.y() ),
                    cvPoint( boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar( 0, 255, 255, 255 ), 2, 0, 0 );
            System.out.println("boundingBox_index" + index + ".x     :     " + boundingBox.x());
            System.out.println("boundingBox_index" + index + ".y     :     " + boundingBox.y());
            /*System.out.println("boundingBox_index" + index + ".width     :     " + boundingBox.width());
            System.out.println("boundingBox_index" + index + ".height     :     " + boundingBox.height());*/
            index++;
        }
        cvShowImage( "Contours", dilate_dst );
        cvWaitKey(0);
    }


}
