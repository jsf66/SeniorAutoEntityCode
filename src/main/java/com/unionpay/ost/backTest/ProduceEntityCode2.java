package com.unionpay.ost.backTest;

import com.unionpay.ost.bean.EntityMeta;
import com.unionpay.ost.config.Configuration;
import com.unionpay.ost.core.TableConvertEntity;
import com.unionpay.ost.exception.MyException;
import com.unionpay.ost.utils.FileUtils;
import com.unionpay.ost.utils.StringUtils;
import org.apache.velocity.Template;
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
public class ProduceEntityCode2 {
    private static Template template=null;
    //初始化相关的模板参数
    static{
        Properties properties = new  Properties();
        VelocityEngine velocityEngine = new  VelocityEngine();
        File file=new File(ProduceEntityCode2.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String velocityPath=file.getParent();
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,velocityPath);
        velocityEngine.init(properties);
        template=velocityEngine.getTemplate("entity.vm");
    }


    public static void main(String[] args){
        constructTemplate(TableConvertEntity.accordDBTypeObtainEntityMetas());
    }
    //构造模板
    private static void constructTemplate(List<EntityMeta>  entityMetaList){
        VelocityContext context = new VelocityContext();
        context.put("StringUtils", StringUtils.class);
        context.put("Configuration",Configuration.class);
        if(entityMetaList!=null&&entityMetaList.size()!=0){
            //先删除原来已经存在的文件
            FileUtils.deleteAllFile(new File(Configuration.getSrcPath()+File.separator+Configuration.getBasePackageName().replace('.','/')));
            for(EntityMeta entityMeta:entityMetaList){
                context.put("entity",entityMeta);
                generateEntityFile(context);
            }
        }

    }
    //生成相应的实体类文件
    private static void generateEntityFile(VelocityContext context){
        EntityMeta entityMeta=(EntityMeta)context.get("entity");
        File file = new File(Configuration.getSrcPath()+File.separator+Configuration.getBasePackageName().replace('.','/')+File.separator+entityMeta.getEntityName()+".java");
        FileUtils.deleteExistFile(file);
        FileOutputStream out = null;
        OutputStreamWriter writer = null;
        try{
            out = new FileOutputStream(file);
            writer = new OutputStreamWriter(out, "UTF-8");
            template.merge(context, writer);
            writer.flush();
            out.flush();
        } catch (FileNotFoundException e) {
            throw new MyException(e,"无法找到输出文件");
        } catch (UnsupportedEncodingException e) {
            throw new MyException(e,"不支持该种编码格式");
        } catch (IOException e) {
            throw new MyException(e,"无法生成代码文件");
        } finally {
            try {
                if(writer!=null){
                    writer.close();
                }
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                throw new MyException(e,"无法关闭流");
            }
        }

    }

}
