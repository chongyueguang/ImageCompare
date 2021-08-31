package com.company;

import com.company.model.RunThreadResModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;

public class Const {
    //停止ボタン状況
    public static boolean stopFlg = false;
    //
    public static int jobID = 0;
    //比較完了数
    public static int kannseiNum = 0;
    //比較完了数
    public static ArrayList<RunThreadResModel> runThreadResModels= new ArrayList<RunThreadResModel>();

    public static HSSFWorkbook wb;

}
