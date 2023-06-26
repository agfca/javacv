package com.llrj.javacv.domain.task;

import com.llrj.javacv.domain.DeleteFileDomain;
import com.llrj.javacv.domain.cv.JavaCVDomain;
import com.llrj.javacv.domain.photo.PhotoDomain;
import com.llrj.javacv.domain.ping.PingFileDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 定时任务服务
 * @date 2023-03-05
 * @copyright 2022 iwhalecloud . All rights reserved.
 */

@Slf4j
@Component
public class ScheduledService {

    @Autowired
    private JavaCVDomain javaCVDomain;

    @Autowired
    private PingFileDomain pingFileDomain;

    @Autowired
    private DeleteFileDomain deleteFileDomain;

    /**
     * 调用摄像头拍照
     */
    //@Scheduled(fixedDelay = 5000)
    public void scheduled0(){
        //photoDomain.
        //emailDomain.someMethod();
    }

    /**
     * 文件删除
     */
    @Scheduled(cron = "${scheduled.deleteFile}")
    public void deleteFile() {
        //deleteFileDomain.main();
    }

    /**
     * 图片识别
     */
    @Scheduled(fixedDelayString = "${scheduled.pingFile}")
    public void scheduled1() {
        javaCVDomain.main();
    }

    /**
     * ping文件检测
     */
    @Scheduled(fixedDelayString = "${scheduled.pingFile}")
    public void scheduled2() {
        pingFileDomain.main();
    }

}