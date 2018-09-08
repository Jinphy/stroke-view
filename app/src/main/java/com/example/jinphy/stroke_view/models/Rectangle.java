package com.example.jinphy.stroke_view.models;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;


import com.example.jinphy.stroke_view.R;

import java.io.Serializable;

/**
 * DESC: 圆角矩形中空
 * Created by jinphy on 2018/9/2.
 */
public class Rectangle extends Shape implements Serializable{

    /**
     * 中空宽度
     **/
    private float strokeWidth;

    /**
     * 中空高度
     **/
    private float strokeHeight;

    /**
     * 中空的圆角半径
     **/
    private float strokeCornerRadius;


    @Override
    public void init(TypedArray array) {
        strokeWidth = array.getDimension(R.styleable.StrokeView_strokeWidth, 0);
        strokeHeight = array.getDimension(R.styleable.StrokeView_strokeHeight, 0);
        strokeCornerRadius = array.getDimension(R.styleable.StrokeView_strokeCornerRadius, 0);
    }

    @Override
    protected void onDrawShadowLayer(Canvas canvas) {
        calculateStrokeCenter(strokeWidth, strokeHeight);
        canvas.drawColor(strokeShadowColor);
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawRoundRect(getShadowStroke(),strokeCornerRadius,strokeCornerRadius, strokeShadowPaint);
        canvas.restore();
    }

    @Override
    protected void onDrawForegroundLayer(Canvas canvas) {
        if (foregroundBitmap != null) {
            canvas.drawBitmap(foregroundBitmap, getBitmapSrcMatrix(), getBitmapDstMatrix(), null);
        } else {
            canvas.drawColor(foregroundColor);
        }
        calculateStrokeCenter(strokeWidth, strokeHeight);
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawRoundRect(getForegroundStroke(),strokeCornerRadius,strokeCornerRadius, strokeForegroundPaint);
        canvas.restore();
    }

    /**
     * DESC: 获取前景层中空的矩阵位置
     * Created by jinphy, on 2018/9/5, at 23:22
     */
    private RectF getForegroundStroke(){
        if (strokeCenter ==null) {
            calculateStrokeCenter(strokeWidth, strokeHeight);
        }

        RectF stroke = new RectF();
        stroke.left = strokeCenter.x - strokeWidth / 2f;
        stroke.right = strokeCenter.x + strokeWidth / 2f;
        stroke.top = strokeCenter.y - strokeHeight / 2f;
        stroke.bottom = strokeCenter.y + strokeHeight / 2f;

        return stroke;
    }

    /**
     * DESC: 获取阴影层中空的矩阵位置
     * Created by jinphy, on 2018/9/5, at 23:23
     */
    private RectF getShadowStroke() {
        RectF stroke = getForegroundStroke();
        stroke.left += strokeElevation;
        stroke.right -= strokeElevation;
        stroke.top += strokeElevation;
        stroke.bottom -= strokeElevation;
        return stroke;
    }



    public float getStrokeWidth() {
        return strokeWidth;
    }

    public Rectangle setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public float getStrokeHeight() {
        return strokeHeight;
    }

    public Rectangle setStrokeHeight(float strokeHeight) {
        this.strokeHeight = strokeHeight;
        return this;
    }

    public float getStrokeCornerRadius() {
        return strokeCornerRadius;
    }

    public Rectangle setStrokeCornerRadius(float strokeCornerRadius) {
        this.strokeCornerRadius = strokeCornerRadius;
        return this;
    }


    private static final String TAG = "Rectangle";
}
