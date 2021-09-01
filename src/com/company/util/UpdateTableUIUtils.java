package com.company.util;

import com.company.Const;
import com.company.service.TblJobInfoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class UpdateTableUIUtils {
    public static void UpdTblUI(JScrollPane jScrollPane, JTable jTable, Vector vectorHeader,Vector<Vector<Object>> vectorJobInfo){
        DefaultTableModel dtm2=(DefaultTableModel)jTable.getModel();
        dtm2.setDataVector(vectorJobInfo,vectorHeader);
        jTable.validate();
        jTable.updateUI();
        jScrollPane.validate();
        jScrollPane.updateUI();
    }
}
