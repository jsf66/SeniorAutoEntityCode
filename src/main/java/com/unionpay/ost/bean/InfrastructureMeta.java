package com.unionpay.ost.bean;

/**
 * 基础架构元数据
 * Created by jsf on 16/9/18..
 */
public class InfrastructureMeta {
    private String templateName;//基础架构的模板名称
    private String className;//基础架构的类名同时该类名也包含该文件存储在的位置
    private String daoName;//基础架构中引用到的dao的名称,主要针对service层
    private String serviceName;//基础架构中引用到的service的名字,主要针对action层

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDaoName() {
        return daoName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
