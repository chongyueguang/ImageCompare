/*
 * Created by JFormDesigner on Thu Aug 12 17:00:33 JST 2021
 */

package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.table.*;

import com.alibaba.fastjson.JSONObject;
import com.company.model.CompareFileModel;
import com.company.model.ImageModel;
import com.company.model.ImageReqInfoModel;
import com.company.model.SettingsModel;
import com.company.service.PostThreadService;
import com.company.util.FileUtils;
import com.company.util.ImageChangeUtils;

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
            fMap = FileUtils.getAllPngFiles(fMap,f,f.getAbsolutePath());
        }
    }

    private void btnNewActionPerformed(ActionEvent e) {
        jfilechooser2.setFileSelectionMode(1);
        int a = jfilechooser2.showSaveDialog(null);  //保存文件，指定路径
        if(a == jfilechooser2.APPROVE_OPTION){
            File f = jfilechooser2.getSelectedFile();
            txt_new.setText(f.getAbsolutePath());
            tMap = new HashMap<>();
            tMap = FileUtils.getAllPngFiles(tMap,f,f.getAbsolutePath());
        }
    }

    private void btnRunActionPerformed(ActionEvent e) throws IOException, InterruptedException {
        int concurrent = 3;//线程条数控制
        int fileSize = 1;//每次获取数据的数量
        ExecutorService executor = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(concurrent);
        int start = 0;
        int end = 0;
        List<CompareFileModel> list = null;

        ArrayList<CompareFileModel> compareFileArr = FileUtils.getCompareFileArr(fMap, tMap);
        for (CompareFileModel compareFileModel : compareFileArr){ //遍历所有图片文件
            //对图片文件进行转码
            String imageBase64From = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getFromFile());
            String imageBase64To = ImageChangeUtils.imageToBase64ByFile(compareFileModel.getToFile());
            //TODO IgnoreAreas为假数据
            ImageModel imageModelFrom1 = new ImageModel();
            imageModelFrom1.setItx(0.15);
            imageModelFrom1.setIty(0.18);
            imageModelFrom1.setRbx(0.19);
            imageModelFrom1.setRby(0.22);
            ImageModel imageModelFrom2 = new ImageModel();
            imageModelFrom2.setItx(0.35);
            imageModelFrom2.setIty(0.38);
            imageModelFrom2.setRbx(0.39);
            imageModelFrom2.setRby(0.32);
            ArrayList<ImageModel> imageModelFroms = new ArrayList<>();
            imageModelFroms.add(imageModelFrom1);
            imageModelFroms.add(imageModelFrom2);
            ImageReqInfoModel imageReqInfoModelFrom = new ImageReqInfoModel();
            imageReqInfoModelFrom.setData(imageBase64From);
            imageReqInfoModelFrom.setIgnoreAreas(imageModelFroms);

            ImageModel imageModelTo1 = new ImageModel();
            imageModelTo1.setItx(0.15);
            imageModelTo1.setIty(0.18);
            imageModelTo1.setRbx(0.19);
            imageModelTo1.setRby(0.22);
            ImageModel imageModelTo2 = new ImageModel();
            imageModelTo2.setItx(0.35);
            imageModelTo2.setIty(0.38);
            imageModelTo2.setRbx(0.39);
            imageModelTo2.setRby(0.32);
            ArrayList<ImageModel> imageModelTos = new ArrayList<>();
            imageModelTos.add(imageModelTo1);
            imageModelTos.add(imageModelTo2);
            ImageReqInfoModel imageReqInfoModelTo = new ImageReqInfoModel();
            imageReqInfoModelTo.setData(imageBase64To);
            imageReqInfoModelTo.setIgnoreAreas(imageModelTos);
            SettingsModel settingsModel = new SettingsModel();
            settingsModel.setConfThres(0);
            settingsModel.setIouThres(0);
            settingsModel.setLevdThres(0);
            settingsModel.setShiftThres(0);

            JSONObject json = new JSONObject();
            json.put("image1",imageReqInfoModelFrom);
            json.put("image2",imageReqInfoModelTo);
            json.put("settings",settingsModel);

            end = start + fileSize;
            if(end > compareFileArr.size()) {
                end = compareFileArr.size();
            }
            list = compareFileArr.subList(start, end);
            final CountDownLatch countDownLatch = new CountDownLatch(list.size());
            if(list == null || list.size() == 0) {
                //logger.info("没有查询到processOne需要处理的数据！");
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                executor.execute(new PostThreadService(semaphore, json,countDownLatch));
            }
            countDownLatch.await();//线程阻塞,直到锁为0才释放，继续向下执行
            start += fileSize;
            //logger.info("lsit-size:===="+list.size());
            Thread.sleep(60000);
            while (list.size() == fileSize){
                executor.shutdown();
            }
        }

    }

    private void btnStopActionPerformed(ActionEvent e) {
    }

    private void button1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        jfilechooser1 = new JFileChooser();
        jfilechooser2 = new JFileChooser();
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
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
            table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table1.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, "", null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                    {null, null, null, null, null, null},
                },
                new String[] {
                    "\u5b9f\u65bd\u8005", "\u30b9\u30c6\u30fc\u30bf\u30b9", "\u4e88\u6e2c\u5b8c\u4e86\u6642\u9593", "\u5b8c\u4e86\u6570/\u5168\u4f53", "\u6295\u5165\u6642\u9593", "\u958b\u59cb\u6642\u9593"
                }
            ));
            {
                TableColumnModel cm = table1.getColumnModel();
                cm.getColumn(0).setMinWidth(60);
            }
            table1.setGridColor(Color.white);
            table1.setSelectionForeground(Color.white);
            table1.setForeground(Color.white);
            table1.setBorder(UIManager.getBorder("EditorPane.border"));
            scrollPane1.setViewportView(table1);
        }

        //---- label2 ----
        label2.setText("\u5b9f\u65bd\u8005\uff08\u30e1\u30fc\u30eb\uff09");

        //---- label4 ----
        label4.setText("\u30d5\u30a1\u30a4\u30eb\u683c\u7d0d\u5834\u6240(\u65b0)");

        //---- btn_new ----
        btn_new.setText("\u53c2\u7167");
        btn_new.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_new.addActionListener(e -> button1ActionPerformed(e));

        //---- label5 ----
        label5.setText("\u30d5\u30a1\u30a4\u30eb\u683c\u7d0d\u5834\u6240(\u73fe)");

        //---- btn_run ----
        btn_run.setText("\u5b9f\u884c");
        btn_run.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_run.addActionListener(e -> button1ActionPerformed(e));

        //---- btn_stop ----
        btn_stop.setText("\u4e2d\u6b62");
        btn_stop.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_stop.addActionListener(e -> button1ActionPerformed(e));

        //---- btn_old ----
        btn_old.setText("\u53c2\u7167");
        btn_old.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
        btn_old.addActionListener(e -> button1ActionPerformed(e));

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
    private HashMap<String, File> fMap;
    private HashMap<String, File> tMap;
}
