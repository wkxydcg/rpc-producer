package com.wkx.provider.util;

import java.util.HashSet;
import java.util.Set;

public class ClassUtils {

    private static Set<Class> baseClassSet;

    static {
        baseClassSet=new HashSet<>();
        baseClassSet.add(String.class);
        baseClassSet.add(byte.class);
        baseClassSet.add(Byte.class);
        baseClassSet.add(short.class);
        baseClassSet.add(int.class);
        baseClassSet.add(long.class);
        baseClassSet.add(float.class);
        baseClassSet.add(double.class);
        baseClassSet.add(boolean.class);
        baseClassSet.add(Boolean.class);
    }

    public static boolean checkIsBaseClass(Class cs){
        if(baseClassSet.contains(cs)) return true;
        try {
            cs.asSubclass(Number.class);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static Object castBaseType(Object value,Class needType){
        if(needType==String.class){
            return String.valueOf(value);
        }else if(needType==byte.class||needType==Byte.class){
            return Byte.parseByte(String.valueOf(value));
        }else if(needType==short.class||needType==Short.class){
            return Short.parseShort(String.valueOf(value));
        }else if(needType==int.class||needType==Integer.class){
            return Integer.parseInt(String.valueOf(value));
        }else if(needType==long.class||needType==Long.class){
            return Long.parseLong(String.valueOf(value));
        }else if(needType==float.class||needType==Float.class){
            return Float.parseFloat(String.valueOf(value));
        }else if(needType==double.class||needType==Double.class){
            return Double.parseDouble(String.valueOf(value));
        }else if(needType==boolean.class||needType==Boolean.class){
            return Boolean.parseBoolean(String.valueOf(value));
        }else{
            return null;
        }
    }

}
