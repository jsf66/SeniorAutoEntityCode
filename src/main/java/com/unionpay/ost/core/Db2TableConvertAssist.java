package com.unionpay.ost.core;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.FailureEntity;
import com.unionpay.ost.bean.FieldMeta;
import com.unionpay.ost.bean.SequenceMeta;
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
 * DB2数据库表转化entity辅助类
 * Created by jsf on 16/8/18..
 */
public class Db2TableConvertAssist {

    public static List<FieldMeta> obtainFieldMetasForDB2(Connection connection, String tableName, String dbType, List<EntityMeta> entityMetaList, EntityMeta entityMeta, List<FailureEntity> failureEntityList) {
        List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        //获取主键的数量
        int pkCount = DataBaseCommonAssist.obtainPKCount(connection, tableName, dbType);
        //DB2数据库相关的表的字段的信息
        String queryUnionSql = "FROM SYSCAT.COLUMNS C  " +
                "LEFT OUTER JOIN SYSCAT.KEYCOLUSE KCU ON KCU.TABSCHEMA = C.TABSCHEMA AND KCU.TABNAME = C.TABNAME  AND KCU.COLNAME = C.COLNAME  " +
                "LEFT OUTER JOIN SYSCAT.TABCONST TC ON TC.CONSTNAME = KCU.CONSTNAME AND KCU.TABSCHEMA=TC.TABSCHEMA AND KCU.TABNAME=TC.TABNAME " +
                "LEFT OUTER JOIN SYSCAT.COLIDENTATTRIBUTES CID ON CID.COLNAME = C.COLNAME AND CID.TABNAME = C.TABNAME AND CID.TABSCHEMA = C.TABSCHEMA  " +
                "LEFT OUTER JOIN SYSCAT.SEQUENCES SEQ ON CID.SEQID=SEQ.SEQID  " +
                "LEFT OUTER JOIN SYSIBM.COLUMNS IBMC ON IBMC.TABLE_NAME=C.TABNAME AND IBMC.COLUMN_NAME=C.COLNAME AND IBMC.TABLE_SCHEMA=C.TABSCHEMA";

        String columnDetailInfoSQL = "SELECT C.TABNAME AS TABNAME,C.COLNAME AS COLNAME,C.REMARKS AS COMMENT, TC.TYPE AS ISPRIM,C.TYPENAME AS TYPENAME," +
                " C.LENGTH AS LENGTH,C.SCALE AS SCALE,IBMC.NUMERIC_PRECISION AS NUMERIC_PRECISION,IBMC.NUMERIC_SCALE AS NUMERIC_SCALE,IBMC.DATETIME_PRECISION AS DATETIME_PRECISION," +
                "C.SCALE AS SCALE,IBMC.IS_NULLABLE AS IS_NULLABLE,C.DEFAULT AS DEFAULT,C.TEXT AS TEXT, CID.START AS START,CID.INCREMENT AS INCREMENT,SEQ.SEQNAME AS SEQNAME  "
                + queryUnionSql + " WHERE C.TABNAME=? and C.TABSCHEMA=? ORDER BY COLNO FOR FETCH ONLY ";
        try {
            ps = connection.prepareStatement(columnDetailInfoSQL);
            ps.setString(1, tableName);
            ps.setString(2, Configuration.getSchema());
            rs = ps.executeQuery();
            //如果主键是复合主键
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
            fieldMetaList = obtainFieldMetasFromDB2ResultSet(connection, pkCount, tableName, entityMeta, failureEntityList);
        } catch (SQLException e) {
            throw new MyException(e, "DB2无法执行该查询列的SQL语句");
        } finally {
            //关闭相关资源
            JDBCUtils.shutDownDataBaseResource(rs, ps, connection, false);
        }
        return fieldMetaList;
    }

