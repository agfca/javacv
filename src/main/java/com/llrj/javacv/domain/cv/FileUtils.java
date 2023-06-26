package com.llrj.javacv.domain.cv;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 文件utils
 * @date 2023-04-26
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
public class FileUtils {

    /**
     * 获取文件夹下最新图片
     * @param fileDir
     * @return
     */
    public static String getLatestImgFilePath(String fileDir){
        File path = new File(fileDir);
        //列出该目录下所有文件和文件夹
        File[] files = path.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        //按照目录中文件最后修改日期实现倒序排序
        File lastFile = Arrays.stream(files).max(Comparator.comparing(File::lastModified)).orElse(null);
        //取最新修改的文件，get文件名
        return lastFile.getPath();
    }

}
