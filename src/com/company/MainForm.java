/*
 * Created by JFormDesigner on Thu Aug 12 17:00:33 JST 2021
 */

package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.table.*;

import com.alibaba.fastjson.JSONObject;
import com.company.model.*;
import com.company.service.PostThreadService;
import com.company.service.TblJobInfoService;
import com.company.util.FileUtils;
import com.company.util.ImageChangeUtils;
import com.company.util.LogUtils;
import com.company.service.TimerService;

/**
 * @author 1
 */
public class MainForm extends JFrame {

    public MainForm() {
        initComponents();
    }

    private void btnOldActionPerformed(ActionEvent e) {
        jfilechooser1.setFileSelectionMode(1);
        int a = jfilechooser1.showSaveDialog(null);  //保存文件，指定路径
        if(a == jfilechooser1.APPROVE_OPTION){
            File f = jfilechooser1.getSelectedFile();
            txt_old.setText(f.getAbsolutePath());
            fMap = new HashMap<>();
            Properties properties = FileUtils.getProperties(f);
            fMap = FileUtils.getAllPngFiles(fMap,f,f.getAbsolutePath(),properties);

        }
    }

    private void btnNewActionPerformed(ActionEvent e) {
        jfilechooser2.setFileSelectionMode(1);
        int a = jfilechooser2.showSaveDialog(null);  //保存文件，指定路径
        if(a == jfilechooser2.APPROVE_OPTION){
            File f = jfilechooser2.getSelectedFile();
            txt_new.setText(f.getAbsolutePath());
            tMap = new HashMap<>();
            Properties properties = FileUtils.getProperties(f);
            tMap = FileUtils.getAllPngFiles(tMap,f,f.getAbsolutePath(),properties);
        }
    }

    private void btnRunActionPerformed(ActionEvent e) throws IOException, InterruptedException {
        //创建RESULT文件夹
        File fileCompare = new File(txt_new.getText()+"\\RESULT\\比較結果");
        if(!fileCompare.mkdirs()){
            LogUtils.error("比較結果Folder作成失敗");
            return;
        }
        File fileOldEvidence = new File(txt_new.getText()+"\\RESULT\\実行完了現エビデンス");
        if(!fileOldEvidence.mkdirs()){
            LogUtils.error("実行完了現エビデンスFolder作成失敗");
            return;
        }
        File fileNewEvidence = new File(txt_new.getText()+"\\RESULT\\実行完了新エビデンス");
        if(!fileNewEvidence.mkdirs()){
            LogUtils.error("実行完了新エビデンスFolder作成失敗");
            return;
        }


        //
        ArrayList<CompareFileModel> compareFileArr = FileUtils.getCompareFileArr(fMap, tMap);
        //
        TblJobInfoService tblJobInfoService = new TblJobInfoService();
        //插入数据
        int jobID = tblJobInfoService.insertJobInfoByJobID(txt_mail.getText(), compareFileArr.size());

        TimerService.waitTimer(compareFileArr,txt_new,jobID);



    }

    private void btnStopActionPerformed(ActionEvent e) {
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

        //---- label1 ----
        label1.setText("\u73fe\u65b0\u6bd4\u8f03\u5b9f\u65bd");
        label1.setBackground(new Color(51, 153, 255));
        label1.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));

        //======== scrollPane1 ========
        {

            //---- table1 ----
            TblJobInfoService tblJobInfoService = new TblJobInfoService();
            Vector<String> column = new Vector<>();
            column.add("\u5b9f\u65bd\u8005");
            column.add("\u30b9\u30c6\u30fc\u30bf\u30b9");
            column.add("\u4e88\u6e2c\u5b8c\u4e86\u6642\u9593");
            column.add("\u5b8c\u4e86\u6570/\u5168\u4f53");
            column.add("\u6295\u5165\u6642\u9593");
            column.add("\u958b\u59cb\u6642\u9593");
            column.add("\u66f4\u65b0\u6642\u9593");

            table1 = new JTable(tblJobInfoService.getJobInfoByListToVector(),column);
            table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            {
                TableColumnModel cm = table1.getColumnModel();
                cm.getColumn(0).setMinWidth(60);
            }

            table1.setGridColor(Color.black);
            table1.setSelectionForeground(Color.white);
            table1.setForeground(Color.black);
            table1.setBorder(UIManager.getBorder("EditorPane.border"));
            scrollPane1.setViewportView(table1);
            //刷新画面定时器
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
}
