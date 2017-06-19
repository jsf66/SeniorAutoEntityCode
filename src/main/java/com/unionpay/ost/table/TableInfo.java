package com.unionpay.ost.table;

import java.util.List;

/**
 * Created by jsf on 16/8/8..
 */
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表的列的信息
     */
    private List<ColumnInfo> columnInfoList;

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
