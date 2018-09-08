package com.example.jinphy.stroke_view.models;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.example.jinphy.stroke_view.R;

import java.io.Serializable;

/**
 * DESC: 圆形中空
 * Created by jinphy on 2018/9/2.
 */
class Circle extends Shape implements Serializable {

    // 半径
    private float strokeRadius;




    public float getStrokeRadius() {
        return strokeRadius;
    }

    public Circle setStrokeRadius(float strokeRadius) {
        this.strokeRadius = strokeRadius;
        return this;
    }


    @Override
    public void init(TypedArray array) {
        strokeRadius = array.getDimension(R.styleable.StrokeView_strokeRadius, 0);
    }

    @Override
    protected void onDrawShadowLayer(Canvas canvas) {
        PointF center = calculateStrokeCenter(strokeRadius*2, strokeRadius*2);
        canvas.drawColor(strokeShadowColor);
        canvas.drawCircle(center.x,center.y,strokeRadius- strokeElevation, strokeShadowPaint);
    }

    @Override
    protected void onDrawForegroundLayer(Canvas canvas) {
        if (foregroundBitmap != null) {
            canvas.drawBitmap(foregroundBitmap, getBitmapSrcMatrix(), getBitmapDstMatrix(), null);
        } else {
            canvas.drawColor(foregroundColor);
        }
        PointF center = calculateStrokeCenter(strokeRadius*2, strokeRadius*2);
        canvas.drawCircle(center.x, center.y, strokeRadius, strokeForegroundPaint);
    }
}
