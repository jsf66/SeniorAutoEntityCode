package com.unionpay.ost.generateAssistTool;

import com.unionpay.ost.utils.StringUtils;

/**
 * Created by jsf on 16/9/20..
 */
public class SuffixRidAssist {

    //剔除vm后缀名,并将首字符大写
    public static String ridVm(String str){
        String newStr=null;
        if(!StringUtils.isEmpty(str)){
            if(str.contains("vm")){
                int startIndex=str.indexOf("vm");
                newStr=str.substring(0,startIndex-1);
                return StringUtils.capFirst(newStr);
            }
        }
        return newStr;
    }
}
