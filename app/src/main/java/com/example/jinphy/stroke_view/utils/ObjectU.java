package com.example.jinphy.stroke_view.utils;

/**
 * DESC:
 * Created by jinphy on 2018/9/8.
 */
public class ObjectU {

    private ObjectU(){}

    public static void throwNull(Object object) {
        throwNull(object, "object is Null!");
    }

    public static void throwNull(Object object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
    }

    public static void throwIllegal(String msg){
        throw new RuntimeException(msg);
    }

}
