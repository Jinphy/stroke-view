package com.example.jinphy.stroke_view.models;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.example.jinphy.stroke_view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * DESC: 自定义路径中空
 * Created by jinphy on 2018/9/2.
 */
public class Path extends Shape implements Serializable{

    private List<PointF> points;

    public List<PointF> getPoints() {
        return points;
    }

    public void setPoints(List<PointF> points) {
        this.points = points;
    }

    @Override
    public void init(TypedArray array) {
        String pathStr = array.getString(R.styleable.StrokeView_strokePath);
        if (pathStr == null || pathStr.trim().length() == 0) {
            return;
        }
        points = new ArrayList<>();
        Observable.just(pathStr)
                .subscribeOn(Schedulers.io())
                .map(x -> x.replaceAll(" +", ""))
                .map(x -> x.split("_"))
                .flatMap(Observable::fromArray)
                .map(x -> x.split(":"))
                .map(x -> new PointF(Float.valueOf(x[0]), Float.valueOf(x[1])))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(points::add)
                .subscribe();
    }

    private android.graphics.Path path;

    private void getPath(){
        if (path != null) {
            return;
        }
        calculateStrokeCenter(0, 0);
        for (PointF point : points) {
            point.x += strokeCenter.x;
            point.y += strokeCenter.y;
        }
        path = new android.graphics.Path();

        if (points.size() == 0) {
            return;
        }
        path.moveTo(points.get(0).x, points.get(0).y);

        for (PointF point : points) {
            path.lineTo(point.x, point.y);
        }
    }

    @Override
    protected void onDrawShadowLayer(Canvas canvas) {
        getPath();
        canvas.drawColor(strokeShadowColor);
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawPath(path, strokeShadowPaint);
        canvas.restore();
    }

    @Override
    protected void onDrawForegroundLayer(Canvas canvas) {
        getPath();
        if (foregroundBitmap != null) {
            canvas.drawBitmap(foregroundBitmap, getBitmapSrcMatrix(), getBitmapDstMatrix(), null);
        } else {
            canvas.drawColor(foregroundColor);
        }
        canvas.save();
        canvas.rotate(strokeRotate, strokeCenter.x, strokeCenter.y);
        canvas.drawPath(path, strokeForegroundPaint);
        canvas.restore();
    }
}
