package com.unionpay.ost.generateAssistTool;

import java.util.Arrays;

/**
 * Created by jsf on 16/9/21..
 */
//该类主要用于生成SerialVersionUID
public class RandomSerialVersionUID {
    //枚举类定义随机数生成强度
    public enum PasswordLevel{Simple,Medium,Hard};


    public static String genSerialVersionUID(){
        return System.currentTimeMillis()+GenerateSerialVersionUID(5,PasswordLevel.Simple,false);
    }
    private static String GenerateSerialVersionUID(int length,PasswordLevel passwordLevel,boolean isCanRepeat){
        char[] codes={'1','2','3','4','5','6','7','8','9',
                'a','b','c','d','e','f','g','h','i','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
        if(passwordLevel==PasswordLevel.Simple){
            codes=Arrays.copyOfRange(codes, 0, 9);
        }else if(passwordLevel==PasswordLevel.Medium){
            codes= Arrays.copyOfRange(codes, 0, 33);
        }else if(passwordLevel==PasswordLevel.Hard){
            codes=Arrays.copyOfRange(codes, 0, 58);
        }
        int n=codes.length;
        if(length>n){
            throw new RuntimeException("您输入的条件有问题，无法进行密码的生成");
        }
        char[] result=new char[length];
        if(isCanRepeat){
            for(int i=0;i<result.length;i++){
                int r=(int)(Math.random()*n);
                result[i]=codes[r];
            }
        }else{
            for(int i=0;i<result.length;i++){
                int r=(int)(Math.random()*n);
                result[i]=codes[r];
                codes[r]=codes[n-1];
                n--;
            }
        }

        return String.valueOf(result);
    }


}
