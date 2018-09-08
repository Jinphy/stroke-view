package com.example.jinphy.stroke_view.models;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import com.example.jinphy.stroke_view.R;

import java.io.Serializable;

/**
 * DESC: 椭圆形中空
 * Created by jinphy on 2018/9/2.
 */
public class Oval extends Shape implements Serializable{


    /**
     * 中空宽度
     **/
    private float strokeWidth;

    /**
     * 中空高度
     **/
    private float strokeHeight;

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getStrokeHeight() {
        return strokeHeight;
    }

    public void setStrokeHeight(float strokeHeight) {
        this.strokeHeight = strokeHeight;
    }

    @Override
    public void init(TypedArray array) {
        strokeWidth = array.getDimension(R.styleable.StrokeView_strokeWidth, 0);
        strokeHeight = array.getDimension(R.styleable.StrokeView_strokeHeight, 0);
    }

    @Override
    protected void onDrawShadowLayer(Canvas canvas) {
        calculateStrokeCenter(strokeWidth, strokeHeight);
        canvas.drawColor(strokeShadowColor);
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawOval(getShadowStroke(), strokeShadowPaint);
        canvas.restore();
    }

    @Override
    protected void onDrawForegroundLayer(Canvas canvas) {
        calculateStrokeCenter(strokeWidth, strokeHeight);
        if (foregroundBitmap != null) {
            canvas.drawBitmap(foregroundBitmap, getBitmapSrcMatrix(), getBitmapDstMatrix(), null);
        } else {
            canvas.drawColor(foregroundColor);
        }
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawOval(getForegroundStroke(), strokeForegroundPaint);
    }



    /**
     * DESC: 获取前景层中空的矩阵位置
     * Created by jinphy, on 2018/9/5, at 23:22
     */
    private RectF getForegroundStroke(){
        PointF center = calculateStrokeCenter(strokeWidth, strokeHeight);

        RectF stroke = new RectF();
        stroke.left = center.x - strokeWidth / 2f;
        stroke.right = center.x + strokeWidth / 2f;
        stroke.top = center.y - strokeHeight / 2f;
        stroke.bottom = center.y + strokeHeight / 2f;
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

}
