package com.unionpay.ost.utils;

import java.util.List;

/**
 * Created by jsf on 16/9/24..
 */
public class CollectionUtils {
    /**
     * 判断集合是否为空
     * @param t
     * @param <T>
     * @return
     */
    public  static <T> boolean isEmpty(List<T> t){
         if(t!=null&&t.size()!=0){
             return false;
         }
        return true;
    }
}
