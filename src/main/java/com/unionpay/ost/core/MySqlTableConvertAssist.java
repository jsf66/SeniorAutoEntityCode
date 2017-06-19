package com.unionpay.ost.core;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.FailureEntity;
import com.unionpay.ost.bean.FieldMeta;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.config.DataBaseRelateConstant;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.table.ColumnInfo;
import com.unionpay.ost.utils.CollectionUtils;
import com.unionpay.ost.utils.DatabaseTypeConvertUtil;
import com.unionpay.ost.utils.JDBCUtils;
import com.unionpay.ost.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * MySql数据库表转化entity辅助类
 * Created by jsf on 16/8/18..
 */
public class MySqlTableConvertAssist {

    /**
     * 获取每个数据库表中数据字段的信息
     *
     * @param connection
     * @param tableName
     * @param dbType
     * @param entityMetaList
     * @param entityMeta
     * @param failureEntityList
     * @return
     */
    public static List<FieldMeta> obtainFieldMetasForMySQL(Connection connection, String tableName, String dbType, List<EntityMeta> entityMetaList, EntityMeta entityMeta, List<FailureEntity> failureEntityList) {
        List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        //获取主键的数量
        int pkCount = DataBaseCommonAssist.obtainPKCount(connection, tableName, dbType);
        try {
            String columnInfoSQL = "select COLUMN_NAME,DATA_TYPE,COLUMN_TYPE,COLUMN_KEY,IS_NULLABLE,COLUMN_COMMENT,NUMERIC_PRECISION,NUMERIC_SCALE,DATETIME_PRECISION from information_schema.COLUMNS " +
                    "where TABLE_SCHEMA=? AND TABLE_NAME=? ORDER BY ORDINAL_POSITION";
            ps = connection.prepareStatement(columnInfoSQL);
            ps.setString(1, Configuration.getSchema());
            ps.setString(2, tableName);
            rs = ps.executeQuery();
            //说明是复合主键(特殊情况)
            if (pkCount > 1) {
                EntityMeta newEntityMeta = new EntityMeta();
                FieldMeta fieldMeta = new FieldMeta();
                List<FieldMeta> priKeyFieldMetaList = new ArrayList<FieldMeta>();
                //新增实体类唯一标识属性修饰符
                fieldMeta.setFieldType("Integer");
                //设置新增实体类唯一标识属性对应的属性名
                fieldMeta.setFieldName(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, false) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                //设置新增实体实体名
                newEntityMeta.setEntityName(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, true) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                priKeyFieldMetaList = obtainPriKeyFieldMetaForManyKey(rs, newEntityMeta, failureEntityList);
                newEntityMeta.setFieldMetaList(priKeyFieldMetaList);
                System.out.println("复合主键,<成功生成" + newEntityMeta.getEntityName() + "实体类>");
                entityMetaList.add(newEntityMeta);
            }
            fieldMetaList = obtainFieldMetasFromMySqlResultSet(connection, pkCount, tableName, entityMeta, failureEntityList);
        } catch (SQLException e) {
            throw new MyException(e, "MYSQL无法执行该查询列的SQL语句");
        } finally {
            //关闭相关资源
            JDBCUtils.shutDownDataBaseResource(rs, ps, connection, false);
        }

        return fieldMetaList;
    }

