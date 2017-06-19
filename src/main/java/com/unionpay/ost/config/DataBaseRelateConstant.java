package com.unionpay.ost.config;

/**
 * Created by jsf on 16/8/16..
 */
public class DataBaseRelateConstant {
    /**
     * 定义数据库类型常量,还可以定义其他数据库常量
     */
    public static final String MYSQL = "mysql";

    public static final String DB2 = "db2";
    /**
     * 数据库表的分割标识
     */
    public static final String TABLESPILTMARK="_";
    /**
     * 对于有多个主键的数据表,将多个数据表放在一个实体类
     */
    public static final String PRIKEYTABLESUFFIX="PK";

}
