package com.company.util;

import com.company.service.TblJobInfoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class TimerUtils {

    public static void refreshScreenTimer(JScrollPane jScrollPane, JTable jTable, Vector vectorHeader) {

        new Timer(3000,new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                TblJobInfoService tblJobInfoService = new TblJobInfoService();
                DefaultTableModel dtm2=(DefaultTableModel)jTable.getModel();
                dtm2.setDataVector(tblJobInfoService.getJobInfoByList(),vectorHeader);
                jTable.validate();
                jTable.updateUI();
                jScrollPane.validate();
                jScrollPane.updateUI();
            }
        }).start();
    }
}
