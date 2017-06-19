package com.unionpay.ost.core;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.FailureEntity;
import com.unionpay.ost.bean.FieldMeta;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.utils.CollectionUtils;
import com.unionpay.ost.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jsf on 16/9/24..
 */
public class ExceptionHandleAssist {
    /**
     * 异常处理的相关的代码
     *
     * @param e
     * @param fieldMeta
     * @param entityMeta
     * @param failureEntity
     * @param failureEntityList
     */
    public static void handleException(Exception e, FieldMeta fieldMeta, EntityMeta entityMeta, FailureEntity failureEntity, List<FailureEntity> failureEntityList) {
        //如果抛出异常,那么将该异常字段放入到失败实体
        boolean flag = false;
        //如果抛出异常,那么将该异常字段放入到失败实体
        if (!StringUtils.isEmpty(fieldMeta.getFieldName())) {
            new MyException(e, "该" + fieldMeta.getFieldName() + "属性无法生成");
        } else {
            new MyException(e);
        }
        if (entityMeta != null) {
            if (!CollectionUtils.isEmpty(failureEntityList)) {
                for (FailureEntity tempFailureEntity : failureEntityList) {
                    if (tempFailureEntity.getFailureEntityName().equalsIgnoreCase(entityMeta.getEntityName())) {
                        tempFailureEntity.getFailureMapField().get(tempFailureEntity.getFailureEntityName()).add(fieldMeta);
                        flag = true;
                    }
                    if (flag) {
                        break;
                    }
                }
                if (!flag) {
                    genFailureEntity(fieldMeta, entityMeta, failureEntity, failureEntityList);
                }
            } else {
                genFailureEntity(fieldMeta, entityMeta, failureEntity, failureEntityList);
            }
        }
    }

    public static void genFailureEntity(FieldMeta fieldMeta, EntityMeta entityMeta, FailureEntity failureEntity, List<FailureEntity> failureEntityList) {
        List<FieldMeta> failureFieldMetaList = new ArrayList<FieldMeta>();
        HashMap<String, List<FieldMeta>> failureFieldMap = new HashMap<String, List<FieldMeta>>();
        failureEntity.setFailureEntityName(entityMeta.getEntityName());
        failureFieldMetaList.add(fieldMeta);
        failureFieldMap.put(entityMeta.getEntityName(), failureFieldMetaList);
        failureEntity.setFailureMapField(failureFieldMap);
        failureEntityList.add(failureEntity);
    }

    /**
     * 描述失败的实体类的相关信息,给生成者以直观的信息
     *
     * @param failureEntityList
     */
    public static void describeFailureEntity(List<FailureEntity> failureEntityList) {
        int i = 1;
        int j = 1;
        System.out.println("!!!!!请注意以下消息(通过以下消息可知实体类以及实体类的相关属性是否生成成功):");
        if (CollectionUtils.isEmpty(failureEntityList)) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!实体类生成成功!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            for (FailureEntity failureEntity : failureEntityList) {
                if (failureEntity.getFailureEntityName() != null) {
                    System.out.println(i + "." + "该实体类" + failureEntity.getFailureEntityName() + "部分自动生成成功,详情如下:");
                    for (FieldMeta fieldMeta : failureEntity.getFailureMapField().get(failureEntity.getFailureEntityName())) {
                        if (fieldMeta != null) {
                            System.out.println(j + ")" + "该实体类" + failureEntity.getFailureEntityName() + "的" + fieldMeta.getFieldName() + "属性自动生成失败.");
                            j++;
                        }
                    }
                    i++;
                }
            }
        }
    }
}
