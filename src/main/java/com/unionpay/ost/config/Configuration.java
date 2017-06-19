package com.unionpay.ost.config;

import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.utils.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * 管理配置信息
 * Created by jsf on 16/8/3..
 */
public class Configuration {

    /**
     * 文件编码格式设定
     */
    public static final String encoding="UTF-8";
    /**
     * 驱动类
     */
    private static String driverName;
    /**
     * 数据库用户名
     */
    private static String userName;
    /**
     * 数据库密码
     */
    private static String passWord;
    /**
     * jdbc的Url
     */
    private static String jdbcUrl;
    /**
     * 项目的源码路径
     */
    private static String srcPath;
    /**
     * 生成entity,dao,service,serviceImpl以及action所在个根包名
     */
    private static String basePackageName;
    /**
     * 生成的entity类所在的完全包名
     */
    private static String basePackageEntityName;
    /**
     * 数据库名
     */
    private static String schema;
    /**
     * 作者
     */
    private static String author = "ost";
    /**
     * 用以支持单个或多个数据库表实体类的生成,数据库表之间使用逗号分隔
     */
    private static String[] tableNames;
    /**
     * 是否生成dao,service,serviceImpl,action层
     */
    private static String  isGenOther="false";//默认不生成


    public Configuration() {

    }

    /**
     * 将连接数据的相关配置文件加载
     */
    static {
        //对于打成jar包后这种获取配置文件的方式,无法获取到配置文件
//       InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
        //只能采用这种方式来获取相应的配置文件内容
        File file=new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        InputStream in = null;
        try {
            try {
                //解决配置文件路径中存在中文字符的问题
                String path=URLDecoder.decode(file.getParent(),"UTF-8");
                in = new FileInputStream(URLDecoder.decode(file.getParent(),"UTF-8")+"/db.properties");
            } catch (UnsupportedEncodingException e) {
                throw new MyException(e,"无法获取配置文件所在路径");
            }
        } catch (FileNotFoundException e) {
            throw new MyException(e,"无法读取数据库配置文件");
        }
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new MyException(e, "无法加载配置文件");
        }
        //去除相应的配置项中的空格
        if(!StringUtils.isEmpty(properties.getProperty("driverName"))){
            Configuration.setDriverName(properties.getProperty("driverName").trim());
        }
        if(!StringUtils.isEmpty(properties.getProperty("jdbcUrl"))){
            Configuration.setJdbcUrl(properties.getProperty("jdbcUrl").trim());
        }
        if(!StringUtils.isEmpty(properties.getProperty("userName"))){
            Configuration.setUserName(properties.getProperty("userName").trim());
        }
        if(!StringUtils.isEmpty(properties.getProperty("passWord"))){
            Configuration.setPassWord(properties.getProperty("passWord").trim());
        }
        if(!StringUtils.isEmpty(properties.getProperty("projectPackageEntityName"))){
            Configuration.setBasePackageEntityName(properties.getProperty("projectPackageEntityName").trim());
        }
        int cutOutIndex=Configuration.getBasePackageEntityName().lastIndexOf(".");
        Configuration.setBasePackageName(Configuration.getBasePackageEntityName().substring(0,cutOutIndex));
//        if(!StringUtils.isEmpty(properties.getProperty("projectPackageName"))){
//            Configuration.setBasePackageName(properties.getProperty("projectPackageName").trim());
//        }

        if(!StringUtils.isEmpty(properties.getProperty("srcPath"))){
            Configuration.setSrcPath(properties.getProperty("srcPath").trim());
        }
        if(!StringUtils.isEmpty(properties.getProperty("schema"))){
            Configuration.setSchema(properties.getProperty("schema").trim());
        }
        String tableNameStr = properties.getProperty("tableNames");
        Configuration.setTableNames(handleTableNameStr(tableNameStr));
        if(!StringUtils.isEmpty(properties.getProperty("isGenOther"))){
            Configuration.setIsGenOther(properties.getProperty("isGenOther"));
        }

    }

    //处理表名字符串
    private static String[] handleTableNameStr(String str) {
        String[] tableNameArray = null;
        if (!StringUtils.isEmpty(str)) {
            String handleTableNameStr = str.toUpperCase().trim();
            try {
                if (handleTableNameStr.contains(",")) {
                    tableNameArray = handleTableNameStr.split(",");
                    if (tableNameArray != null && tableNameArray.length != 0) {
                        Configuration.setTableNames(tableNameArray);
                    }
                } else {
                    tableNameArray=new String[]{handleTableNameStr};
                }

            } catch (Exception e) {
                throw new MyException(e,"有非法字符,无法进行分隔");
            }
        }
        return tableNameArray;

    }

    public static String getDriverName() {
        return driverName;
    }

    public static void setDriverName(String driverName) {
        Configuration.driverName = driverName;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Configuration.userName = userName;
    }

    public static String getPassWord() {
        return passWord;
    }

    public static void setPassWord(String passWord) {
        Configuration.passWord = passWord;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        Configuration.jdbcUrl = jdbcUrl;
    }

    public static String getSrcPath() {
        return srcPath;
    }

    public static void setSrcPath(String srcPath) {
        Configuration.srcPath = srcPath;
    }

    public static String getBasePackageName() {
        return basePackageName;
    }
    public static void setBasePackageName(String basePackageName) {
        Configuration.basePackageName = basePackageName;
    }

    public static String getSchema() {
        return schema;
    }

    public static void setSchema(String schema) {
        Configuration.schema = schema;
    }

    public static String getAuthor() {
        return author;
    }

    public static String[] getTableNames() {
        return tableNames;
    }

    public static void setTableNames(String[] tableNames) {
        Configuration.tableNames = tableNames;
    }

    public static String getIsGenOther() {
        return isGenOther;
    }

    public static void setIsGenOther(String isGenOther) {
        Configuration.isGenOther = isGenOther;
    }

    public static String getBasePackageEntityName() {
        return basePackageEntityName;
    }

    public static void setBasePackageEntityName(String basePackageEntityName) {
        Configuration.basePackageEntityName = basePackageEntityName;
    }
}