    /**
     * 从MySql查询结果集中整理相应的字段元数据信息
     *
     * @param connection
     * @param pkCount
     * @param tableName
     * @param entityMeta
     * @param failureEntityList
     * @return
     */
    private static List<FieldMeta> obtainFieldMetasFromMySqlResultSet(Connection connection, int pkCount, String tableName, EntityMeta entityMeta, List<FailureEntity> failureEntityList) {
        List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();
        //对于多主键情况,是否有新属性生成
        boolean newFieldIsExistFlag = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String columnInfoSQL = "select COLUMN_NAME,DATA_TYPE,COLUMN_TYPE,COLUMN_KEY,IS_NULLABLE,COLUMN_COMMENT,NUMERIC_PRECISION,NUMERIC_SCALE,DATETIME_PRECISION from information_schema.COLUMNS " +
                "where TABLE_SCHEMA=? AND TABLE_NAME=? ORDER BY ORDINAL_POSITION";
        try {
            ps = connection.prepareStatement(columnInfoSQL);
            ps.setString(1, Configuration.getSchema());
            ps.setString(2, tableName);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new MyException(e, "无法执行该查询数据库列信息的语句");
        }
        if (rs != null) {
            try {
                while (rs.next()) {
                    FieldMeta fieldMeta = new FieldMeta();
                    ColumnInfo columnInfo = new ColumnInfo();
                    FailureEntity failureEntity = new FailureEntity();
                    try {
                        String columnName = rs.getString("COLUMN_NAME");
                        String columnDateType = rs.getString("DATA_TYPE");
                        String columnType = rs.getString("COLUMN_TYPE");
                        String columnKey = rs.getString("COLUMN_KEY");
                        String columnIsNull = rs.getString("IS_NULLABLE");
                        String columnComment = rs.getString("COLUMN_COMMENT");
                        String columnPrecision = rs.getString("NUMERIC_PRECISION");
                        String columnScale = rs.getString("NUMERIC_SCALE");
                        String columnDateTimePrecision = rs.getString("DATETIME_PRECISION");
                        if (pkCount > 1) {
                            if ("PRI".equalsIgnoreCase(columnKey)) {
                                if (!newFieldIsExistFlag) {
                                    fieldMeta.setFieldType(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, true) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                                    fieldMeta.setFieldName(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, false) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                                    fieldMetaList.add(fieldMeta);
                                }
                                newFieldIsExistFlag = true;
                                continue;
                            }
                        }

                        //对于MySQl数据库主键的标识是以"PRI"认知的
                        if (columnKey.contains("PRI")) {
                            fieldMeta.setWhetherPK(true);
                        }
                        //将相关的结果信息存储到列信息中(这里主要用到的是字段名)
                        columnInfo.setColumnName(columnName);
                        //将相关的结果集放入到Field对象中;
                        fieldMeta.setFieldName(StringUtils.capInMark(columnName, DataBaseRelateConstant.TABLESPILTMARK, false));
                        fieldMeta.setFieldType(DatabaseTypeConvertUtil.dataBaseTypeToJavaTypeForMYSQL(columnDateType.toUpperCase()));
                        if (!columnDateType.equals(columnType)) {
                            //对于这种情况,如果小数精度类型为0,那么和整数无法在字段上区别,同时在entity类上显示注解有区别,只有通过类型来判断是整数还是带有精度数
                            if (!StringUtils.isEmpty(columnScale)) {
                                if (Integer.parseInt(columnScale) == 0) {
                                    fieldMeta.setFieldPrecision(Long.parseLong(columnPrecision));
                                    fieldMeta.setFieldScale(Long.parseLong(columnScale));
                                    //对于这四种整数数据类型,将它们的Precision设置为空
                                    for (String strType : DataBaseCommonAssist.wholeNumberArray) {
                                        if (strType.equalsIgnoreCase(columnDateType)) {
                                            long columnLength = Long.parseLong(columnType.replace("(", "").replace(")", "").replace(columnDateType, ""));
                                            fieldMeta.setFieldLength(columnLength);
                                            fieldMeta.setFieldPrecision(null);
                                            break;
                                        }
                                    }
                                } else {
                                    //对于精度不是0的,那么它就是小数,直接进行设置
                                    fieldMeta.setFieldPrecision(Long.parseLong(columnPrecision));
                                    fieldMeta.setFieldScale(Long.parseLong(columnScale));
                                }
                            } else {
                                //可能是字符型也可能是日期类型
                                long columnLength = Long.parseLong(columnType.replace("(", "").replace(")", "").replace(columnDateType, ""));
                                fieldMeta.setFieldLength(columnLength);
                                if (!StringUtils.isEmpty(columnDateTimePrecision)) {
                                    fieldMeta.setFieldDateTimePrecision(Long.parseLong(columnDateTimePrecision));
                                }
                            }
                        }
                        //该列是否为空,存放的是YES或NO
                        if ("YES".equalsIgnoreCase(columnIsNull)) {
                            fieldMeta.setWhetherNULL(true);
                        }
                        //字段的注释
                        if (!StringUtils.isEmpty(columnComment)) {
                            fieldMeta.setFieldComment(columnComment);
                        }
                        fieldMeta.setColumnInfo(columnInfo);
                        fieldMetaList.add(fieldMeta);

                        //                        //对整数或带有精度的时间类型进行处理
//                       if (StringUtils.isEmpty(columnScale) || (0 == Long.parseLong(columnScale))) {
//                            long columnLength = Long.parseLong(columnType.replace("(", "").replace(")", "").replace(columnDateType, ""));
//                          fieldMeta.setFieldLength(columnLength);
//                           if (!StringUtils.isEmpty(columnDateTimePrecision)) {
//                                fieldMeta.setFieldDateTimePrecision(Long.parseLong(columnDateTimePrecision));
//                            }
//                       } else {
//                            //对带有小数精度要求的数进行处理
//                          fieldMeta.setFieldPrecision(Long.parseLong(columnPrecision));
//                          fieldMeta.setFieldScale(Long.parseLong(columnScale));
//                       }
                    } catch (Exception e) {
                        ExceptionHandleAssist.handleException(e, fieldMeta, entityMeta, failureEntity, failureEntityList);
                        continue;
                    }

                }
            } catch (SQLException e1) {
                throw new MyException(e1, "无法获取结果集中的记录");
            } finally {
                JDBCUtils.shutDownDataBaseResource(rs, ps, null, false);
            }

        }
        return fieldMetaList;
    }

