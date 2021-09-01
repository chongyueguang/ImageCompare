package com.company.dao;

import com.company.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcBaseDao {
    private static String driverClass;
    private static String url;
    private static String user;
    private static String password;

    // 静的コードブロック
    static{
        try {
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("config.properties");
//            String confPath = System.getProperty("user.dir") + "\\config.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(confPath));

            Properties props = new Properties();
            props.load(in);
            driverClass = props.getProperty("driverClass");
            url = props.getProperty("url");
            user = props.getProperty("user");
            password = props.getProperty("password");
            in.close();
        } catch (IOException e) {
            LogUtils.error(e.getMessage());
            throw new ExceptionInInitializerError("configファイル内容の取得を失敗した");
        }
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            LogUtils.error(e.getMessage());
            throw new ExceptionInInitializerError("ドライバのロードに失敗しました");
        }
    }

    /**
     * 获取连接
     * @return: conn
     */
    public Connection getConnection(){
        try {
            Connection conn = DriverManager.getConnection(url,user,password);
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("データベースへのリンクのURLまたはユーザー名とパスワードが間違っています。構成ファイルを確認してください");
        }
    }

}
