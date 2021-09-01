/*
 * Created by JFormDesigner on Thu Aug 12 17:00:33 JST 2021
 */

package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.*;
import com.company.model.*;
import com.company.service.TblJobInfoService;
import com.company.util.ExcelUtils;
import com.company.util.FileUtils;
import com.company.util.LogUtils;
import com.company.service.TimerService;
import com.company.util.UpdateTableUIUtils;
import com.company.work.WaitWork;

/**
 * @author 1
 */
public class MainForm extends JFrame {

    public MainForm() {
        initComponents();
    }

    /**
     *現参照ボタン
     * @param e
     */
    private void btnOldActionPerformed(ActionEvent e) {
        jfilechooser1.setFileSelectionMode(1);
        //ファイルを保存する
        int a = jfilechooser1.showSaveDialog(null);
        if(a == jfilechooser1.APPROVE_OPTION){
            File f = jfilechooser1.getSelectedFile();
            txt_old.setText(f.getAbsolutePath());
        }
    }

    /**
     *新参照ボタン
     * @param e
     */
    private void btnNewActionPerformed(ActionEvent e) {
        jfilechooser2.setFileSelectionMode(1);
        //ファイルを保存する
        int a = jfilechooser2.showSaveDialog(null);
        if(a == jfilechooser2.APPROVE_OPTION){
            File f = jfilechooser2.getSelectedFile();
            txt_new.setText(f.getAbsolutePath());
        }
    }

