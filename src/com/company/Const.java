package com.company;

import com.company.model.RunThreadResModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;

public class Const {
    //停止ボタン状況
    public static boolean stopFlg = false;
    //jobID
    public static int jobID = 0;
    //比較完了数
    public static int kannseiNum = 0;
    //レポート作成リスト
    public static ArrayList<RunThreadResModel> runThreadResModels= new ArrayList<RunThreadResModel>();
    //Excel操作用
    public static HSSFWorkbook wb;
    //runtimeフラグ 0:待ち　1:ランニング　2:完了
    public static int successRunFlg = 0;

}
