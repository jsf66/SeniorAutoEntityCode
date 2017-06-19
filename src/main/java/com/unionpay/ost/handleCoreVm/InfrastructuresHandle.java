package com.unionpay.ost.handleCoreVm;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.InfrastructureMeta;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.config.VelocityTemplStyle;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.utils.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.CharArrayReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jsf on 16/9/18..
 */
public class InfrastructuresHandle {

    public static List<InfrastructureMeta> coreVmHandle(VelocityEngine velocityEngine, EntityMeta entityMeta) {
        List<InfrastructureMeta> infrastructureMetaList = new ArrayList<InfrastructureMeta>();
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext();
        SAXReader saxReader = new SAXReader();
        Document document = null;
        //后期改进为名字相同的容器
        context.put("BasePackageEntityName",Configuration.getBasePackageEntityName());
        context.put("BasePackageName", Configuration.getBasePackageName());
        context.put("entityName", entityMeta.getEntityName());
        velocityEngine.mergeTemplate("com/unionpay/ost/coreVm/infrastructure.vm", VelocityTemplStyle.TEMPLATECODE, context, writer);
        CharArrayReader charArrayReader = new CharArrayReader(writer.toString().toCharArray());
        try {
            document = saxReader.read(charArrayReader);
        } catch (DocumentException e) {
            throw new MyException(e, "无法生成文档实体");
        }
        //获取xml文件根节点
        Element root = document.getRootElement();
        //从根节点遍历子节点
        Iterator<Element> iterator = root.elementIterator("infrastructure");
        while (iterator.hasNext()) {
            InfrastructureMeta infrastructureMeta = new InfrastructureMeta();
            //获取当前element元素
            Element element = iterator.next();
            //获取当前element的属性元素
            Iterator<Attribute> attributeIterator = element.attributeIterator();
            while (attributeIterator.hasNext()) {
                //获取其属性元素
                Attribute attribute = attributeIterator.next();
                if ("templateName".equalsIgnoreCase(attribute.getName())) {
                    infrastructureMeta.setTemplateName(attribute.getStringValue());
                } else if ("className".equalsIgnoreCase(attribute.getName())) {
                    infrastructureMeta.setClassName(attribute.getStringValue());
                }
                Iterator<Element> childIterator = element.elementIterator("beanProp");
                while (childIterator.hasNext()) {
                    Element childElement = childIterator.next();
                    String daoName = childElement.attributeValue("daoName");
                    String serviceName = childElement.attributeValue("serviceName");
                    if (!StringUtils.isEmpty(daoName)) {
                        infrastructureMeta.setDaoName(daoName);
                    }
                    if (!StringUtils.isEmpty(serviceName)) {
                        infrastructureMeta.setServiceName(serviceName);
                    }
                }

            }
            infrastructureMetaList.add(infrastructureMeta);
        }
        return infrastructureMetaList;

    }

}
