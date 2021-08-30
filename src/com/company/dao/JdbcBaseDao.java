package com.company.dao;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcBaseDao {
    // 定义 数据库驱动
    private static String driverClass;
    // 定义 数据库的连接
    private static String url;
    // 定义 数据库用户
    private static String user;
    // 定义 数据库用户的密码
    private static String password;

    // 静态代码块
    static{
        try {
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("config.properties");
//            String confPath = System.getProperty("user.dir") + "\\config.properties";
//            InputStream in = new BufferedInputStream(new FileInputStream(confPath));

            // 实例化Properties对象，目的是为了创建props
            Properties props = new Properties();
            // 在props对象中可以进行加载属性列表到Properties类对象
            props.load(in);//也就是说: 通过props对象进行加载输入流对象（in）
            driverClass = props.getProperty("driverClass");
            url = props.getProperty("url");
            user = props.getProperty("user");
            password = props.getProperty("password");
            in.close();
        } catch (IOException e) {
            // 转换异常抛出
            throw new ExceptionInInitializerError("dbcfgファイル内容の取得を失敗した");
        }
        try {
            // 类加载-->驱动
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            // 转换异常抛出
            throw new ExceptionInInitializerError("加载驱动失败");
        }
    }

    /**
     * 获取连接
     * @return: conn
     */
    public Connection getConnection(){
        try {
            //连接类型  连接对象  =  驱动管理中的获取连接(连接，用户名，密码)
            Connection conn = DriverManager.getConnection(url,user,password);
            // 将连接进行返回
            return conn;
        } catch (Exception e) {
            // 转换异常抛出
            throw new RuntimeException("链接数据库的url或用户名密码错误,请检查您的配置文件");
        }
    }

}
