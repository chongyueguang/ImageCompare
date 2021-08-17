package com.company.util;

import com.company.model.CompareFileModel;

import java.io.File;
import java.util.*;

public class FileUtils {

    /**
     * 获取指定文件夹路径下的全部以.png结尾的文件
     * @param fileHashMap 空map集合
     * @param file 指定路径下的文件夹
     * @param path 指定根路径
     * @return 以文件相对路径为key，文件为value的集合
     */
    public static HashMap<String, File> getAllPngFiles(HashMap<String,File> fileHashMap,File file,String path){
        File[] listFiles = file.listFiles();
        for (File f:listFiles) {
            if(f.isDirectory()){
                getAllPngFiles(fileHashMap,f,path);
            }
            if (f.isFile()&&f.getName().toLowerCase().endsWith(".png")){
                fileHashMap.put(f.getAbsolutePath().replace(path,""),f);
            }
        }
        return fileHashMap;
    }

    /**
     *
     * @param fMap
     * @param tMap
     * @return
     */
    public static ArrayList<CompareFileModel> getCompareFileArr(HashMap<String,File> fMap, HashMap<String,File> tMap){
        ArrayList<CompareFileModel> arrayList = new ArrayList<CompareFileModel>();
        Set fSet = fMap.entrySet();
        Iterator fIt = fSet.iterator();
        while (fIt.hasNext()){
            Map.Entry fEntry = (Map.Entry) fIt.next();
            if(tMap.containsKey(fEntry.getKey())){
                CompareFileModel compareFileModel = new CompareFileModel();
                compareFileModel.setKey(fEntry.getKey().toString());
                compareFileModel.setFromFile(fMap.get(fEntry.getKey()));
                compareFileModel.setToFile(tMap.get(fEntry.getKey()));
                arrayList.add(compareFileModel);
            }
        }
        return arrayList;
    }

}
