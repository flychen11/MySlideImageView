package com.nanjing.flychen.myslideimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ww on 2018/4/17.
 */

public class SlideImageView extends View {
    int oldy = 0;
    private int pWidth;//屏幕宽度
    private int pHeight;//屏幕高度
    private Paint paint;
    private int bHeight;//图片高度
    private Bitmap bitmap;//初始化图片
    private int down_y;//手指落下时的y坐标
    private List<Bitmap> list;//图片集合
    private List<BitmapInfo> infoList;//图片位置信息集合
    public SlideImageView(Context context) {
        this(context, null);
    }

    public SlideImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        pWidth = dm.widthPixels;
        pHeight = dm.heightPixels;
        paint = new Paint();
        list = new ArrayList<>();
        infoList = new ArrayList<>();
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_initialization);
        bHeight = bitmap.getHeight();
    }

    public void setBitmaps(List<Bitmap> list) {
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            infoList.add(new BitmapInfo(0, i * bHeight / 4, pWidth, i * bHeight / 4 + bHeight));
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                getParent().requestDisallowInterceptTouchEvent(true);//屏蔽父类的事件屏蔽
                down_y = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP://抬起
                oldy = 0;
                break;
            case MotionEvent.ACTION_MOVE://移动
                getParent().requestDisallowInterceptTouchEvent(true);//屏蔽父类的事件屏蔽
                moveImageView((int) (event.getY() - down_y));
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect srcRect = new Rect(0, 0, pWidth, bHeight);//要绘制的bitmap 区域
        if (list.size() == 0) {
            Rect destRect = new Rect(0, 0, pWidth, bHeight);//要将bitmap 绘制在屏幕的什么地方
            canvas.drawBitmap(bitmap, srcRect, destRect, paint);
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                BitmapInfo bitmapInfo = infoList.get(i);
                Rect destRect = new Rect(bitmapInfo.widthLeft, bitmapInfo.hightTop, bitmapInfo.widthRight, bitmapInfo.hightTop + bHeight);//要将bitmap 绘制在屏幕的什么地方
                canvas.drawBitmap(list.get(i), srcRect, destRect, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (infoList.size() == 0) {
            setMeasuredDimension(pWidth, bHeight);//设置控件宽高
        } else {
            setMeasuredDimension(pWidth, bHeight + infoList.get(infoList.size() - 1).hightTop);//设置控件宽高
        }

    }

    /**
     * 图片根据手指移动距离滑动
     *
     * @param y 手指移动距离
     */
    private void moveImageView(int y) {
        if (infoList.size() == 0) {
            return;
        }
        if (y < 0) {//向上滑动
            for (int i = 0; i < infoList.size(); i++) {
                BitmapInfo bitmapInfo = infoList.get(i);
                if (i == 0) {
                    if (bitmapInfo.hightTop > (-infoList.size() + 1) * bHeight) {
                        bitmapInfo.setHightTop(bitmapInfo.hightTop + y - oldy);
                        if (bitmapInfo.hightTop < (-infoList.size() + 1) * bHeight) {
                            bitmapInfo.setHightTop((-infoList.size() + 1) * bHeight);
                        }
                    }
                } else {
                    BitmapInfo lastBitmapInfo = infoList.get(i - 1);
                    if (bitmapInfo.hightTop > (-infoList.size() + i + 1) * bHeight && lastBitmapInfo.hightTop <= -(4 - i) * bHeight / 4) {
                        bitmapInfo.setHightTop(bitmapInfo.hightTop + y - oldy);
                        if (bitmapInfo.hightTop < (-infoList.size() + i + 1) * bHeight) {
                            bitmapInfo.setHightTop((-infoList.size() + i + 1) * bHeight);
                        }
                    }
                }
            }
        } else {//向下滑动
            for (int i = list.size() - 1; i >= 0; i--) {
                BitmapInfo bitmapInfo = infoList.get(i);
                if (bitmapInfo.hightTop < i * bHeight / 4) {
                    bitmapInfo.setHightTop(bitmapInfo.hightTop + y - oldy);
                    if (bitmapInfo.hightTop > i * bHeight / 4) {
                        bitmapInfo.setHightTop(i * bHeight / 4);
                    }
                }
            }
        }
        oldy = y;
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = bHeight + infoList.get(infoList.size() - 1).hightTop;
        this.setLayoutParams(layoutParams);
        invalidate();
    }

    /**
     * 图片位置信息内部类
     */
    private class BitmapInfo {
        private int widthLeft;
        private int hightTop;
        private int widthRight;
        private int hightBottom;

        BitmapInfo(int widthLeft, int hightTop, int widthRight, int hightBottom) {
            this.widthLeft = widthLeft;
            this.hightTop = hightTop;
            this.widthRight = widthRight;
            this.hightBottom = hightBottom;
        }

        void setHightTop(int hightTop) {
            this.hightTop = hightTop;
        }
    }
}
