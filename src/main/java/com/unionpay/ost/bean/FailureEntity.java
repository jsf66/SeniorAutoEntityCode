package com.unionpay.ost.bean;


import java.util.List;
import java.util.Map;

/**
 * Created by jsf on 16/9/21..
 */
public class FailureEntity {

    //生成实体类或该实体类的某个实体字段时放入到该实体集合中
    private  String  failureEntityName;
    private Map<String,List<FieldMeta>>  failureMapField;

    public Map<String, List<FieldMeta>> getFailureMapField() {
        return failureMapField;
    }

    public void setFailureEntityName(String failureEntityName) {
        this.failureEntityName = failureEntityName;
    }

    public String getFailureEntityName() {
        return failureEntityName;
    }

    public void setFailureMapField(Map<String, List<FieldMeta>> failureMapField) {
        this.failureMapField = failureMapField;
    }
}
