package com.yhongm.shoppingcart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by yuhongmiao on 2017/6/7.
 */

public class ShoppingCartView extends ViewGroup implements ShoppingCartMainView.OnClickButton {
    private ShoppingCartMainView shoppingCartView;
    private ShoppingCartListener shoppingCartListener;
    private Bitmap mProductBitmap;

    public ShoppingCartView(Context context) {
        this(context, null);
    }

    public ShoppingCartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ShoppingCartMainView scv = new ShoppingCartMainView(context);
        addMainView(scv);
    }


    public void addMainView(View child) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(child, layoutParams);
        shoppingCartView = (ShoppingCartMainView) getChildAt(0);
        shoppingCartView.setListener(this);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            View view = getChildAt(0);
            view.layout(l, t - view.getMeasuredHeight(), l + view.getMeasuredWidth(), t);
        }
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            childAt.layout(l, t - childAt.getMeasuredHeight(), l + childAt.getMeasuredWidth(), t);
        }
    }

    @Override
    public void add(int mProductTotal) {
        if (shoppingCartListener != null) {
            shoppingCartListener.add(mProductTotal);
        }
        ShoppingAnimChildView shoppingAnimView = new ShoppingAnimChildView(getContext());
        if (mProductBitmap != null) {
            shoppingAnimView.setProductBitmap(mProductBitmap);
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(shoppingAnimView, layoutParams);

        requestLayout();
        shoppingAnimView.startAnim();

    }

    @Override
    public void love() {
        boolean love = shoppingCartView.isLove();
        if (shoppingCartListener != null) {
            shoppingCartListener.love(love);
        }

    }

    /**
     * 设置产品产品
     * @param listener
     */
    public void setShoppingCartListener(ShoppingCartListener listener) {
        this.shoppingCartListener = listener;
    }

    /**
     * 设置产品图
     *
     * @param bitmap
     */
    public void setProductBitmap(Bitmap bitmap) {
        this.mProductBitmap = bitmap;
    }

    public interface ShoppingCartListener {
        void add(int num);

        void love(boolean isLove);
    }
}

class ShoppingAnimChildView extends View {
    private int measuredWidth;
    private int measuredHeight;
    private Bitmap bitmap;
    private Bitmap newBitmap;
    private float percent;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    private int currentBitmapWidth = 0;
    private int currentBitmapHeight = 0;
    private ValueAnimator valueAnimator;
    private int[] ints;
    private Paint mBitmapPaint;
    private Paint mLovePaint;

    public ShoppingAnimChildView(Context context) {
        this(context, null);
    }

    public ShoppingAnimChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ints = new int[100];
        for (int i = 0; i < 100; i++) {
            ints[i] = i;
        }
        initPaint();
        initBitmap();
        initAnimator();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAlpha(0);
    }

    /**
     * 改变数值实现动画
     */
    private void initAnimator() {
        valueAnimator = ValueAnimator.ofInt(ints);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                percent = animatedValue / 100f;
                if (percent < 0.8) {
                    int height = (int) (bitmapWidth * (1 - percent));
                    int width = (int) (bitmapHeight * (1 - percent));
                    if (width > 0 && height > 0) {
                        newBitmap = scaleBitmap(bitmap, 1 - percent);
                    }
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBitmapPaint.setAlpha(0);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mBitmapPaint.setAlpha(255);
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());

    }

    /**
     * 开始动画
     */
    public void startAnim() {
        valueAnimator.start();
    }

    private void initBitmap() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), new Matrix(), false);
        currentBitmapWidth = bitmapWidth = bitmap.getWidth();
        currentBitmapHeight = bitmapHeight = bitmap.getHeight();

    }

    /**
     * 缩放bitmap
     * @param bitmap
     * @param scale
     * @return
     */
    public Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(scale, scale);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return bitmap1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = 0;

        int heightSize = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = 800;
        }
        setMeasuredDimension(widthSize, heightSize);
        measuredWidth = getMeasuredWidth();

        measuredHeight = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PointF mCurveStart = new PointF();//曲线开始的点
        PointF mCurveMiddle = new PointF();//曲线中间点
        PointF mCurveEnd = new PointF();//曲线结束的点
        mCurveStart.set(measuredWidth * 0.7f, measuredHeight - 85);
        mCurveMiddle.set(measuredWidth * 0.4f, 0);
        mCurveEnd.set(measuredWidth * 0.3f, measuredHeight - 85);
        Path mCurvePath = new Path();//移动曲线
        mCurvePath.moveTo(mCurveStart.x, mCurveStart.y);
        mCurvePath.cubicTo(mCurveStart.x, mCurveStart.y, mCurveMiddle.x, mCurveMiddle.y, mCurveEnd.x, mCurveEnd.y);
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(mCurvePath, false);
        float[] pos = new float[2];
        pathMeasure.getPosTan(pathMeasure.getLength() * percent, pos, null);
        Matrix matrix = new Matrix();
        matrix.postTranslate(pos[0] - newBitmap.getWidth() / 2, pos[1] - newBitmap.getHeight() / 2);
        canvas.drawBitmap(newBitmap, matrix, mBitmapPaint);

    }

    /**
     * 设置产品图片
     * @param mProductBitmap
     */
    public void setProductBitmap(Bitmap mProductBitmap) {
        bitmap = mProductBitmap;
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), new Matrix(), false);
        currentBitmapWidth = bitmapWidth = bitmap.getWidth();
        currentBitmapHeight = bitmapHeight = bitmap.getHeight();
    }
}

