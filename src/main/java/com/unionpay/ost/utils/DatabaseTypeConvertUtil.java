package com.unionpay.ost.utils;

/**
 * Created by jsf on 16/8/8..
 */
public class DatabaseTypeConvertUtil {
    public static String dataBaseTypeToJavaTypeForMYSQL(String dateBaseType) {
        if(dateBaseType!=null){
            if("VARCHAR".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else if("CHAR".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else if("DATE".equalsIgnoreCase(dateBaseType.trim())){
                return "Date";
            }else if("DATETIME".equalsIgnoreCase(dateBaseType.trim())){
                return "Timestamp";
            }else if("TIMESTAMP".equalsIgnoreCase(dateBaseType.trim())){
                //数据库中的Timestamp类型转化为Date类型
                return "Date";
            }else if("DOUBLE".equalsIgnoreCase(dateBaseType.trim())){
                return "Double";
            }else if("TINYINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("SMALLINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("MEDIUMINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("BIGINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Long";
            }else if("INT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("FLOAT".equalsIgnoreCase(dateBaseType.trim())){
                return "Float";
            }else if("DECIMAL".equalsIgnoreCase(dateBaseType.trim())){
                return "BigDecimal";
            }else if("BLOB".equalsIgnoreCase(dateBaseType.trim())){
                return "byte[]";
            }else if("TEXT".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else{
                return "String";
            }
        }else{
            return "String";
        }
        //在JDK1.6中不能使用
//        switch (dateBaseType) {
//            case "VARCHAR":
//                return "String";
//            case "CHAR":
//                return "String";
//            case "DATE":
//                return "Date";
//            case "DATETIME":
//                return "Timestamp";
//            case "TIMESTAMP":
//                return "Timestamp";
//            case "DOUBLE":
//                return "Double";
//            case "TINYINT":
//                return "Integer";
//            case "SMALLINT":
//                return "Integer";
//            case "MEDIUMINT":
//                return "Integer";
//            case "INT":
//                return "Integer";
//            case "FLOAT":
//                return "Float";
//            case "DECIMAL":
//                return "BigDecimal";
//            case "BLOB":
//                return "byte[]";
//            case "TEXT":
//                return "String";
//            default:
//                return "String";
//        }
    }
    public static String dataBaseTypeToJavaTypeForDB2(String dateBaseType){

        if(dateBaseType!=null){
            if("BIGINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Long";
            }else if("CHAR".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else if("CLOB".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else if("VARCHAR".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else if("DATE".equalsIgnoreCase(dateBaseType.trim())){
                return "Date";
            }else if("DATETIME".equalsIgnoreCase(dateBaseType.trim())){
                return "Timestamp";
            }else if("TIMESTAMP".equalsIgnoreCase(dateBaseType.trim())){
                //数据库中的Timestamp类型转化为Date类型
                return "Date";
            }else if("TIME".equalsIgnoreCase(dateBaseType.trim())){
                return "Time";
            }else if("DOUBLE".equalsIgnoreCase(dateBaseType.trim())){
                return "Double";
            }else if("TINYINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("SMALLINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("MEDIUMINT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("INTEGER".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("INT".equalsIgnoreCase(dateBaseType.trim())){
                return "Integer";
            }else if("NUMERIC".equalsIgnoreCase(dateBaseType.trim())){
                return "BigDecimal";
            }else if("DECIMAL".equalsIgnoreCase(dateBaseType.trim())){
                return "BigDecimal";
            }else if("BLOB".equalsIgnoreCase(dateBaseType.trim())){
                return "byte[]";
            }else if("TEXT".equalsIgnoreCase(dateBaseType.trim())){
                return "String";
            }else{
                return "String";
            }

        }else{
            return "String";
        }

//        switch (dateBaseType) {
//            case "BIGINT":
//                return "Long";
//            case "CHAR":
//                return "String";
//            case "CLOB":
//                return "String";
//            case "DATE":
//                return "Date";
//            case "DATETIME":
//                return "Timestamp";
//            case "TIMESTAMP":
//                return "Timestamp";
//            case "TIME":
//                return "Time";
//            case "DOUBLE":
//                return "Double";
//            case "TINYINT":
//                return "Integer";
//            case "SMALLINT":
//                return "Integer";
//            case "MEDIUMINT":
//                return "Integer";
//            case "INTEGER":
//                return "Integer";
//            case "INT":
//                return "Integer";
//            case "NUMERIC":
//                return "BigDecimal";
//            case "DECIMAL":
//                return "BigDecimal";
//            case "BLOB":
//                return "byte[]";
//            case "TEXT":
//                return "String";
//            default:
//                return "String";
//        }
    }
}
