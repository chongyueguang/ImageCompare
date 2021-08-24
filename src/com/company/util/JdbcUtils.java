package com.company.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcUtils {
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
            //  JdbcUtil.class是获得当前对象所属的class对象
            //  getClassLoader()是取得该Class对象的类装载器
            //  getResourceAsStream(“dbcfg.properties”) 调用类加载器的方法加载资源，返回是字节输入流
            InputStream in = com.company.MainForm.class.getClassLoader().getResourceAsStream("dbcfg.properties");
            // 实例化Properties对象，目的是为了创建props
            Properties props = new Properties();
            // 在props对象中可以进行加载属性列表到Properties类对象
            props.load(in);//也就是说: 通过props对象进行加载输入流对象（in）
            /*
             * 通过getProperty方法用指定的键在此属性列表中搜索属性
             */
            //也就是说: 通过props对象进行获取【dbcfg.properties】中的指定的键-driverClass（被指定）
            driverClass = props.getProperty("driverClass");
            //也就是说: 通过props对象进行获取【dbcfg.properties】中的指定的键-url（被指定）
            url = props.getProperty("url");
            //也就是说: 通过props对象进行获取【dbcfg.properties】中的指定的键-user（被指定）
            user = props.getProperty("user");
            //也就是说: 通过props对象进行获取【dbcfg.properties】中的指定的键-password（被指定）
            password = props.getProperty("password");
            //已经获取过配置文件中的属性键值对，将字节输入流进行释放关闭
            in.close();
        } catch (IOException e) {
            // 转换异常抛出
            throw new ExceptionInInitializerError("获取数据库配置文件信息失败");
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
    public static Connection getConnection(){
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

    /**
     * 释放资源
     * 传递三个参数: 结果集对象 ，处理Sql语句对象 , 连接对象
     * 无返回值状态
     */
    public static void release(ResultSet rs, Statement stmt, Connection conn){
        //如果 结果集中不为空
        if(rs!=null){
            try {
                rs.close();//将结果集中关闭
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
        //如果处理Sql语句对象不为空
        if(stmt!=null){
            try {
                stmt.close();//将处理Sql语句对象关闭
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        //如果 连接不为空
        if(conn!=null){
            try {
                conn.close();//将连接关闭
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
