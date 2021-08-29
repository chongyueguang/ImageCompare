package com.company.util;

import com.company.model.CompareFileModel;
import com.company.model.ImageAttributeModel;
import com.company.model.ImageModel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

public class FileUtils {

    /**
     * 获取指定文件夹路径下的全部以.png结尾的文件
     * @param fileHashMap 空map集合
     * @param file 指定路径下的文件夹
     * @param path 指定根路径
     * @return 以文件相对路径为key，文件为value的集合
     */
    public static HashMap<String, ImageAttributeModel> getAllPngFiles(HashMap<String, ImageAttributeModel> fileHashMap, File file, String path,Properties pro){
        File[] listFiles = file.listFiles();
        for (File f:listFiles) {
            if(f.isDirectory()){
                getAllPngFiles(fileHashMap,f,path,pro);
            }
            if (f.isFile()&&f.getName().toLowerCase().endsWith(".png")){
                ImageAttributeModel imageAttributeModel = new ImageAttributeModel();
                imageAttributeModel.setFile(f);
                if(pro != null){
                    imageAttributeModel.setImageModel(FileUtils.readProToModel(pro,f));
                }else {
                    imageAttributeModel.setImageModel(new ArrayList<ImageModel>());
                }
                fileHashMap.put(f.getAbsolutePath().replace(path,""),imageAttributeModel);
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
    public static ArrayList<CompareFileModel> getCompareFileArr(HashMap<String,ImageAttributeModel> fMap, HashMap<String,ImageAttributeModel> tMap){
        ArrayList<CompareFileModel> arrayList = new ArrayList<CompareFileModel>();
        Set fSet = fMap.entrySet();
        Iterator fIt = fSet.iterator();
        while (fIt.hasNext()){
            Map.Entry fEntry = (Map.Entry) fIt.next();
            if(tMap.containsKey(fEntry.getKey())){
                CompareFileModel compareFileModel = new CompareFileModel();
                compareFileModel.setKey(fEntry.getKey().toString());
                compareFileModel.setFromFile(fMap.get(fEntry.getKey()).getFile());
                compareFileModel.setToFile(tMap.get(fEntry.getKey()).getFile());
                compareFileModel.setFromImageModel(fMap.get(fEntry.getKey()).getImageModel());
                compareFileModel.setToImageModel(tMap.get(fEntry.getKey()).getImageModel());
                arrayList.add(compareFileModel);
            }
        }
        return arrayList;
    }

    public static Properties getProperties(File file){
        File[] listFiles = file.listFiles();
        for (File f:listFiles) {
            if("ignoreAreas.properties".equals(f.getName())){
                Properties prop = null;
                try {
                    InputStream reader = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(reader));
                    prop = new Properties();
                    prop.load(br);
                    br.close();
                    return prop;
                } catch (MalformedURLException mue) {
                    mue.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return null;
    }

    public static List<ImageModel> readProToModel(Properties pro,File file){
        try {
            Iterator it=pro.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry=(Map.Entry)it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                String matchKey = key.toString().replace("*",".*");
                if (file.getName().matches(matchKey)){
                    String[] split = value.toString().split("],");
                    List<ImageModel> imageModels = new ArrayList<>();
                    for (int i = 0;i < split.length;i++){
                        if(split[i].endsWith("]")){
                            split[i] = split[i].substring(0,split[i].length()-1);
                        }
                        String[] split1 = split[i].replace("[", "").split(",");
                        if (split1.length == 4){
                            ImageModel imageModel = new ImageModel();
                            BufferedImage image = null;
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException e) {
                                LogUtils.error(file.getName() + "ファイル不正");
                                e.printStackTrace();
                            }
                            imageModel.setLtx(Double.parseDouble(split1[0])/image.getWidth());
                            imageModel.setLty(Double.parseDouble(split1[1])/image.getHeight());
                            imageModel.setRbx(Double.parseDouble(split1[2])/image.getWidth());
                            imageModel.setRby(Double.parseDouble(split1[3])/image.getHeight());
                            imageModels.add(imageModel);
                        }
                    }
                    return imageModels;
                }
            }
        }catch (Exception e){
            LogUtils.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
