package com.example.jinphy.stroke_view.models;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;


import com.example.jinphy.stroke_view.R;
import com.example.jinphy.stroke_view.StrokeView;
import com.example.jinphy.stroke_view.exception.StrokeGravityException;
import com.example.jinphy.stroke_view.interfaces.ScaleType;
import com.example.jinphy.stroke_view.interfaces.Stroke;
import com.example.jinphy.stroke_view.interfaces.StrokeGravity;
import com.example.jinphy.stroke_view.interfaces.StrokeType;
import com.example.jinphy.stroke_view.utils.NumberU;
import com.example.jinphy.stroke_view.utils.ObjectU;

import java.io.Serializable;
import java.lang.ref.SoftReference;

/**
 * DESC: 中空形状的基类
 * Created by jinphy on 2018/9/2.
 */
public abstract class Shape implements Stroke, Serializable {

    protected SoftReference<StrokeView> view;
    protected float density;

    /**
     * 中空的阴影颜色
     **/
    protected int strokeShadowColor;
    /**
     * 中空的阴影高度
     **/
    protected float strokeElevation; // 该值必须大于0
    /**
     * 前景透明度 [0..255]
     **/
    protected int foregroundAlpha;
    /**
     * 前景颜色，与前景图片二选一
     **/
    protected int foregroundColor = Color.WHITE;
    /**
     * 前景图片，与前景颜色二选一
     **/
    protected Bitmap foregroundBitmap;
    /**
     * 前景图片收缩方式
     **/
    protected int bitmapScaleType;
    /**
     * 中空的对齐方式
     * @see StrokeGravity
     **/
    protected int strokeGravity;
    /**
     * 中空基于某个对齐方式下的X轴方向的偏移量
     * @see StrokeGravity
     **/
    protected float strokeOffsetX;
    /**
     * 中空基于某个对齐方式下的Y轴方向的偏移量
     * @see StrokeGravity
     **/
    protected float strokeOffsetY;
    /**
     * 中空的旋转角度
     **/
    protected float strokeRotate;
    /**
     * 中空的阴影画笔
     **/
    protected Paint strokeShadowPaint;
    /**
     * 中空的前景画笔
     **/
    protected Paint strokeForegroundPaint;
    /**
     * 前景透明度画笔
     **/
    protected Paint alphaPaint;
    /**
     * 中空的中心坐标
     **/
    protected PointF strokeCenter;

    /**
     * 创建指定类型的中空
     **/
    public static Stroke buildStroke(StrokeView strokeView, int strokeType, TypedArray array) {
        switch (strokeType) {
            case StrokeType.RECTANGLE:
                return new Rectangle().init(strokeView, array);
            case StrokeType.CIRCLE:
                return new Circle().init(strokeView, array);
            case StrokeType.OVAL:
                return new Oval().init(strokeView, array);
            case StrokeType.PATH:
                return new Path().init(strokeView, array);
            default:
                return null;
        }
    }

    /**
     * 初始化参数
     **/
    protected  final  <T extends Shape> T init(StrokeView view, TypedArray array) {
        this.view = new SoftReference<>(view);
        density = view.getResources().getDisplayMetrics().density;

        strokeShadowColor =
                array.getColor(R.styleable.StrokeView_strokeShadowColor, 0x7f333333);
        strokeElevation =
                array.getDimension(R.styleable.StrokeView_strokeElevation, 2*density);
        foregroundAlpha = NumberU.ensure(
                array.getInteger(R.styleable.StrokeView_foregroundAlpha, 255), 0, 255);

        bitmapScaleType = array.getInteger(R.styleable.StrokeView_bitmapScaleType, ScaleType.NORMAL);

        if (array.hasValue(R.styleable.StrokeView_foreground)) {
            Drawable drawable =  array.getDrawable(R.styleable.StrokeView_foreground);
            if (drawable instanceof BitmapDrawable) {
                foregroundBitmap = ((BitmapDrawable) drawable).getBitmap();
            }else if (drawable instanceof ColorDrawable) {
                foregroundColor = ((ColorDrawable) drawable).getColor();
            }
        }
        strokeGravity = array.getInteger(R.styleable.StrokeView_strokeGravity, StrokeGravity.CENTER);
        strokeOffsetX = array.getDimension(R.styleable.StrokeView_strokeOffsetX, 0);
        strokeOffsetY = array.getDimension(R.styleable.StrokeView_strokeOffsetY, 0);
        strokeRotate = array.getFloat(R.styleable.StrokeView_strokeRotate, 0);

//        子类实现该方法
        init(array);

        setup();

        return (T) this;
    }