class ShoppingCartMainView extends View {
    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private RectF mAddShoppingCartRectF;
    private RectF mLoveRectF;
    private float mDownX;
    private float mDownY;
    private int mProductTotal = 0;//商品总量
    private Paint mLoveRectPaint;
    private Paint mShoppingCartRectPaint;
    private Paint mAddShoppingCartRectFPaint;
    private TextPaint mTextPaint;
    private boolean isLove = false;
    private Paint mLovePaint;
    private Paint mShoppingCartPaint;

    public ShoppingCartMainView(Context context) {
        this(context, null);
    }

    public ShoppingCartMainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        initPaint();
    }

    private void initPaint() {

        mLoveRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLoveRectPaint.setColor(Color.BLACK);
        mLoveRectPaint.setStyle(Paint.Style.STROKE);
        mShoppingCartRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShoppingCartRectPaint.setColor(Color.BLACK);
        mShoppingCartRectPaint.setStyle(Paint.Style.STROKE);
        mAddShoppingCartRectFPaint = new Paint();
        mAddShoppingCartRectFPaint.setColor(Color.parseColor("#FFA500"));

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(30);

        mLovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mShoppingCartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = 0;

        int heightSize = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = 800;
        }

        setMeasuredDimension(widthSize, heightSize);
        mMeasuredWidth = getMeasuredWidth();

        mMeasuredHeight = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLoveRectF = new RectF();
        mLoveRectF.left = 0;
        mLoveRectF.right = mMeasuredWidth * 0.2f;
        mLoveRectF.top = mMeasuredHeight - 170;
        mLoveRectF.bottom = mMeasuredHeight;

        canvas.drawRect(mLoveRectF, mLoveRectPaint);

        drawLove(canvas, mLoveRectF.centerX(), mLoveRectF.centerY());
        RectF shoppingCartRectF = new RectF();
        shoppingCartRectF.left = mMeasuredWidth * 0.2f;
        shoppingCartRectF.right = mMeasuredWidth * 0.4f;
        shoppingCartRectF.top = mMeasuredHeight - 170;
        shoppingCartRectF.bottom = mMeasuredHeight;


        canvas.drawRect(shoppingCartRectF, mShoppingCartRectPaint);
        drawShoppingCart(canvas, shoppingCartRectF.centerX(), shoppingCartRectF.centerY());

        mAddShoppingCartRectF = new RectF();
        mAddShoppingCartRectF.left = mMeasuredWidth * 0.4f;
        mAddShoppingCartRectF.right = mMeasuredWidth;
        mAddShoppingCartRectF.top = mMeasuredHeight - 170;
        mAddShoppingCartRectF.bottom = mMeasuredHeight;

        canvas.drawRect(mAddShoppingCartRectF, mAddShoppingCartRectFPaint);
        drawShoppingText(canvas, mAddShoppingCartRectF.centerX(), mAddShoppingCartRectF.centerY());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mDownX = event.getX();

                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if (upX > mAddShoppingCartRectF.left && upX < mAddShoppingCartRectF.right && upY > mAddShoppingCartRectF.top && upY < mAddShoppingCartRectF.bottom) {
                    clickAddShoppingButton();
                }
                if (upX > mLoveRectF.left && upX < mLoveRectF.right && upY > mLoveRectF.top && upY < mLoveRectF.bottom) {
                    clickLoveBtn();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 点击喜欢
     */
    private void clickLoveBtn() {
        isLove = !isLove;
        invalidate();
        if (onClickButton != null) {
            onClickButton.love();
        }
    }

    /**
     * 是否为喜欢状态
     *
     * @return
     */
    public boolean isLove() {
        return isLove;
    }

    /**
     * 点击加入购物车
     */
    private void clickAddShoppingButton() {
        mProductTotal++;

        if (onClickButton != null) {
            onClickButton.add(mProductTotal);
        }
        invalidate();
    }

    /**
     * 绘制加购物车
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawShoppingText(Canvas canvas, float x, float y) {
        String str = "加入购物车";
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(50);
        float measureText = textPaint.measureText(str, 0, str.length());
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float v = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(str, x - measureText / 2, y - v / 2 - fontMetrics.top, textPaint);
    }

    /**
     * 绘制喜欢
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawLove(Canvas canvas, float x, float y) {
        canvas.save();
        canvas.translate(x, y);
        Path leftPath = new Path();
        leftPath.moveTo(0, -40);
        Path rightPath = new Path();
        rightPath.moveTo(0, -40);
        leftPath.cubicTo(0, -40, -30, -80, -35, -40);
        leftPath.cubicTo(-35, -40, -30, -30, 0, 10);
        rightPath.cubicTo(0, -40, 30, -80, 35, -40);
        rightPath.cubicTo(35, -40, 30, -30, 0, 10);

        if (isLove) {
            mLovePaint.setStyle(Paint.Style.FILL);
            mLovePaint.setColor(Color.RED);
        } else {
            mLovePaint.setStyle(Paint.Style.STROKE);
            mLovePaint.setColor(Color.BLACK);
        }

        mLovePaint.setStrokeWidth(3);
        canvas.drawPath(leftPath, mLovePaint);
        canvas.drawPath(rightPath, mLovePaint);
        canvas.restore();

        float measureText = mTextPaint.measureText("喜欢", 0, "喜欢".length());

        canvas.drawText("喜欢", x - measureText / 2, y + 50, mTextPaint);
    }

    /**
     * 绘制购物车
     * @param canvas
     * @param x
     * @param y
     */
    private void drawShoppingCart(Canvas canvas, float x, float y) {
        canvas.save();
        canvas.translate(x, y);
        Path mPath = new Path();
        mPath.moveTo(-40, -60);
        mPath.lineTo(-30, -60);
        mPath.lineTo(-20, 0);
        mPath.lineTo(20, 0);
        mPath.lineTo(30, -40);
        mPath.lineTo(-10, -40);
        Path mPath2 = new Path();
        mPath2.moveTo(-10, -25);
        mPath2.lineTo(10, -25);

        mShoppingCartPaint.setStyle(Paint.Style.STROKE);
        mShoppingCartPaint.setStrokeWidth(5);
        mShoppingCartPaint.setColor(Color.BLACK);
        canvas.drawPath(mPath, mShoppingCartPaint);
        canvas.drawPath(mPath2, mShoppingCartPaint);
        mShoppingCartPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(-15, 10, 5, mShoppingCartPaint);
        canvas.drawCircle(15, 10, 5, mShoppingCartPaint);
        mShoppingCartPaint.setColor(Color.RED);
        canvas.drawCircle(30, -40, 20, mShoppingCartPaint);

        String strNum = mProductTotal + "";
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.WHITE);
        float strNumLength = mTextPaint.measureText(strNum, 0, strNum.length());
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        canvas.drawText(strNum, 30 - strNumLength / 2, -40 - (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.top, mTextPaint);
        canvas.restore();
        String str = "购物车";

        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.BLACK);
        float measureText = mTextPaint.measureText(str, 0, str.length());
        canvas.drawText(str, x - measureText / 2, y + 50, mTextPaint);

    }

    public OnClickButton onClickButton;

    public void setListener(OnClickButton onClickAdd) {
        this.onClickButton = onClickAdd;
    }

    public interface OnClickButton {
        /**
         * 点击添加按钮
         *
         * @param mProductTotal
         */
        void add(int mProductTotal);

        /**
         * 点击喜欢按钮
         */
        void love();
    }
}
