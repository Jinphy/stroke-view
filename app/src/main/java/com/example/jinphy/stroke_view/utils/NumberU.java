package com.example.jinphy.stroke_view.utils;

/**
 * DESC:
 * Created by jinphy on 2018/9/4.
 */
public class NumberU {

    public static int ensure(int data, int min, int max) {
        return data < min ? min : data > max ? max : data;
    }

    public static boolean in(int data, int min, int max) {
        return data >= min && (data <= max);
    }

    public static boolean anyEquals(int data, int... matches) {
        for (int match : matches) {
            if (match == data) {
                return true;
            }
        }
        return false;
    }
}
