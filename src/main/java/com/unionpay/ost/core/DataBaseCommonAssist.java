package com.unionpay.ost.core;

import com.unionpay.ost.bean.FailureEntity;
import com.unionpay.ost.bean.FieldMeta;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.config.DataBaseRelateConstant;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.utils.CollectionUtils;
import com.unionpay.ost.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库共用辅助类
 * Created by jsf on 16/8/18..
 */
public class DataBaseCommonAssist {

    public static String[] wholeNumberArray = {"INTEGER", "BIGINT", "SMALLINT", "TINYINT", "MEDIUMINT", "INT"};

    /**
     * 数据库中获取每个数据表中主键的个数
     *
     * @param connection 数据库连接
     * @param tableName  数据库中的表名
     * @return 主键的数目
     */
    public static int obtainPKCount(Connection connection, String tableName, String dbType) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        String totalPKCountForMySQL = "select count(COLUMN_KEY) AS PKCOUNT from information_schema.COLUMNS  "
                + "where TABLE_SCHEMA=? and TABLE_NAME=? AND COLUMN_KEY='PRI' ORDER BY ORDINAL_POSITION";

        String totalPkCountForDBSQL = "SELECT COUNT(TYPE) AS PKCOUNT FROM SYSCAT.KEYCOLUSE KCU " +
                " LEFT OUTER JOIN SYSCAT.TABCONST TC ON TC.CONSTNAME=KCU.CONSTNAME AND TC.TABSCHEMA=KCU.TABSCHEMA " +
                " AND TC.TABNAME=KCU.TABNAME WHERE  TC.TABSCHEMA=? AND  TC.TABNAME=? AND TC.TYPE='P' ";
        try {
            if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                ps = connection.prepareStatement(totalPKCountForMySQL);
            } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                ps = connection.prepareStatement(totalPkCountForDBSQL);
            }
            ps.setString(1, Configuration.getSchema());
            ps.setString(2, tableName);
            rs = ps.executeQuery();
            while (rs.next()) {
                count = Integer.parseInt(rs.getString("PKCOUNT"));
            }
        } catch (SQLException e) {
            throw new MyException(e, "无法执行查询主键数目的语句");
        } finally {
            JDBCUtils.shutDownDataBaseResource(rs, ps, null, false);
        }
        return count;
    }

    /**
     * 判断该表是否存在
     *
     * @param connection
     * @param tableName  数据库表名
     * @param dbType     连接的数据库类型
     * @return 如果存在, 返回1
     */
    public static int isExistTable(Connection connection, String tableName, String dbType) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        String judgeIsExistTableForMySql = "SELECT COUNT(*) AS TABLECOUNT from information_schema.TABLES where TABLE_NAME=? AND TABLE_SCHEMA=?";
        String judgeIsExistTableForDB2 = "SELECT COUNT(*) AS TABLECOUNT FROM SYSCAT.TABLES WHERE TABNAME=? AND TABSCHEMA=?";
        try {
            if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                ps = connection.prepareStatement(judgeIsExistTableForMySql);
            } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                ps = connection.prepareStatement(judgeIsExistTableForDB2);
            }
            ps.setString(1, tableName);
            ps.setString(2, Configuration.getSchema());
            rs = ps.executeQuery();
            while (rs.next()) {
                count = Integer.parseInt(rs.getString("TABLECOUNT"));
            }
        } catch (SQLException e) {
            throw new MyException(e, "无法查询该表是否存在");
        } finally {
            JDBCUtils.shutDownDataBaseResource(rs, ps, null, false);
        }
        return count;

    }


}
