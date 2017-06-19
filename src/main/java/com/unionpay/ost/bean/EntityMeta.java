package com.unionpay.ost.bean;

import com.unionpay.ost.table.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体元数据模型
 * Created by jsf on 16/8/8..
 */
public class EntityMeta {
    private String entityName;// 实体名称
    private List<FieldMeta> fieldMetaList = new ArrayList<FieldMeta>();// 字段集合
    private TableInfo tableInfo;// 实体类对应的表的对象

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public List<FieldMeta> getFieldMetaList() {
        return fieldMetaList;
    }

    public void setFieldMetaList(List<FieldMeta> fieldMetaList) {
        this.fieldMetaList = fieldMetaList;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
