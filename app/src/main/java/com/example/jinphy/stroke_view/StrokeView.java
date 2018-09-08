package com.example.jinphy.stroke_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.example.jinphy.stroke_view.interfaces.Stroke;
import com.example.jinphy.stroke_view.models.Shape;

import java.io.Serializable;

/**
 * DESC: 中空视图
 * Created by jinphy on 2018/9/2, at 10:37
 */
public class StrokeView extends View implements Serializable {

    private Stroke stroke;

    public StrokeView(Context context) {
        super(context);
        init(null, 0);
    }

    public StrokeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StrokeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray array = getContext()
                .obtainStyledAttributes(attrs, R.styleable.StrokeView, defStyle, 0);

        int strokeType = array.getInt(R.styleable.StrokeView_strokeType, 0);
        stroke = Shape.buildStroke(this, strokeType, array);

        array.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        stroke.onDraw(canvas);
    }

    /**
     * DESC: 参数可以使用实现该接口类或者使用自定义的Shape的子类
     * Created by jinphy, on 2018/9/4, at 20:35
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * 获取中空
     **/
    public Stroke getStroke() {
        return stroke;
    }
}
