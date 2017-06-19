package com.unionpay.ost.core;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.bean.InfrastructureMeta;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.config.DataBaseRelateConstant;
import com.unionpay.ost.config.VelocityTemplStyle;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.generateAssistTool.RandomSerialVersionUID;
import com.unionpay.ost.generateAssistTool.SuffixRidAssist;
import com.unionpay.ost.handleCoreVm.InfrastructuresHandle;
import com.unionpay.ost.utils.FileUtils;
import com.unionpay.ost.utils.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.List;
import java.util.Properties;


/**
 * Created by jsf on 16/8/8..
 * Version 1.1
 */
public class ProduceEntityCode {
    static VelocityEngine velocityEngine = null;

    //初始化相关的模板参数
    static {
        Properties properties = new Properties();
        //设置velocity资源加载方式为class
        properties.setProperty("resource.loader", "class");
        //设置velocity资源加载方式为file时的处理类
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //设置模板编码
        properties.setProperty(Velocity.ENCODING_DEFAULT, Configuration.encoding);
        properties.setProperty(Velocity.INPUT_ENCODING, Configuration.encoding);
        properties.setProperty(Velocity.OUTPUT_ENCODING, Configuration.encoding);
        //实例化一个VelocityEngine对象
        velocityEngine = new VelocityEngine(properties);
    }

    //程序入口
    public static void main(String[] args) {
        constructTemplate(TableConvertEntity.accordDBTypeObtainEntityMetas());
    }

    //构造模板
    private static void constructTemplate(List<EntityMeta> entityMetaList) {
        VelocityContext context = new VelocityContext();
        context.put("StringUtils", StringUtils.class);
        context.put("Configuration", Configuration.class);
        context.put("SerialVersionUID", RandomSerialVersionUID.class);
        if (entityMetaList != null && entityMetaList.size() != 0) {
            //先删除原来已经存在的文件
            FileUtils.deleteAllFile(new File(Configuration.getSrcPath() + File.separator + Configuration.getBasePackageName().replace('.', '/')));
            for (EntityMeta entityMeta : entityMetaList) {
                context.put("entity", entityMeta);
                //如果类名中带有PK则不进行dao类以及后续的类的生成
                if (entityMeta.getEntityName().contains(DataBaseRelateConstant.PRIKEYTABLESUFFIX)) {
                    generateEntityFile(context, null);
                    continue;
                }
                //如果isGenOther为"true",那么生成该类对应的其他对应的action,service,serviceImpl,dao.否则,只生成entity实体类
                if (Configuration.getIsGenOther().equalsIgnoreCase("true")) {
                    List<InfrastructureMeta> infrastructureMetaList = InfrastructuresHandle.coreVmHandle(velocityEngine, entityMeta);
                    for (InfrastructureMeta infrastructureMeta : infrastructureMetaList) {
                        generateEntityFile(context, infrastructureMeta);
                    }
                    System.out.println("<!" + entityMeta.getEntityName() + "实体类对应的dao层,service层,controller层中各实体生成成功!>");
                } else {
                    generateEntityFile(context, null);
                }
            }
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<该类实体生成完毕>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }

    //生成相应的实体类文件
    private static void generateEntityFile(VelocityContext context, InfrastructureMeta infrastructureMeta) {
        File file = null;
        String templateName = null;
        FileWriter fileWriter = null;
        EntityMeta entityMeta = (EntityMeta) context.get("entity");
        if (infrastructureMeta == null) {
            //说明是符合主键,类名中包含PK,复合主键所在的类不用生成dao,service,seriveImpl类
//            file = new File(Configuration.getSrcPath()+File.separator+Configuration.getBasePackageName().replace('.','/')+File.separator+"entity"+File.separator+entityMeta.getEntityName()+".java");
            file = new File(Configuration.getSrcPath() + File.separator + Configuration.getBasePackageEntityName().replace('.', '/') + File.separator + entityMeta.getEntityName() + ".java");
            templateName = "com/unionpay/ost/model/entity.vm";
        } else {
            file = new File(Configuration.getSrcPath() + File.separator + infrastructureMeta.getClassName().replace('.', '/') + ".java");
            templateName = "com/unionpay/ost/model/" + infrastructureMeta.getTemplateName();

        }
        FileUtils.deleteExistFile(file);
        //实例化一个StringWriter
        StringWriter writer = new StringWriter();
        //动态的构建模板名
        velocityEngine.mergeTemplate(templateName, VelocityTemplStyle.TEMPLATECODE, context, writer);
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), Configuration.encoding);
            osw.write(writer.toString());
            osw.flush();
            osw.close();
            if (infrastructureMeta != null) {
                System.out.println("组装完成" + entityMeta.getEntityName() + "实体类对应的" + SuffixRidAssist.ridVm(infrastructureMeta.getTemplateName()) + "类");
            }
        } catch (IOException e) {
            throw new MyException(e, "写出文件异常");
        } finally {
            FileUtils.closeAll(writer, fileWriter);
        }

    }

}
