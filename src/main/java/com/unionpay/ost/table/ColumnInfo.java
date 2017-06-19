package com.unionpay.ost.table;

/**
 * Created by jsf on 16/8/8..
 */
public class ColumnInfo {
     private String columnName;
     private String columnDateType;
    private long columnLength;
    private String columnKey;
    private String columnIsNull;
    private String columnComment;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDateType() {
        return columnDateType;
    }

    public void setColumnDateType(String columnDateType) {
        this.columnDateType = columnDateType;
    }

    public long getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(long columnLength) {
        this.columnLength = columnLength;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getColumnIsNull() {
        return columnIsNull;
    }

    public void setColumnIsNull(String columnIsNull) {
        this.columnIsNull = columnIsNull;
    }
}
