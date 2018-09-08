package com.example.jinphy.stroke_view.interfaces;

import java.io.Serializable;

/**
 * DESC: 中空的形状
 * Created by jinphy on 2018/9/4.
 */
public interface StrokeType  extends Serializable{

    /**
     * 圆角矩形
     **/
    int RECTANGLE = 0;

    /**
     * 圆形
     **/
    int CIRCLE = 1;

    /**
     * 椭圆形
     **/
    int OVAL = 2;

    /**
     * 自定义路径
     **/
    int PATH = 3;
}