    /**
     * 如果有多个主键,则将这多个主键构造成新实体的属性
     *
     * @param rs
     * @return 新实体的属性集合
     */
    private static List<FieldMeta> obtainPriKeyFieldMetaForManyKey(ResultSet rs, EntityMeta entityMeta, List<FailureEntity> failureEntityList) {
        List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    FieldMeta fieldMeta = new FieldMeta();
                    ColumnInfo columnInfo = new ColumnInfo();
                    FailureEntity failureEntity = new FailureEntity();
                    try {
                        String columnName = rs.getString("COLUMN_NAME");
                        String columnDateType = rs.getString("DATA_TYPE");
                        String columnType = rs.getString("COLUMN_TYPE");
                        String columnKey = rs.getString("COLUMN_KEY");
                        String columnIsNull = rs.getString("IS_NULLABLE");
                        String columnComment = rs.getString("COLUMN_COMMENT");
                        if ("PRI".equalsIgnoreCase(columnKey)) {
                            //将相关的结果信息存储到列信息中(这里主要用到的是字段名)
                            fieldMeta.setFieldName(StringUtils.capInMark(columnName, DataBaseRelateConstant.TABLESPILTMARK, false));
                            fieldMeta.setFieldType(DatabaseTypeConvertUtil.dataBaseTypeToJavaTypeForMYSQL(columnDateType.toUpperCase()));
                            //字段的注释
                            if (!StringUtils.isEmpty(columnComment)) {
                                fieldMeta.setFieldComment(columnComment);
                            }
                            //对于主键,主键没有字段精度,所以直接可以这样处理
                            long columnLength = Long.parseLong(columnType.replace("(", "").replace(")", "").replace(columnDateType, ""));
                            fieldMeta.setFieldLength(columnLength);
                            //该列是否为空,存放的是YES或NO
                            if ("YES".equalsIgnoreCase(columnIsNull)) {
                                fieldMeta.setWhetherNULL(true);
                            }
                            columnInfo.setColumnName(columnName);
                            fieldMeta.setColumnInfo(columnInfo);
                            fieldMetaList.add(fieldMeta);
                            continue;
                        }
                    } catch (Exception e) {
                        ExceptionHandleAssist.handleException(e, fieldMeta, entityMeta, failureEntity, failureEntityList);
                        continue;
                    }

                }
            } catch (SQLException e) {
                throw new MyException(e, "无法获取结果集信息");
            } finally {
                //关闭该结果集
                JDBCUtils.shutDownDataBaseResource(rs, null, null, false);
            }
        }
        return fieldMetaList;
    }
}
