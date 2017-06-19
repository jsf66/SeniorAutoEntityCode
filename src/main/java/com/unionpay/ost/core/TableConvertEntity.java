package com.unionpay.ost.core;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.FailureEntity;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.config.DataBaseRelateConstant;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.table.TableInfo;
import com.unionpay.ost.utils.JDBCUtils;
import com.unionpay.ost.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将表信息转换为对应的实体类信息
 * Created by jsf on 16/8/6..
 */
public class TableConvertEntity {
    /**
     * 生成失败的实体类放入到此集合类中
     */
    public static List<FailureEntity> failureEntityList = new ArrayList<FailureEntity>();

    /**
     * 根据不同的数据库类型将数据库对应的数据表转化为实体类对象
     *
     * @return 实体类对象集合
     */
    public static List<EntityMeta> accordDBTypeObtainEntityMetas() {
        List<EntityMeta> entityMetaList = new ArrayList<EntityMeta>();
        String jdbcUrl = Configuration.getJdbcUrl();
        if (jdbcUrl.contains(DataBaseRelateConstant.MYSQL)) {
            entityMetaList = obtainEntityMetas(DataBaseRelateConstant.MYSQL);
            ExceptionHandleAssist.describeFailureEntity(failureEntityList);
        } else if (jdbcUrl.contains(DataBaseRelateConstant.DB2)) {
            entityMetaList = obtainEntityMetas(DataBaseRelateConstant.DB2);
            ExceptionHandleAssist.describeFailureEntity(failureEntityList);
        }
        return entityMetaList;
    }

    /**
     * 将数据库对应的数据表转化为实体类对象
     *
     * @return 实体类对象集合
     */
    private static List<EntityMeta> obtainEntityMetas(String dbType) {
        Connection connection = DataBaseManager.openConnection();
        System.out.println("已经连接到" + dbType + "数据库,开始生成数据库表对应的entity类");
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tableName = null;
        List<EntityMeta> entityMetaList = new ArrayList<EntityMeta>();
        //如果没有配置数据库表,则执行该段代码,直接连接数据库,将数据库中的所有表直接生成entity类
        if (StringUtils.isStrArrayEmpty(Configuration.getTableNames())) {

            try {
                if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                    System.out.println("查询" + Configuration.getSchema() + "下的数据表");
                    System.out.println("***********************************************************");
                    ps = connection.prepareStatement("show tables from " + Configuration.getSchema());
                } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                    System.out.println("查询" + Configuration.getSchema() + "下的数据表");
                    System.out.println("***********************************************************");
                    ps = connection.prepareStatement("select * from syscat.tables where tabschema=?");
                    ps.setString(1, Configuration.getSchema());
                }
            } catch (SQLException e) {
                JDBCUtils.shutDownDataBaseResource(null, ps, connection, true);
                throw new MyException(e, "无法创建PrepareStatement对象");
            }

            try {
                //查询所有的表
                rs = ps.executeQuery();
            } catch (SQLException e) {
                JDBCUtils.shutDownDataBaseResource(rs, ps, connection);
                throw new MyException(e, "无法执行查询数据库表的SQL语句");
            }

