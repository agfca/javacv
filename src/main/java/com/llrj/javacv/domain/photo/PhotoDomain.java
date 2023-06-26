package com.llrj.javacv.domain.photo;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import com.llrj.javacv.domain.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 摄像头拍照
 * @date 2023-02-10
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
@Slf4j
@Service
public class PhotoDomain {

    @Autowired
    private AppConfig appConfig;

    public static void main1(String[] args) throws FrameGrabber.Exception, InterruptedException {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);//0表示本机摄像头  当然这里也可以换成网络摄像头地址
        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//窗口关闭时程序运行结束
        canvas.setAlwaysOnTop(true);
        int i=0;
        while(true){
            if(!canvas.isDisplayable()){//窗口是否关闭
                System.out.println("已关闭");
                grabber.stop();//停止抓取
                System.exit(2);//退出
            }
            canvas.showImage(grabber.grab());//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame表示一帧视频图像
            //调用doExecuteFrame()方法，将截取的图片保存在本地
            doExecuteFrame(grabber.grabFrame(),"d:/pingimg/"+i+".jpg");
            Thread.sleep(1000);//50毫秒刷新一次图像
            i++;
        }
    }

    /**
     *
     * @param f 表示帧
     * @param targetFileName 存储路径
     */
    public static void doExecuteFrame(Frame f, String targetFileName) {
        if (null ==f ||null ==f.image) {
            return;
        }
        Java2DFrameConverter converter =new Java2DFrameConverter();
        BufferedImage bi =converter.getBufferedImage(f);
        File output =new File(targetFileName);
        try {
            ImageIO.write(bi,"jpg",output);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static JFrame window;

    /*
    opencv 摄像头
    https://blog.csdn.net/ruyujiexys/article/details/120370757
    https://www.cnblogs.com/muphy/p/12940804.html
    https://learn.microsoft.com/zh-cn/windows/uwp/audio-video-camera/capture-photos-and-video-with-cameracaptureui

    javaScript 截图
    https://cloud.tencent.com/developer/article/1641490
    https://blog.csdn.net/qq_31788297/article/details/51537380

    https://cloud.tencent.com/developer/article/1537408

    2560*1440
     */
    public static void main(String[] args) throws InterruptedException {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.HD.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        JFrame window = new JFrame("Test webcam panel");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        final JButton button = new JButton("拍照");
        window.add(panel, BorderLayout.CENTER);
        window.add(button, BorderLayout.SOUTH);
        window.setResizable(true);
        window.pack();
        window.setVisible(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);  //设置按钮不可点击
                //实现拍照保存-------start
                String fileName = "D://" + System.currentTimeMillis();       //保存路径即图片名称（不用加后缀）
                WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null, "拍照成功");
                        button.setEnabled(true);    //设置按钮可点击

                        return;
                    }
                });
                //实现拍照保存-------end
            }
        });
    }

}
