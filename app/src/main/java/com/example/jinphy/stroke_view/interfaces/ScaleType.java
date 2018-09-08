package com.example.jinphy.stroke_view.interfaces;

import java.io.Serializable;

/**
 * DESC: 图片的伸缩类型
 *
 * Created by jinphy on 2018/9/4.
 */
public interface ScaleType extends Serializable{

    /**
     * 正常显示，从图片左上角开始截取指定的大小
     **/
    int NORMAL = 0;

    /**
     * 填充图片X轴方向的所有像素
     **/
    int FIT_X = 1;

    /**
     * 填充图片Y轴方向的所有像素
     **/
    int FIT_Y = 2;

    /**
     * 填充图片的所有像素
     **/
    int FIT_XY = 3;

    /**
     * 从图片中心裁剪指定大小
     **/
    int CENTER_CROP = 4;
}