    /**
     * 実行ボタン
     * @param e
     * @throws IOException
     * @throws InterruptedException
     */
    private void btnRunActionPerformed(ActionEvent e) throws IOException, InterruptedException {
        btn_run.setEnabled(true);
        File f1 = jfilechooser1.getSelectedFile();
        File f2 = jfilechooser2.getSelectedFile();
        //メールチェック
        if("".equals(txt_mail.getText()) || txt_mail.getText() == null){
            JOptionPane.showMessageDialog(null, "メールを入力してください。");
            LogUtils.error("メールを入力してください。");
            return;
        }
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        if(!regex.matcher(txt_mail.getText()).matches()){
            JOptionPane.showMessageDialog(null, "メールフォーマット不正。");
            LogUtils.error("メールフォーマット不正。");
            return;
        }
        //ファイル格納場所(現)チェック
        if("".equals(txt_old.getText()) || txt_old.getText() == null){
            JOptionPane.showMessageDialog(null, "ファイル格納場所(現)を入力してください。");
            LogUtils.error("ファイル格納場所(現)を入力してください。");
            return;
        }
        //ファイル格納場所(新)チェック
        if("".equals(txt_new.getText()) || txt_new.getText() == null){
            JOptionPane.showMessageDialog(null, "ファイル格納場所(新)を入力してください。");
            LogUtils.error("ファイル格納場所(新)を入力してください。");
            return;
        }
        //创建RESULT文件夹
        File fileCompare = new File(txt_new.getText()+"\\RESULT\\比較結果");
        if((!fileCompare.exists()) && (!fileCompare.mkdirs())){
            JOptionPane.showMessageDialog(null, "比較結果Folder作成失敗");
            LogUtils.error("比較結果Folder作成失敗");
            return;
        }
        String oldFolder = txt_old.getText();
        oldFolder = oldFolder.substring(oldFolder.lastIndexOf("\\"),oldFolder.length()) ;
        File fileOldEvidence = new File(txt_new.getText()+"\\RESULT\\"+oldFolder);
        if((!fileOldEvidence.exists()) && (!fileOldEvidence.mkdirs())){
            JOptionPane.showMessageDialog(null,  oldFolder + "Folder作成失敗");
            LogUtils.error( oldFolder + "Folder作成失敗");
            return;
        }
        String newFolder = txt_new.getText();
        newFolder = newFolder.substring(newFolder.lastIndexOf("\\"),newFolder.length()) ;
        File fileNewEvidence = new File(txt_new.getText()+"\\RESULT\\" + newFolder);
        if((!fileNewEvidence.exists()) && (!fileNewEvidence.mkdirs())){
            JOptionPane.showMessageDialog(null,  newFolder + "Folder作成失敗");
            LogUtils.error( newFolder + "Folder作成失敗");
            return;
        }
        File fileTempOldEvidence = new File(txt_new.getText()+"\\RESULT\\TEMPOLD");
        if((!fileTempOldEvidence.exists()) && (!fileTempOldEvidence.mkdirs())){
            JOptionPane.showMessageDialog(null,  " TEMPOLD Folder作成失敗");
            LogUtils.error("TEMPOLD Folder作成失敗");
            return;
        }
        File fileTempNewEvidence = new File(txt_new.getText()+"\\RESULT\\TEMPNEW");
        if((!fileTempNewEvidence.exists()) && (!fileTempNewEvidence.mkdirs())){
            JOptionPane.showMessageDialog(null,  " TEMPNEW Folder作成失敗");
            LogUtils.error("TEMPNEW Folder作成失敗");
            return;
        }
        //OLDフォルダの子フォルダ作成
        ArrayList<String> oldFolderList = new ArrayList<>();
        oldFolderList =FileUtils.getAllPath(oldFolderList,f1,txt_old.getText());
        for(int i = 0;i<oldFolderList.size();i++) {
            File childFolder = new File(txt_new.getText()+"\\RESULT\\"+oldFolder + "\\" + oldFolderList.get(i));
            File childTempFolder = new File(txt_new.getText()+"\\RESULT\\TEMPOLD\\" + oldFolderList.get(i));
            File compareChildFolder = new File(txt_new.getText()+"\\RESULT\\比較結果\\" + oldFolderList.get(i));
            if((!childFolder.exists()) && (!childFolder.mkdirs())){
                JOptionPane.showMessageDialog(null, "比較結果Folder作成失敗");
                LogUtils.error("比較結果Folder作成失敗" + childFolder);
                return;
            }
            if((!childTempFolder.exists()) && (!childTempFolder.mkdirs())){
                JOptionPane.showMessageDialog(null, "比較結果TEMP Folder作成失敗");
                LogUtils.error("比較結果TEMP Folder作成失敗" + childTempFolder);
                return;
            }
            if((!compareChildFolder.exists()) && (!compareChildFolder.mkdirs())){
                JOptionPane.showMessageDialog(null, "比較結果Compare Folder作成失敗");
                LogUtils.error("比較結果Compare Folder作成失敗" + compareChildFolder);
                return;
            }
        }
        //NEWフォルダの子フォルダ作成
        ArrayList<String> newFolderList = new ArrayList<>();
        newFolderList =FileUtils.getAllPath(newFolderList,f2,txt_new.getText());
        for(int i = 0;i<newFolderList.size();i++) {
            File childFolder = new File(txt_new.getText()+"\\RESULT\\"+ newFolder + "\\" + newFolderList.get(i));
            File childTempFolder = new File(txt_new.getText()+"\\RESULT\\TEMPNEW\\" + newFolderList.get(i));
            if((!childFolder.exists()) && (!childFolder.mkdirs())){
                JOptionPane.showMessageDialog(null, "比較結果Folder作成失敗");
                LogUtils.error("比較結果Folder作成失敗" + childFolder);
                return;
            }
            if((!childTempFolder.exists()) && (!childTempFolder.mkdirs())){
                JOptionPane.showMessageDialog(null, "比較結果Folder作成失敗");
                LogUtils.error("比較結果Folder作成失敗" + childTempFolder);
                return;
            }
        }

        Properties prop = new Properties();
//        FileOutputStream fileOutputStream = new FileOutputStream(MainForm.class.getResource("/temp.properties").getFile());
//        String confPath = System.getProperty("user.dir") + "\\temp.properties";
//        FileOutputStream fileOutputStream = new FileOutputStream(confPath);
//        prop.setProperty("mainAddress", txt_mail.getText());
//        prop.store(fileOutputStream, new Date().toString());
//        fileOutputStream.flush();
//        fileOutputStream.close();

        fMap = new HashMap<>();
        //propertiesファイルによって、ignoreAreasを取得
        Properties properties1 = FileUtils.getProperties(f1);
        //全体pngファイルを取得
        fMap = FileUtils.getAllPngFiles(fMap,f1,f1.getAbsolutePath(),properties1);

        tMap = new HashMap<>();
        //propertiesファイルによって、ignoreAreasを取得
        Properties properties2 = FileUtils.getProperties(f2);
        //全体pngファイルを取得
        tMap = FileUtils.getAllPngFiles(tMap,f2,f2.getAbsolutePath(),properties2);

        //比較ファイルを取得
        ArrayList<CompareFileModel> compareFileArr = FileUtils.getCompareFileArr(fMap, tMap);
        if( compareFileArr.size() == 0 ){
            JOptionPane.showMessageDialog(null,  "比較ファイルない");
            LogUtils.error("比較ファイルない");
            return;
        }
        TblJobInfoService tblJobInfoService = new TblJobInfoService();
        //insert data
        Const.jobID = tblJobInfoService.insertJobInfoByJobID(txt_mail.getText(), compareFileArr.size());
        UpdateTableUIUtils.UpdTblUI(scrollPane1,table1,column,tblJobInfoService.getJobInfoByListToVector());
        waitWork = new WaitWork(compareFileArr,txt_new,txt_old);

        waitWork.execute();
        btn_run.setEnabled(false);
    }

