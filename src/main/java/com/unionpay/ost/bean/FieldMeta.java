package com.unionpay.ost.bean;

import com.unionpay.ost.table.ColumnInfo;

/**
 * Created by jsf on 16/8/8..
 */
public class FieldMeta {
    private String fieldName;// 属性名
    private String fieldType;// 属性数据类型
    private Long fieldPrecision;//属性精度(字段长度)
    private Long fieldScale;//属性范围(小数位数,如果字段为小数)
    private Long fieldDateTimePrecision;//属性如果是日期类型,表示日期精度
    private Long fieldLength;// 属性字段长度
    private String fieldComment;// 属性注释
    private boolean whetherPK ;// 是否为主键
    private boolean whetherNULL;// 是否可空
    private ColumnInfo columnInfo;//属性对应的列对象
    private SequenceMeta sequenceMeta;//属性对应的序列

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Long getFieldLength() {
        return fieldLength;
    }

    public Long getFieldDateTimePrecision() {
        return fieldDateTimePrecision;
    }

    public Long getFieldPrecision() {
        return fieldPrecision;
    }

    public Long getFieldScale() {
        return fieldScale;
    }

    public boolean isWhetherNULL() {
        return whetherNULL;
    }

    public boolean isWhetherPK() {
        return whetherPK;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public SequenceMeta getSequenceMeta() {
        return sequenceMeta;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }

    public void setFieldLength(Long fieldLength) {
        this.fieldLength = fieldLength;
    }

    public void setFieldDateTimePrecision(Long fieldDateTimePrecision) {
        this.fieldDateTimePrecision = fieldDateTimePrecision;
    }

    public void setFieldPrecision(Long fieldPrecision) {
        this.fieldPrecision = fieldPrecision;
    }

    public void setFieldScale(Long fieldScale) {
        this.fieldScale = fieldScale;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }



    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setWhetherNULL(boolean whetherNULL) {
        this.whetherNULL = whetherNULL;
    }

    public void setWhetherPK(boolean whetherPK) {
        this.whetherPK = whetherPK;
    }

    public void setSequenceMeta(SequenceMeta sequenceMeta) {
        this.sequenceMeta = sequenceMeta;
    }


}
