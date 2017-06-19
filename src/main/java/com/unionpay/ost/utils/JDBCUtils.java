package com.unionpay.ost.utils;

import com.unionpay.ost.exception.MyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 封装了JDBC常用的操作
 * Created by jsf on 16/8/3..
 */
public class JDBCUtils {
    /**
     * 关闭数据库连接
     * @param resultSet
     * @param preparedStatement
     * @param connection
     */
    public static void shutDownDataBaseResource(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection){
        shutResultSet(resultSet,preparedStatement,connection,true);
    }
    /**
     * 关闭数据库资源（包括相关的结果集、PrepareStatement对象、数据库连接）
     * @param resultSet
     * @param preparedStatement
     * @param connection
     * @param isCloseConnection 是否关闭数据库连接
     */
    public static void shutDownDataBaseResource(ResultSet resultSet,PreparedStatement preparedStatement,Connection connection,boolean isCloseConnection){
        shutResultSet(resultSet,preparedStatement,connection,isCloseConnection);
    }
    private static void shutResultSet(ResultSet resultSet,PreparedStatement preparedStatement,Connection connection,boolean isCloseConnection){
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new MyException(e, "无法关闭结果集");
            }finally{
                shutPrepareStatement(preparedStatement,connection,isCloseConnection);
            }
        }else{
            shutPrepareStatement(preparedStatement,connection,isCloseConnection);
        }
    }
    private static void shutPrepareStatement(PreparedStatement preparedStatement,Connection connection,boolean isCloseConnection){
        if(preparedStatement!=null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new MyException(e, "无法关闭PreparedStatement对象");
            }finally{
                shutConnection(connection,isCloseConnection);
            }
        }
    }

    private static void shutConnection(Connection connection,boolean isCloseConnection){
        if(isCloseConnection){
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new MyException(e, "无法关闭连接");
                }
            }
        }
    }

}