    private void btnStopActionPerformed(ActionEvent e) {
        if(Const.successRunFlg != 2){
            if(waitWork != null){
                waitWork.cancel(true);
                //ストップの変更flg
                Const.stopFlg = true;

                if(Const.successRunFlg == 1){
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    int i = 0;
                    for (RunThreadResModel runThreadResModel : Const.runThreadResModels) {

                        try {
                            ResultInfoModel resultInfoModel1 = runThreadResModel.getResultInfoModel();
                            CompareFileModel compareFileModel = runThreadResModel.getCompareFileModel();
                            try {
                                Const.wb = ExcelUtils.setHSSFWorkbookValue("sheet1", Const.wb, i, resultInfoModel1, compareFileModel, txt_new,txt_old);
                                i++;
                                Const.kannseiNum = i;
                            }catch (Exception ex){
                                LogUtils.error(ex.getMessage());
                                ex.printStackTrace();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    Date date1 = new Date();
                    DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(txt_new.getText() +"\\RESULT\\比較結果レポート"+format.format(date1)+".xls");
                        //OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(txt_new.getText() + "\\RESULT\\比較結果レポート" + format.format(date1) + ".xls"), "utf-8");
                        Const.wb.write(os);
                        os.flush();
                        os.close();
                        FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPOLD"));
                        FileUtils.deleteFolder(new File(txt_new.getText() +"\\RESULT\\TEMPNEW"));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
        //このデータのステータスを変更します
        TblJobInfoService tblJobInfoService = new TblJobInfoService();
        tblJobInfoService.updateJobInfoEndTimeByJobIDForStop(Const.jobID,Const.kannseiNum);

        //结束主线程
        System.exit(0);
    }

    private void initComponents() {

        jfilechooser1 = new JFileChooser();
        jfilechooser2 = new JFileChooser();
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        //table1 = new JTable();
        label2 = new JLabel();
        txt_old = new JTextField();
        label4 = new JLabel();
        txt_new = new JTextField();
        btn_new = new JButton();
        label5 = new JLabel();
        btn_run = new JButton();
        btn_stop = new JButton();
        txt_mail = new JTextField();
        btn_old = new JButton();
        label3 = new JLabel();

        //======== this ========
        setTitle("\u73fe\u65b0\u6bd4\u8f03\u30c4\u30fc\u30eb");
        setResizable(false);
        setVisible(true);
        Container contentPane = getContentPane();

        txt_new.setEnabled(false);
        txt_old.setEnabled(false);

        //---- label1 ----
        label1.setText("\u73fe\u65b0\u6bd4\u8f03\u5b9f\u65bd");
        label1.setBackground(new Color(51, 153, 255));
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));

        //======== scrollPane1 ========
        {

            //---- table1 ----
            TblJobInfoService tblJobInfoService = new TblJobInfoService();
            column = new Vector<>();
            column.add("\u5b9f\u65bd\u8005");
            column.add("\u30b9\u30c6\u30fc\u30bf\u30b9");
            column.add("\u4e88\u6e2c\u5b8c\u4e86\u6642\u9593");
            column.add("\u5b8c\u4e86\u6570/\u5168\u4f53");
            column.add("\u6295\u5165\u6642\u9593");
            column.add("\u958b\u59cb\u6642\u9593");
            column.add("\u66f4\u65b0\u6642\u9593");

            try{
                table1 = new JTable(tblJobInfoService.getJobInfoByListToVector(),column);
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "DB属性不正");
                System.exit(0);
            }

            table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            {
                TableColumnModel cm = table1.getColumnModel();
                cm.getColumn(0).setMinWidth(60);
            }

            table1.setGridColor(Color.black);
            table1.setSelectionForeground(Color.white);
            table1.setForeground(Color.black);
            table1.setBorder(UIManager.getBorder("EditorPane.border"));
            table1.setRowHeight(15);
            scrollPane1.setViewportView(table1);
            //画面タイマーの定時更新
            TimerService.refreshScreenTimer(scrollPane1,table1,column);
        }

        //---- label2 ----
        label2.setText("\u5b9f\u65bd\u8005\uff08\u30e1\u30fc\u30eb\uff09");

        //---- label4 ----
        label4.setText("\u30d5\u30a1\u30a4\u30eb\u683c\u7d0d\u5834\u6240(\u65b0)");

        //---- btn_new ----
        btn_new.setText("\u53c2\u7167");
        btn_new.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_new.addActionListener(e -> btnNewActionPerformed(e));

        //---- label5 ----
        label5.setText("\u30d5\u30a1\u30a4\u30eb\u683c\u7d0d\u5834\u6240(\u73fe)");

        //---- btn_run ----
        btn_run.setText("\u5b9f\u884c");
        btn_run.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_run.addActionListener(e -> {
            try {
                btnRunActionPerformed(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        //---- btn_stop ----
        btn_stop.setText("\u4e2d\u6b62");
        btn_stop.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_stop.addActionListener(e -> btnStopActionPerformed(e));

        //---- btn_old ----
        btn_old.setText("\u53c2\u7167");
        btn_old.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_old.addActionListener(e -> btnOldActionPerformed(e));

        //---- label3 ----
        label3.setText("\u5b9f\u65bd\u72b6\u6cc1");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(64, 64, 64)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(495, 495, 495))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(scrollPane1, GroupLayout.Alignment.LEADING)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(label2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(txt_mail, GroupLayout.Alignment.CENTER)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addGroup(contentPaneLayout.createParallelGroup()
                                                .addComponent(txt_old, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txt_new, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE))
                                            .addGap(0, 0, Short.MAX_VALUE)))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(btn_new, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                                            .addGap(5, 5, 5)
                                            .addComponent(btn_run, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btn_stop, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btn_old, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))))
                            .addGap(42, 42, 42))))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addComponent(label1, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_mail, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_old)
                        .addComponent(txt_old, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_new)
                        .addComponent(btn_run)
                        .addComponent(btn_stop)
                        .addComponent(txt_new, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(37, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

//        InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream(".properties");
//        String confPath = System.getProperty("user.dir") + "\\temp.properties";
//        InputStream in = null;
//        Properties props = new Properties();
//        try {
//            in = new BufferedInputStream(new FileInputStream(confPath));
//            props.load(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(props != null){
//            txt_mail.setText(props.getProperty("mailAddress"));
//        }

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JLabel label2;
    private JTextField txt_old;
    private JLabel label4;
    private JTextField txt_new;
    private JButton btn_new;
    private JLabel label5;
    private JButton btn_run;
    private JButton btn_stop;
    private JTextField txt_mail;
    private JButton btn_old;
    private JLabel label3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    private JFileChooser jfilechooser1;
    private JFileChooser jfilechooser2;
    private HashMap<String, ImageAttributeModel> fMap;
    private HashMap<String, ImageAttributeModel> tMap;
    private WaitWork waitWork;
    private Vector<String> column;
}
