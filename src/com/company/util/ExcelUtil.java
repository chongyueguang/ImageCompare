package com.company.util;

import com.company.model.CompareFileModel;
import com.company.model.ImageModel;
import com.company.model.ImageReqInfoModel;
import com.company.model.ResultInfoModel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,HSSFWorkbook wb){

        //excel标题
        String[] title1 = {"実行日","期待値ファイル","検証ファイル","比較結果ファイル","AIエンジン結果","","","期待値ファイル\n" +
                "マスク情報"};
        String[] title2 = {"検証ファイル\n" +
                "マスク情報","テストチーム\n" +
                "検証結果","テスト設計チーム検証結果","","","アプリチーム検証結果","","","統括検証結果","",""};
        String[] title3 = {"","","","","精度","ステータス","比較結果",""};
        String[] title4 = {"","","検証者","検証時間","コメント","検証者","検証時間","コメント","検証者","検証時間","コメント"};

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row1 = sheet.createRow(0);
        row1.setHeightInPoints(20);
        HSSFRow row2 = sheet.createRow(1);
        row2.setHeightInPoints((float) 33.75);
        HSSFRow row3 = sheet.createRow(2);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        // 居中格式
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style1.setBottomBorderColor(HSSFColor.BLACK.index);
        style1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style1.setWrapText(true);
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style1.setFillForegroundColor(HSSFColor.YELLOW.index);
        style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 创建字体样式
        HSSFFont headerFont1 = (HSSFFont) wb.createFont();
        // 字体加粗
        headerFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 设置字体类型
        headerFont1.setFontName("ＭＳ Ｐゴシック");
        // 设置字体大小
        headerFont1.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        style1.setFont(headerFont1);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        // 居中格式
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style2.setBottomBorderColor(HSSFColor.BLACK.index);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setWrapText(true);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style2.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 创建字体样式
        HSSFFont headerFont2 = (HSSFFont) wb.createFont();
        // 字体加粗
        headerFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 设置字体类型
        headerFont2.setFontName("ＭＳ Ｐゴシック");
        // 设置字体大小
        headerFont2.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        style2.setFont(headerFont2);

        //声明列对象
        HSSFCell cell1 = null;
        HSSFCell cell2 = null;

        //创建标题
        for(int i=0;i<title1.length;i++){
            cell1 = row1.createCell(i);
            cell1.setCellValue(title1[i]);
            cell1.setCellStyle(style1);
        }
        //创建标题
        for(int i=0;i<title3.length;i++){
            cell2 = row2.createCell(i);
            cell2.setCellValue(title3[i]);
            cell2.setCellStyle(style1);
        }
        //创建标题
        for(int j=0;j<title2.length;j++){
            cell1 = row1.createCell(title1.length+j);
            cell1.setCellValue(title2[j]);
            cell1.setCellStyle(style2);
        }
        for(int j=0;j<title4.length;j++){
            cell2 = row2.createCell(title3.length+j);
            cell2.setCellValue(title4[j]);
            cell2.setCellStyle(style2);
        }

        CellRangeAddress region1 = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress region2 = new CellRangeAddress(0, 1, 1, 1);
        CellRangeAddress region3 = new CellRangeAddress(0, 1, 2, 2);
        CellRangeAddress region4 = new CellRangeAddress(0, 1, 3, 3);
        CellRangeAddress region5 = new CellRangeAddress(0, 0, 4, 6);
        CellRangeAddress region6 = new CellRangeAddress(0, 1, 7, 7);
        CellRangeAddress region7 = new CellRangeAddress(0, 1, 8, 8);
        CellRangeAddress region8 = new CellRangeAddress(0, 1, 9, 9);
        CellRangeAddress region9 = new CellRangeAddress(0, 0, 10, 12);
        CellRangeAddress region10 = new CellRangeAddress(0, 0, 13, 15);
        CellRangeAddress region11 = new CellRangeAddress(0, 0, 16, 18);
        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        sheet.addMergedRegion(region3);
        sheet.addMergedRegion(region4);
        sheet.addMergedRegion(region5);
        sheet.addMergedRegion(region6);
        sheet.addMergedRegion(region7);
        sheet.addMergedRegion(region8);
        sheet.addMergedRegion(region9);
        sheet.addMergedRegion(region10);
        sheet.addMergedRegion(region11);
        sheet.setColumnWidth(0,9*256);
        sheet.setColumnWidth(1,13*256);
        sheet.setColumnWidth(2,16*256);
        sheet.setColumnWidth(3,15*256);
        sheet.setColumnWidth(4,6*256);
        sheet.setColumnWidth(5,9*256);
        sheet.setColumnWidth(6,9*256);
        sheet.setColumnWidth(7,30*256);
        sheet.setColumnWidth(8,12*256);
        sheet.setColumnWidth(9,11*256);
        sheet.setColumnWidth(10,8*256);
        sheet.setColumnWidth(11,10*256);
        sheet.setColumnWidth(12,9*256);
        sheet.setColumnWidth(13,8*256);
        sheet.setColumnWidth(14,10*256);
        sheet.setColumnWidth(15,9*256);
        sheet.setColumnWidth(16,8*256);
        sheet.setColumnWidth(17,10*256);
        sheet.setColumnWidth(18,9*256);

        return wb;
    }

    public static HSSFWorkbook setHSSFWorkbookValue(String sheetName, HSSFWorkbook wb, int rowNum, ResultInfoModel resultInfoModel, CompareFileModel compareFileModel, JTextField txt_new){
        HSSFSheet sheet = wb.getSheet(sheetName);
        HSSFRow row3 = sheet.createRow(rowNum + 2);
        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style3 = wb.createCellStyle();
        style3.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        // 居中格式
        style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style3.setBottomBorderColor(HSSFColor.BLACK.index);
        style3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style3.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style3.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 创建字体样式
        HSSFFont headerFont3 = (HSSFFont) wb.createFont();
        // 字体加粗
        headerFont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 设置字体类型
        headerFont3.setFontName("ＭＳ Ｐゴシック");
        // 设置字体大小
        headerFont3.setFontHeightInPoints((short) 11);
        // 为标题样式设置字体样式
        style3.setFont(headerFont3);

        HSSFCell cell = null;
        //创建内容
        //実行日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        HSSFCell cell1 = row3.createCell(0);
        cell1.setCellValue(df.format(new Date()));
        cell1.setCellStyle(style3);
        //期待値ファイル
        HSSFCell cell2 = row3.createCell(1);
        cell2.setCellValue("OLD" + compareFileModel.getFromFile().getName());
        HSSFHyperlink  link1 = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link1.setAddress(txt_new.getText() +"\\RESULT\\実行完了現エビデンス\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        cell2.setHyperlink(link1);
        cell2.setCellStyle(style3);
        //検証ファイル
        HSSFCell cell3 = row3.createCell(2);
        cell3.setCellValue("NEW" + compareFileModel.getToFile().getName());
        HSSFHyperlink  link2 = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link2.setAddress(txt_new.getText() +"\\RESULT\\実行完了新エビデンス\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        cell3.setHyperlink(link2);
        cell3.setCellStyle(style3);
        //比較結果ファイル
        ImageChangeUtils.base64StrToImage(resultInfoModel.getData().getDiffImage1(),txt_new.getText() +"\\RESULT\\TEMPOLD\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        ImageChangeUtils.base64StrToImage(resultInfoModel.getData().getDiffImage2(),txt_new.getText() +"\\RESULT\\TEMPNEW\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        ImageChangeUtils.joinImage(new File(txt_new.getText() +"\\RESULT\\TEMPOLD\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName()),
                new File(txt_new.getText() +"\\RESULT\\TEMPNEW\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName()),
                txt_new.getText() +"\\RESULT\\比較結果\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        HSSFCell cell4 = row3.createCell(3);
        cell4.setCellValue("Diff" + compareFileModel.getToFile().getName());
        HSSFHyperlink  link3 = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link3.setAddress(txt_new.getText() +"\\RESULT\\比較結果\\"+ compareFileModel.getToFile().getPath() + "\\" + compareFileModel.getToFile().getName());
        cell4.setHyperlink(link3);
        cell4.setCellStyle(style3);
        //精度
        HSSFCell cell5 = row3.createCell(4);
        cell5.setCellValue(resultInfoModel.getData().getStatistic().getConfAvg());
        cell5.setCellStyle(style3);
        //ステータス
        HSSFCell cell6 = row3.createCell(5);
        cell6.setCellValue(resultInfoModel.getMessage());
        cell6.setCellStyle(style3);
        //比較結果
        HSSFCell cell7 = row3.createCell(6);
        if(!"正常".equals(resultInfoModel.getMessage())){
            cell7.setCellValue("-");
        }else {
            cell7.setCellValue(resultInfoModel.getData().getDiffResult());
        }
        cell7.setCellStyle(style3);
        //期待値ファイルマスク情報
        HSSFCell cell8 = row3.createCell(7);
        List<ImageModel> list = compareFileModel.getFromImageModel();
        String mask = "";
        for (ImageModel imageModel:list) {
            String Ltx = imageModel.getLtx().toString();
            String Lty = imageModel.getLty().toString();
            String Rbx = imageModel.getRbx().toString();
            String Rby = imageModel.getRby().toString();
            mask += mask + ",("+ Ltx + "," + Lty + "," + Rbx + "," + Rby + ")";
        }
        mask.replace(",","");
        cell8.setCellValue(mask);
        cell8.setCellStyle(style3);
        //検証ファイルマスク情報
        HSSFCell cell9 = row3.createCell(8);
        cell9.setCellStyle(style3);
        //テストチーム検証結果
        HSSFCell cell10 = row3.createCell(9);
        cell10.setCellStyle(style3);
        //検証者
        HSSFCell cell11 = row3.createCell(10);
        cell11.setCellStyle(style3);
        //検証時間
        HSSFCell cell12 = row3.createCell(11);
        cell12.setCellStyle(style3);
        //コメント
        HSSFCell cell13 = row3.createCell(12);
        cell13.setCellStyle(style3);
        //検証者
        HSSFCell cell14 = row3.createCell(13);
        cell14.setCellStyle(style3);
        //検証時間
        HSSFCell cell15 = row3.createCell(14);
        cell15.setCellStyle(style3);
        //コメント
        HSSFCell cell16 = row3.createCell(15);
        cell16.setCellStyle(style3);
        //検証者
        HSSFCell cell17 = row3.createCell(16);
        cell17.setCellStyle(style3);
        //検証時間
        HSSFCell cell18 = row3.createCell(17);
        cell18.setCellStyle(style3);
        //コメント
        HSSFCell cell19 = row3.createCell(18);
        cell19.setCellStyle(style3);
        return wb;
    }
}