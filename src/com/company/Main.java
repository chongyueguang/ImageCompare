package com.company;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.company.model.DetectionsModel;
import com.company.model.ImageModel;
import com.company.model.ImageResponseModel;
import com.company.model.ResultInfoModel;
import com.company.util.ImageChangeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        String base64Str = ImageChangeUtils.imageToBase64ByFile(new File("C:\\Users\\hzq\\Desktop\\To\\333\\222.png"));
//        System.out.println(base64Str);
//
//        boolean b = ImageChangeUtils.base64StrToImage(base64Str, "C:\\Users\\hzq\\Desktop\\To\\333\\333.png");
//        System.out.println(b);

        try{
            new MainForm();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