    /**
     * 从DB2查询结果集中整理相应的字段元数据信息
     *
     * @param connection
     * @param pkCount
     * @param tableName
     * @param entityMeta        主要用在错误处理中使用到该实体的类名
     * @param failureEntityList
     * @return 属性元数据集合
     */
    private static List<FieldMeta> obtainFieldMetasFromDB2ResultSet(Connection connection, int pkCount, String tableName, EntityMeta entityMeta, List<FailureEntity> failureEntityList) {
        List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();
        //对于多主键情况,是否有新属性生成
        boolean newFieldIsExistFlag = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //DB2数据库相关的表的字段的信息
        String queryUnionSql = "FROM SYSCAT.COLUMNS C  " +
                "LEFT OUTER JOIN SYSCAT.KEYCOLUSE KCU ON KCU.TABSCHEMA = C.TABSCHEMA AND KCU.TABNAME = C.TABNAME  AND KCU.COLNAME = C.COLNAME  " +
                "LEFT OUTER JOIN SYSCAT.TABCONST TC ON TC.CONSTNAME = KCU.CONSTNAME AND KCU.TABSCHEMA=TC.TABSCHEMA AND KCU.TABNAME=TC.TABNAME " +
                "LEFT OUTER JOIN SYSCAT.COLIDENTATTRIBUTES CID ON CID.COLNAME = C.COLNAME AND CID.TABNAME = C.TABNAME AND CID.TABSCHEMA = C.TABSCHEMA  " +
                "LEFT OUTER JOIN SYSCAT.SEQUENCES SEQ ON CID.SEQID=SEQ.SEQID  " +
                "LEFT OUTER JOIN SYSIBM.COLUMNS IBMC ON IBMC.TABLE_NAME=C.TABNAME AND IBMC.COLUMN_NAME=C.COLNAME AND IBMC.TABLE_SCHEMA=C.TABSCHEMA";

        String columnDetailInfoSQL = "SELECT C.TABNAME AS TABNAME,C.COLNAME AS COLNAME,C.REMARKS AS COMMENT, TC.TYPE AS ISPRIM,C.TYPENAME AS TYPENAME," +
                " C.LENGTH AS LENGTH,C.SCALE AS SCALE,IBMC.NUMERIC_PRECISION AS NUMERIC_PRECISION,IBMC.NUMERIC_SCALE AS NUMERIC_SCALE,IBMC.DATETIME_PRECISION AS DATETIME_PRECISION," +
                "C.SCALE AS SCALE,IBMC.IS_NULLABLE AS IS_NULLABLE,C.DEFAULT AS DEFAULT,C.TEXT AS TEXT, CID.START AS START,CID.INCREMENT AS INCREMENT,SEQ.SEQNAME AS SEQNAME  "
                + queryUnionSql + " WHERE C.TABNAME=? and C.TABSCHEMA=? ORDER BY COLNO FOR FETCH ONLY ";
        try {
            ps = connection.prepareStatement(columnDetailInfoSQL);
            ps.setString(1, tableName);
            ps.setString(2, Configuration.getSchema());
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new MyException(e, "无法执行该查询数据库列信息的语句");
        }
        if (rs != null) {
            try {
                while (rs.next()) {
                    FieldMeta fieldMeta = new FieldMeta();
                    ColumnInfo columnInfo = new ColumnInfo();
                    SequenceMeta sequenceMeta = new SequenceMeta();
                    FailureEntity failureEntity = new FailureEntity();
                    try {
                        String columnName = rs.getString("COLNAME");
                        String columnDateType = rs.getString("TYPENAME");
                        String columnKey = rs.getString("ISPRIM");
                        String columnIsNull = rs.getString("IS_NULLABLE");
                        String columnComment = rs.getString("COMMENT");
                        String columnLength = rs.getString("LENGTH");
                        String columnPrecision = rs.getString("NUMERIC_PRECISION");
                        String columnScale = rs.getString("NUMERIC_SCALE");
                        String columnDateTimePrecision = rs.getString("DATETIME_PRECISION");
                        String sequenceStart = rs.getString("START");
                        String sequenceIncrement = rs.getString("INCREMENT");
                        String sequenceName = rs.getString("SEQNAME");
                        if (pkCount > 1) {
                            if ("P".equalsIgnoreCase(columnKey)) {
                                if (!newFieldIsExistFlag) {
                                    fieldMeta.setFieldType(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, true) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                                    fieldMeta.setFieldName(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, false) + DataBaseRelateConstant.PRIKEYTABLESUFFIX);
                                    fieldMetaList.add(fieldMeta);
                                }
                                newFieldIsExistFlag = true;
                                continue;
                            }
                        }
                        //构造序列对象
                        if (!StringUtils.isEmpty(sequenceName)) {
                            sequenceMeta.setSequenceName(sequenceName);
                            sequenceMeta.setSequenceStrategy(SequenceMeta.SEQUENCE_STRATEGY);
                            sequenceMeta.setSequenceAllocationSize(Long.parseLong(sequenceIncrement));
                            sequenceMeta.setSequenceInitialValue(Long.parseLong(sequenceStart));
                        } else {
                            sequenceMeta = null;
                        }
                        //将相关的结果信息存储到列信息中(这里主要用到的是字段名)
                        columnInfo.setColumnName(columnName);
                        //将相关的结果集放入到Field对象中;
                        fieldMeta.setFieldName(StringUtils.capInMark(columnName, DataBaseRelateConstant.TABLESPILTMARK, false));
                        fieldMeta.setFieldType(DatabaseTypeConvertUtil.dataBaseTypeToJavaTypeForDB2(columnDateType.toUpperCase()));
                        //由于db2数据库中,如果小数精度类型为0,那么和整数无法在字段上区别,同时在entity类上显示注解有区别,只有通过类型来判断是整数还是带有精度数
                        if (!StringUtils.isEmpty(columnPrecision)) {
                            fieldMeta.setFieldPrecision(Long.parseLong(columnPrecision));
                            if (!StringUtils.isEmpty(columnScale)) {
                                fieldMeta.setFieldScale(Long.parseLong(columnScale));
                            }
                            //如果是这五种数据类型,则将Precision置为null
                            for (String strType : DataBaseCommonAssist.wholeNumberArray) {
                                if (strType.equalsIgnoreCase(columnDateType)) {
                                    fieldMeta.setFieldLength(Long.parseLong(columnLength));
                                    fieldMeta.setFieldPrecision(null);
                                    break;
                                }
                            }
                        } else {
                            //可能是字符型也可能是日期类型
                            fieldMeta.setFieldLength(Long.parseLong(columnLength));
                            if (!StringUtils.isEmpty(columnDateTimePrecision)) {
                                fieldMeta.setFieldDateTimePrecision(Long.parseLong(columnDateTimePrecision));
                            }
                        }
                        //对于DB2数据库主键的标识是以"P"认知的
                        if ("P".equalsIgnoreCase(columnKey)) {
                            fieldMeta.setWhetherPK(true);
                        }
                        //该列是否为空,存放的是YES或NO
                        if ("YES".equalsIgnoreCase(columnIsNull)) {
                            fieldMeta.setWhetherNULL(true);
                        }
                        //字段的注释
                        if (!StringUtils.isEmpty(columnComment)) {
                            fieldMeta.setFieldComment(columnComment);
                        }
                        fieldMeta.setSequenceMeta(sequenceMeta);
                        fieldMeta.setColumnInfo(columnInfo);
                        fieldMetaList.add(fieldMeta);
                    } catch (Exception e) {
                        ExceptionHandleAssist.handleException(e, fieldMeta, entityMeta, failureEntity, failureEntityList);
                        continue;
                    }
                }
            } catch (SQLException e) {
                throw new MyException(e, "无法获取结果集中的记录");
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
     * @param entityMeta
     * @param failureEntityList
     * @return
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
                        String columnName = rs.getString("COLNAME");
                        String columnDateType = rs.getString("TYPENAME");
                        String columnKey = rs.getString("ISPRIM");
                        String columnIsNull = rs.getString("IS_NULLABLE");
                        String columnLength = rs.getString("LENGTH");
                        String columnComment = rs.getString("COMMENT");
                        if ("P".equalsIgnoreCase(columnKey)) {
                            //由于是符合主键,所以符合主键是没有序列的
                            SequenceMeta sequenceMeta = null;
                            //将相关的结果信息存储到列信息中(这里主要用到的是字段名)
                            columnInfo.setColumnName(columnName);
                            //将相关的结果集放入到Field对象中;
                            fieldMeta.setFieldName(StringUtils.capInMark(columnName, DataBaseRelateConstant.TABLESPILTMARK, false));
                            fieldMeta.setFieldType(DatabaseTypeConvertUtil.dataBaseTypeToJavaTypeForDB2(columnDateType.toUpperCase()));
                            fieldMeta.setFieldLength(Long.parseLong(columnLength));
                            //字段的注释
                            if (!StringUtils.isEmpty(columnComment)) {
                                fieldMeta.setFieldComment(columnComment);
                            }
                            //该列是否为空,存放的是YES或NO
                            if ("YES".equalsIgnoreCase(columnIsNull)) {
                                fieldMeta.setWhetherNULL(true);
                            }
                            fieldMeta.setSequenceMeta(sequenceMeta);
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
                throw new MyException(e, "无法获取结果即中的数据");
            } finally {
                //关闭该结果集
                JDBCUtils.shutDownDataBaseResource(rs, null, null, false);
            }
        }
        return fieldMetaList;
    }

}
