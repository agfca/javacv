package com.llrj.javacv.domain;

import com.llrj.javacv.domain.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 删除文件
 * @date 2023-03-16
 */

@Slf4j
@Service
public class DeleteFileDomain {

    @Autowired
    private AppConfig appConfig;

    public void main() {
        //删除一个文件夹下的所有文件(包括子目录内的文件)
        File file = new File(appConfig.getOpencvDir());//输入要删除文件目录的绝对路径
        deleteFile(file);
    }
    public void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            return;
        }
        log.info("删除文件数量：{}", files.length);
        //遍历该目录下的文件对象
        for (File f: files){
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
    }

}