            try {
                //迭代表,生成实体类
                while (rs.next()) {
                    EntityMeta entityMeta = new EntityMeta();
                    TableInfo tableInfo = new TableInfo();
                    FailureEntity failureEntity = new FailureEntity();
                    try {
                        if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                            tableName = rs.getString("Tables_in_" + Configuration.getSchema());
                            System.out.println("开始组装" + tableName + "表对应的Entity类");
                        } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                            tableName = rs.getString("TABNAME");
                            System.out.println("开始组装" + tableName + "表对应的Entity类");
                        }
                        //这里主要用到是数据表名
                        tableInfo.setTableName(tableName);
                        //组装表实体对象模型
                        entityMeta.setEntityName(StringUtils.capInMark(tableName, DataBaseRelateConstant.TABLESPILTMARK, true));
                        if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                            System.out.println("<开始组装" + entityMeta.getEntityName() + "实体类>");
                            entityMeta.setFieldMetaList(MySqlTableConvertAssist.obtainFieldMetasForMySQL(connection, tableName, DataBaseRelateConstant.MYSQL, entityMetaList, entityMeta, failureEntityList));
                        } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                            System.out.println("<开始组装" + entityMeta.getEntityName() + "实体类>");
                            entityMeta.setFieldMetaList(Db2TableConvertAssist.obtainFieldMetasForDB2(connection, tableName, DataBaseRelateConstant.DB2, entityMetaList, entityMeta, failureEntityList));
                        }
                        entityMeta.setTableInfo(tableInfo);
                        System.out.println("成功组装" + tableName + "表对应的实体类" + entityMeta.getEntityName());
                        System.out.println("***********************************************************");
                    } catch (Exception e) {
                        //中断此次实体类生成并记录到失败实体类中,并继续进入到下一次循环中
                        new MyException(e, "该" + tableName + "表对应的实体类无法生成");
                        failureEntity.setFailureEntityName(entityMeta.getEntityName());
                        failureEntityList.add(failureEntity);
                        continue;
                    }
                    entityMetaList.add(entityMeta);
                }
            } catch (SQLException e) {
                throw new MyException(e, "结果集不存在");
            } finally {
                JDBCUtils.shutDownDataBaseResource(rs, ps, connection);
            }

        } else {
            //如果配置数据库表,则执行该段代码,用于单表生成代码
            for (String tabName : Configuration.getTableNames()) {
                if (DataBaseCommonAssist.isExistTable(connection, tabName, dbType) != 0) {
                    if (DataBaseRelateConstant.MYSQL.equalsIgnoreCase(dbType)) {
                        System.out.println("开始组装" + tabName + "表对应的Entity类");
                        EntityMeta entityMeta = new EntityMeta();
                        TableInfo tableInfo = new TableInfo();
                        FailureEntity failureEntity = new FailureEntity();
                        try {
                            //这里主要用到是数据表名
                            tableInfo.setTableName(tabName);
                            //组装表实体对象模型
                            entityMeta.setEntityName(StringUtils.capInMark(tabName, DataBaseRelateConstant.TABLESPILTMARK, true));
                            System.out.println("<开始组装" + entityMeta.getEntityName() + "实体类>");
                            entityMeta.setFieldMetaList(MySqlTableConvertAssist.obtainFieldMetasForMySQL(connection, tabName, DataBaseRelateConstant.MYSQL, entityMetaList, entityMeta, failureEntityList));
                            entityMeta.setTableInfo(tableInfo);
                            System.out.println("成功组装" + tabName + "表对应的实体类" + entityMeta.getEntityName());
                            System.out.println("***********************************************************");
                        } catch (Exception e) {
                            //中断此次实体类生成并记录到失败实体类中,并继续进入到下一次循环中
                            new MyException(e, "该" + tableName + "表对应的实体类无法生成");
                            failureEntity.setFailureEntityName(entityMeta.getEntityName());
                            failureEntityList.add(failureEntity);
                            continue;
                        }
                        entityMetaList.add(entityMeta);
                    } else if (DataBaseRelateConstant.DB2.equalsIgnoreCase(dbType)) {
                        System.out.println("开始组装" + tabName + "表对应的Entity类");
                        EntityMeta entityMeta = new EntityMeta();
                        TableInfo tableInfo = new TableInfo();
                        FailureEntity failureEntity = new FailureEntity();
                        try {
                            //这里主要用到是数据表名
                            tableInfo.setTableName(tabName);
                            //组装表实体对象模型
                            entityMeta.setEntityName(StringUtils.capInMark(tabName, DataBaseRelateConstant.TABLESPILTMARK, true));
                            System.out.println("<开始组装" + entityMeta.getEntityName() + "实体类>");
                            entityMeta.setFieldMetaList(Db2TableConvertAssist.obtainFieldMetasForDB2(connection, tabName, DataBaseRelateConstant.DB2, entityMetaList, entityMeta, failureEntityList));
                            entityMeta.setTableInfo(tableInfo);
                            System.out.println("成功组装" + tabName + "表对应的实体类" + entityMeta.getEntityName());
                            System.out.println("***********************************************************");
                        } catch (Exception e) {
                            //中断此次实体类生成并记录到失败实体类中,并继续进入到下一次循环中
                            new MyException(e, "该" + tableName + "表对应的实体类无法生成");
                            failureEntity.setFailureEntityName(entityMeta.getEntityName());
                            failureEntityList.add(failureEntity);
                            continue;
                        }
                        entityMetaList.add(entityMeta);
                    }
                } else {
                    JDBCUtils.shutDownDataBaseResource(rs, ps, connection);
                    throw new MyException("不存在该表,请查看表名是否写正确");
                }
            }
        }
        return entityMetaList;
    }
}
