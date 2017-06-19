package com.unionpay.ost.core;


import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.exception.MyException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 根据配置文件,维持连接对象的管理(使用连接池)
 * Created by jsf on 16/8/3..
 */
public class DataBaseManager {
    
     private static Configuration configuration=new Configuration();

    /**
     * 加载驱动
     */
    private static void classForName(){
         try {
             Class.forName(configuration.getDriverName());
         } catch (ClassNotFoundException e) {
             throw new MyException(e,"无法加载驱动");
         }
     }

    /**
     * 获取数据库连接
     * @return 返回数据库连接
     */
    public static Connection openConnection(){
        classForName();
        Connection connection=null;
        try {
            connection= DriverManager.getConnection(Configuration.getJdbcUrl(),Configuration.getUserName(),Configuration.getPassWord());
        } catch (SQLException e) {
            throw new MyException(e,"无法获得数据库连接");
        }
        return connection;
    }





}
