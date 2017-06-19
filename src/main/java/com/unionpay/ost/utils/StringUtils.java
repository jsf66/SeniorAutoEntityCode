package com.unionpay.ost.utils;

/**
 * 封装了字符串常用的操作
 * Created by jsf on 16/8/3..
 */
public class StringUtils {
    /**
     * 将s通过mark分开后的字符串首字母大写并拼装返回
     * @param s 字符串
     * @param mark 分割标记
     * @param flag 返回字符串是否首字母大写
     * @return
     */
    public static String capInMark(String s,String mark,boolean flag){
        if(s == null) return s;

        String str = s.toLowerCase().trim();
        String[] array = str.split(mark);

        StringBuffer sb = new StringBuffer();

        for(int i = 0 ; i< array.length ; i++){
            if(!flag && i==0 ){
                sb.append(array[i]);
                continue;
            }
            sb.append(capFirst(array[i]));
        }

        return sb.toString();
    }

    /**
     * 首字母大写
     * @param s 字符串
     * @return
     */
    public static String capFirst(String s){
        if(s == null||s.length()==0)
            return s;

        StringBuffer sb = new StringBuffer();
        sb.append(Character.toUpperCase(s.charAt(0)));
        if(s.length()>1){
            sb.append(s.substring(1));
        }
        return sb.toString();
    }

    /**
     * 首字母小写
     * @param s
     * @return
     */
    public static String uncapFirst(String s){
        if(s == null||s.length()==0)
            return s;

        StringBuffer sb = new StringBuffer();
        sb.append(Character.toLowerCase(s.charAt(0)));
        if(s.length()>1){
            sb.append(s.substring(1));
        }
        return sb.toString();
    }

    /**
     * 判断字段串是否为空或空字符串
     * @param s
     * @return
     */
    public static boolean isEmpty(String s){
        if(s==null){
            return true;
        }else if("".equals(s)){
            return true;
        }
        return false;
    }

    /**
     * 判断字符串数组是否为空
     * @param str
     * @return
     */
    public static boolean isStrArrayEmpty(String[] str){
        if(str==null){
            return true;
        }else if(str.length==0){
            return true;
        }
        return false;
    }


}