    /**
     * 初始化设置
     **/
    private void setup() {
        // init stroke paint
        strokeShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        strokeShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        strokeShadowPaint.setMaskFilter(new BlurMaskFilter(strokeElevation, BlurMaskFilter.Blur.NORMAL));

        strokeForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        strokeForegroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        alphaPaint.setColor(Color.WHITE);
        alphaPaint.setAlpha(foregroundAlpha);
    }

    /**
     * 由子类实现
     **/
    abstract protected void init(TypedArray array);

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        View view;
        if (this.view == null || (view = this.view.get()) == null) {
            return;
        }
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        Bitmap shadow = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap foreground = shadow.copy(Bitmap.Config.ARGB_8888, true);
        this.onDrawShadowLayer(new Canvas(shadow));
        this.onDrawForegroundLayer(new Canvas(foreground));

        canvas.drawBitmap(shadow, 0, 0, null);
        canvas.drawBitmap(foreground, 0, 0, alphaPaint);
    }

    /**
     * DESC: 绘制阴影层的中空
     * Created by jinphy, on 2018/9/5, at 20:50
     */
    abstract protected void onDrawShadowLayer(Canvas canvas);

    /**
     * DESC: 绘制前景层的中空
     * Created by jinphy, on 2018/9/5, at 20:51
     */
    abstract protected void onDrawForegroundLayer(Canvas canvas);

    /**
     * 计算中空的中心坐标
     **/
    protected PointF calculateStrokeCenter(float strokeWidth, float strokeHeight) {
        String exception = "Cannot set the stroke gravity as %s and %s at the same time";

        PointF point = new PointF();

        View view;
        if (this.view == null || (view = this.view.get()) == null) {
            return point;
        }
        float width = view.getMeasuredWidth();
        float height = view.getMeasuredHeight();

        if (StrokeGravity.isLeft(strokeGravity) & StrokeGravity.isRight(strokeGravity)) {
            throw new StrokeGravityException(String.format(exception, "left", "right"));
        }
        if (StrokeGravity.isTop(strokeGravity) & StrokeGravity.isBottom(strokeGravity)) {
            throw new StrokeGravityException(String.format(exception, "top", "bottom"));
        }

        point.x = strokeWidth / 2f;
        point.y = strokeHeight / 2f;

        if (StrokeGravity.isCenter(strokeGravity)) {
            point.x = width / 2f;
            point.y = height/ 2f;
        }
        if (StrokeGravity.isLeft(strokeGravity)) {
            point.x = strokeWidth / 2f;
        }
        if (StrokeGravity.isRight(strokeGravity)) {
            point.x = width - strokeWidth / 2f;
        }

        if (StrokeGravity.isTop(strokeGravity)) {
            point.y = strokeHeight / 2f;
        }
        if (StrokeGravity.isBottom(strokeGravity)) {
            point.y = height - strokeHeight / 2f;
        }

        point.x += strokeOffsetX;
        point.y += strokeOffsetY;

        strokeCenter = point;
        return point;
    }

    /**
     * DESC: 根据图片的伸缩类型获取图片的源矩阵位置
     * Created by jinphy, on 2018/9/7, at 21:34
     */
    protected Rect getBitmapSrcMatrix() {
        View view;
        if (this.view == null || (view = this.view.get()) == null) {
            return new Rect();
        }
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        Rect rect = new Rect();
        int bWidth = foregroundBitmap.getWidth();
        int bHeight = foregroundBitmap.getHeight();
        switch (bitmapScaleType) {
            case ScaleType.CENTER_CROP:
                rect.left = width < bWidth ? (bWidth - width) >> 1 : 0;
                rect.right = width < bWidth ? bWidth - rect.left : bWidth;
                rect.top = height < bHeight ? (bHeight - height) >> 1 : 0;
                rect.bottom = height < bHeight ? bHeight - rect.top : bHeight;
                break;
            case ScaleType.FIT_X:
                rect.left = 0;
                rect.right = bWidth;
                rect.top = 0;
                rect.bottom = height < bHeight ? height : bHeight;
                break;
            case ScaleType.FIT_Y:
                rect.left = 0;
                rect.right = width < bWidth ? width : bWidth;
                rect.top = 0;
                rect.bottom = bHeight;
                break;
            case ScaleType.FIT_XY:
                rect.left = 0;
                rect.right = bWidth;
                rect.top = 0;
                rect.bottom = bHeight;
                break;
            default://ScaleType.NORMAL
                rect.left = 0;
                rect.right = width < bWidth ? width : bWidth;
                rect.top = 0;
                rect.bottom = height < bHeight ? height : bHeight;
                break;
        }
        return rect;
    }

    /**
     * DESC: 根据图片的伸缩类型来获取图片在view中显示的目标矩阵位置，即将图片映射到view中
     * Created by jinphy, on 2018/9/7, at 21:36
     */
    protected RectF getBitmapDstMatrix() {
        View view;
        if (this.view == null || (view = this.view.get()) == null) {
            return new RectF();
        }
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        RectF rectF = new RectF();
        int bWidth = foregroundBitmap.getWidth();
        int bHeight = foregroundBitmap.getHeight();
        switch (bitmapScaleType) {
            case ScaleType.CENTER_CROP:
                rectF.left = width < bWidth ? 0 : (width - bWidth) >> 1;
                rectF.right = width < bWidth ? width : width - rectF.left;
                rectF.top = height < bHeight ? 0 : (height - bHeight) >> 1;
                rectF.bottom = height < bHeight ? height : height - rectF.top;
                break;
            case ScaleType.FIT_X:
                rectF.left = 0;
                rectF.right = width;
                rectF.top = 0;
                rectF.bottom = height < bHeight ? height : bHeight;
                break;
            case ScaleType.FIT_Y:
                rectF.left = 0;
                rectF.right = width < bWidth ? width : bWidth;
                rectF.top = 0;
                rectF.bottom = height;
                break;
            case ScaleType.FIT_XY:
                rectF.left = 0;
                rectF.right = width;
                rectF.top = 0;
                rectF.bottom = height;
                break;
            default:
                rectF.left = 0;
                rectF.right = width < bWidth ? width : bWidth;
                rectF.top = 0;
                rectF.bottom = height < bHeight ? height : bHeight;
                break;
        }
        return rectF;
    }

    //----------------------------------------------------------------------

    //   Getter

    public int getStrokeShadowColor() {
        return strokeShadowColor;
    }

    public float getStrokeElevation() {
        return strokeElevation;
    }

    public int getForegroundAlpha() {
        return foregroundAlpha;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public Bitmap getForegroundBitmap() {
        return foregroundBitmap;
    }

    public int getBitmapScaleType() {
        return bitmapScaleType;
    }

    public int getStrokeGravity() {
        return strokeGravity;
    }

    public float getStrokeOffsetX() {
        return strokeOffsetX;
    }

    public float getStrokeOffsetY() {
        return strokeOffsetY;
    }

    public float getStrokeRotate() {
        return strokeRotate;
    }

    //-----------------------------------------------

    // Setter


    public <T extends Shape> T setStrokeShadowColor(int strokeShadowColor) {
        this.strokeShadowColor = strokeShadowColor;
        return (T) this;
    }

    public <T extends Shape> T  setStrokeElevation(float strokeElevation) {
        if (strokeElevation <= 0) {
            ObjectU.throwIllegal("strokeElevation must be big than 0");
        }
        this.strokeElevation = strokeElevation;
        return (T) this;
    }

    public <T extends Shape> T  setForegroundAlpha(int foregroundAlpha) {
        foregroundAlpha = NumberU.ensure(foregroundAlpha, 0, 255);
        this.foregroundAlpha = foregroundAlpha;
        return (T) this;
    }

    public <T extends Shape> T  setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        return (T) this;
    }

    public <T extends Shape> T  setForegroundBitmap(Bitmap foregroundBitmap) {
        ObjectU.throwNull(foregroundBitmap, "foregroundBitmap cannot be null");
        this.foregroundBitmap = foregroundBitmap;
        return (T) this;
    }

    public <T extends Shape> T  setBitmapScaleType(int bitmapScaleType) {
        if (!NumberU.in(bitmapScaleType, ScaleType.NORMAL, ScaleType.CENTER_CROP)) {
            ObjectU.throwIllegal("value of bitmapScaleType must between 0 and 4");
        }
        this.bitmapScaleType = bitmapScaleType;
        return (T) this;
    }

    public <T extends Shape> T  setStrokeGravity(int strokeGravity) {
        if (!NumberU.anyEquals(strokeGravity,
                StrokeGravity.CENTER,
                StrokeGravity.LEFT,
                StrokeGravity.RIGHT,
                StrokeGravity.TOP,
                StrokeGravity.BOTTOM)) {
            ObjectU.throwIllegal("value of strokeGravity is illegal");
        }
        this.strokeGravity = strokeGravity;
        return (T) this;
    }

    public <T extends Shape> T  setStrokeOffsetX(float strokeOffsetX) {
        this.strokeOffsetX = strokeOffsetX;
        return (T) this;
    }

    public <T extends Shape> T  setStrokeOffsetY(float strokeOffsetY) {
        this.strokeOffsetY = strokeOffsetY;
        return (T) this;
    }

    public <T extends Shape> T  setStrokeRotate(float strokeRotate) {
        this.strokeRotate = strokeRotate;
        return (T) this;
    }

    public void invalidate(){
        View view;
        if (this.view == null || (view = this.view.get()) == null) {
            return;
        }
        view.invalidate();
    }

    private static final String TAG = "Shape";
}
