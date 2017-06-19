package com.unionpay.ost.utils;

import com.unionpay.ost.exception.MyException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 封装了文件常用的操作
 * Created by jsf on 16/8/3..
 */
public class FileUtils {
    /**
     * 迭代删除目录下的所有文件
     *
     * @param file
     */
    public static void deleteAllFile(File file) {
        if (file == null) {
            throw new MyException("无法删除一个空的文件");
        }
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteAllFile(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }

    }

    /**
     * 创建目录,如果该目录下有同名文件,则进行删除
     *
     * @param file
     */
    public static void deleteExistFile(File file) {
        if (file == null) {
            return;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        } else {
            //如果原来有相同目录下有同名文件,则删除后再进行重新生成
            File[] files = parentFile.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    if (f.getName().equals(file.getName())) {
                        f.delete();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 文件复制
     * @param fromPath 从该路径下复制文件
     * @param destPath 复制到该文件路径下
     */
    public static void copyFileWithPath(String fromPath, String destPath) {
        InputStream is=null;
        OutputStream os=null;
        File fromFile = new File(fromPath);
        File destFile = new File(destPath);
        if (fromFile.isDirectory() || destFile.isDirectory()) {
            throw new MyException("不是文件，无法进行读取");
        } else {
            try {
                is = new FileInputStream(fromFile);
                os = new FileOutputStream(destFile);
                byte[] store = new byte[1024];
                int len=0;
                while ((len=is.read(store))!=-1){
                     os.write(store,0,len);
                     os.flush();
                }

            } catch (FileNotFoundException e) {
                throw new MyException(e, "无法读取文件");
            } catch (IOException e) {
                throw new MyException(e, "读取文件失败");
            }finally {
               closeAll(is,os);
            }
        }
    }

    /**
     * 文件复制
     * @param fromPath 从该路径下复制文件
     * @param destPath 复制到该文件路径下
     */
    public static void copyFileWithNIOPath(String fromPath,String destPath){
        File fromFile=new File(fromPath);
        File destFile=new File(destPath);
        int byteReads=-1;
        FileChannel fromFileChannel=null;
        FileChannel destFileChannel=null;
        try {
            fromFileChannel=new FileInputStream(fromFile).getChannel();
            destFileChannel=new FileOutputStream(destFile).getChannel();
            ByteBuffer storeByteBuffer=ByteBuffer.allocate(88);
            while((byteReads=fromFileChannel.read(storeByteBuffer))!=-1){
                //从读模式改成写模式
                storeByteBuffer.flip();
                //将数据写出
                destFileChannel.write(storeByteBuffer);
                //从读模式切换到写模式,准备再次读取数据
                storeByteBuffer.clear();
            }
        } catch (FileNotFoundException e) {
            throw new MyException(e, "无法读取文件");
        } catch (IOException e) {
            throw new MyException(e,"读取文件失败");
        }finally {
            closeAll(fromFileChannel,destFileChannel);
        }

    }

    /**
     * 关闭相关的流
     * @param e
     * @param <E>
     */
    public static <E extends Closeable> void closeAll(E...e){
        for(E obj:e){
            if(obj!=null){
                try {
                    obj.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args){
//        String fromPath="/Users/jsf/Desktop/处理事项/nio-date.txt";
//        String destPath="/Users/jsf/Desktop/处理事项/a.txt";
//        copyFileWithNIOPath(fromPath,destPath);
//
//    }

}
