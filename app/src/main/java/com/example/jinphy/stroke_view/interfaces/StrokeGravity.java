package com.example.jinphy.stroke_view.interfaces;

import java.io.Serializable;

/**
 * DESC: 中空的对齐方式
 * Created by jinphy on 2018/9/4.
 */
public interface StrokeGravity extends Serializable {

    /**
     * 在View的中间
     **/
    int CENTER = 0b00001;

    /**
     * 中空的左边缘与View的左边缘对齐
     **/
    int LEFT = 0b00010;

    /**
     * 中空的右边缘与View的右边缘对齐
     */
    int RIGHT = 0b00100;

    /**
     * 中空的上边缘与View的上边缘对齐
     **/
    int TOP = 0b01000;

    /**
     * 中空的下边缘与View的下边缘对齐
     **/
    int BOTTOM = 0b10000;


    static boolean isCenter(int gravity) {
        return (StrokeGravity.CENTER & gravity) != 0;
    }

    static boolean isLeft(int gravity) {
        return (StrokeGravity.LEFT & gravity) != 0;
    }

    static boolean isRight(int gravity) {
        return (StrokeGravity.RIGHT & gravity) != 0;
    }

    static boolean isTop(int gravity) {
        return (StrokeGravity.TOP & gravity) != 0;
    }

    static boolean isBottom(int gravity) {
        return (StrokeGravity.BOTTOM & gravity) != 0;
    }

}
