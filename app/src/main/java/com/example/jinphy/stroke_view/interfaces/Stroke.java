package com.example.jinphy.stroke_view.interfaces;

import android.graphics.Canvas;

import java.io.Serializable;

/**
 * DESC: 中空接口，负责绘制中空
 * Created by jinphy on 2018/9/2.
 */
public interface Stroke extends Serializable{

    void onDraw(Canvas canvas);

}
