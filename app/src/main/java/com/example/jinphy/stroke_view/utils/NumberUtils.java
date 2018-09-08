package com.example.jinphy.stroke_view.utils;

/**
 * DESC:
 * Created by jinphy on 2018/9/4.
 */
public class NumberUtils {

    public static int ensure(int data, int min, int max) {
        return data < min ? min : data > max ? max : data;
    }
}
