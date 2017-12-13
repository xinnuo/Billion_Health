package com.ruanmeng.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ruanmeng.billion_health.R;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:用于显示9宫格图片
 * 作者：FrankChoo
 */
public class NineGridLayout extends FrameLayout {

    private static final float DEFUALT_SPACING = 10f; //默认的间距
    private static final int MAX_COUNT = 9;           //最多能显示的图片
    private static final int MAX_W_H_RATIO = 3;       //最大宽高比

    private Context mContext;
    private float mSpacing = DEFUALT_SPACING; //图片之间的间距
    private int mColumns;                     //图片的列数
    private int mRows;                        //图片的函数

    private int mTotalWidth;  //该自定义总宽度
    private int mSingleWidth; //每一张图片的宽度

    private ArrayList<String> mUriList = new ArrayList<>(); //要显示的图片的URI集合
    private onClickImageListener mListener;                 //点击监听器

    public NineGridLayout(Context context) {
        super(context);
        init(context);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        WindowManager ws = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = ws.getDefaultDisplay();
        //noinspection deprecation
        mTotalWidth = display.getWidth() - DensityUtil.dp2px(80f);
        mSingleWidth = (mTotalWidth) * 3 / 10;
    }

    /**
     * 设置间距
     */
    public void setSpacing(float spacing) {
        mSpacing = spacing;
    }

    /**
     * 设置图片的URI地址列表
     */
    public void loadUriList(List<String> uriList) {
        if (uriList != null) {
            mUriList.clear();
            for (String item : uriList) mUriList.add(HttpIP.BaseImg + item);
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置点击图片的监听器
     */
    public void setOnClickImageListener(onClickImageListener listener) {
        mListener = listener;
    }

    /**
     * 在onMeasure之前执行
     */
    @Override
    protected void onAttachedToWindow() {
        if (mUriList.size() == 0) {
            setVisibility(View.GONE);
        }
        if (getVisibility() == View.GONE) return;
        showView();
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //勿用changed, 有时候addView之后changed不一定为true, 导致布局错乱
        if (getChildCount() != 0) {
            int childCount = getChildCount();
            if (childCount == 1) {
                //只有一张图片的时候, 图片的位置根据图片的的宽高来定
                View childView = getChildAt(0);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                childView.layout(0, 0, childWidth, childHeight);

            } else if (childCount > 1) {
                //多于一张图片的时候, 显示固定的9宫格图片, 宽高都是mSingleWidth
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    int childWidth = childView.getMeasuredWidth();
                    //设置ImageView的位置
                    int[] position = getImageColumnsAndRows(i);
                    int mLeft = (int) ((childWidth + mSpacing) * position[1]);
                    int mTop = (int) ((childWidth + mSpacing) * position[0]);
                    int mRight = mLeft + childWidth;
                    int mBottom = mTop + childWidth;
                    childView.layout(mLeft, mTop, mRight, mBottom);
                    Log.d("onLayout", "layout_children" + i);
                }
                //只有当图片超过MAX_COUNT的时候才会添加一个TextView即第十个child
                if (childCount == 10) {
                    View childView = getChildAt(childCount - 1);
                    int childWidth = childView.getMeasuredWidth();
                    int mLeft = (int) ((childWidth + mSpacing) * 2);
                    int mTop = (int) ((childWidth + mSpacing) * 2);
                    int mRight = mLeft + childWidth;
                    int mBottom = mTop + childWidth;
                    childView.layout(mLeft, mTop, mRight, mBottom);
                }
            }
        }
    }

    /**
     * 刷新该自定义View
     */
    private void showView() {
        removeAllViews();
        int count = mUriList.size();
        if (count == 1) {
            String url = mUriList.get(0);
            setupLayoutParams(this, mSingleWidth, mSingleWidth); //设置图片未加载出来之前该ViewGroup的宽高
            CustomImageView imageView = createImageView(0, url);
            addView(imageView); //将ImageView加入该ViewGroup中

        } else if (count <= MAX_COUNT) {
            setColumnsAndRows(count); //设置行数和列数
            setupLayoutParams(this, mTotalWidth, (int) (mSingleWidth * mRows + mSpacing * (mRows - 1))); //设置该ViewGroup的宽高

            //循环layout ImageView确定其位置
            for (int i = 0; i < count; i++) {
                String uri = mUriList.get(i);
                CustomImageView imageView = createImageView(i, uri);
                addView(imageView);//将ImageView加入该ViewGroup中
            }

        } else if (count > MAX_COUNT) {
            setColumnsAndRows(count);//设置行数和列数
            setupLayoutParams(this, mTotalWidth, (int) (mSingleWidth * mRows + mSpacing * (mRows - 1)));//设置该ViewGroup的宽高

            //循环layout ImageView确定其位置
            for (int i = 0; i < MAX_COUNT; i++) {
                String uri = mUriList.get(i);
                CustomImageView imageView = createImageView(i, uri);
                addView(imageView); //将ImageView加入该ViewGroup中
            }

            //添加超过最大显示数量的文本
            int overCount = mUriList.size() - MAX_COUNT;
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new LayoutParams(mSingleWidth, mSingleWidth));
            textView.setText("+" + String.valueOf(overCount));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(30);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.BLACK);
            textView.getBackground().setAlpha(120);
            addView(textView);
        }

    }

    /**
     * 创建CustomImageView并且返回实例
     */
    private CustomImageView createImageView(final int position, final String uri) {
        final CustomImageView imageView = new CustomImageView(mContext);
        //设置ImageView预设宽高
        imageView.setLayoutParams(new LayoutParams(mSingleWidth, mSingleWidth));
        //设置图片占位符
        imageView.setImageResource(R.mipmap.not_1);
        //设置图片显示模式
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //设置图片的点击事件
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickNineImage(position, imageView, uri, mUriList);
                }
            }
        });
        loadImageUri(imageView, uri);
        return imageView;
    }

    /**
     * 根据图片个数确定行列数量
     */
    private void setColumnsAndRows(int count) {
        if (count <= 3) {
            mRows = 1;
            mColumns = count;
        } else if (count <= 6) {
            mRows = 2;
            mColumns = 3;
            if (count == 4) {
                mColumns = 2;
            }
        } else {
            mColumns = 3;
            mRows = 3;
        }
    }

    /**
     * 根据ImageView在ViewGroup中的位置获取到它的行和列
     */
    private int[] getImageColumnsAndRows(int childNum) {
        int[] position = new int[2];
        if (childNum < MAX_COUNT) {
            for (int i = 0; i < mRows; i++) {
                for (int j = 0; j < mColumns; j++) {
                    if ((i * mColumns + j) == childNum) {
                        position[0] = i;//行
                        position[1] = j;//列
                        break;
                    }
                }
            }
        }

        return position;
    }

    /**
     * 加载九宫格图片
     */
    private void loadImageUri(final CustomImageView imageView, final String uri) {
        if (mUriList.size() == 1) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(uri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

                            int originalWidth = bitmap.getWidth();
                            int originalHeight = bitmap.getHeight();
                            int newWidth;
                            int newHeight;
                            /*//宽高的缩放比例为16 : 9
                            if (originalHeight > originalWidth) {
                                newWidth = mTotalWidth / 2;
                                newHeight = newWidth * 16 / 9;
                            } else {
                                newWidth = mTotalWidth * 2 / 3;
                                newHeight = newWidth * 9 / 16;
                            }*/
                            if (originalHeight > originalWidth * MAX_W_H_RATIO) { //h:w = 5:3
                                newWidth = mTotalWidth / 2;
                                newHeight = newWidth * 5 / 3;
                            } else if (originalHeight < originalWidth) { //h:w = 2:3
                                newWidth = mTotalWidth * 2 / 3;
                                newHeight = newWidth * 2 / 3;
                            } else { //newH:h = newW :w
                                newWidth = mTotalWidth / 2;
                                newHeight = originalHeight * newWidth / originalWidth;
                            }

                            //重新设置该ViewGroup的宽高
                            setupLayoutParams(NineGridLayout.this, newWidth, newHeight);
                            //重新设置ImageView的宽高
                            setupLayoutParams(imageView, newWidth, newHeight);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
        } else {
            Glide.with(mContext)
                    .load(uri)
                    .apply(RequestOptions
                            .centerCropTransform()
                            .placeholder(R.mipmap.not_1)
                            .error(R.mipmap.not_1)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate())
                    .into(imageView);
        }
    }

    /**
     * 设置View的宽高
     */
    private void setupLayoutParams(View target, int width, int height) {
        ViewGroup.LayoutParams params = target.getLayoutParams();
        params.width = width;
        params.height = height;
        target.setLayoutParams(params);
    }

    /**
     * 自定义一个按下时有点击特效的ImageView
     */
    private static class CustomImageView extends AppCompatImageView {

        public CustomImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomImageView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        drawable.mutate().setColorFilter(Color.GRAY,
                                PorterDuff.Mode.MULTIPLY);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Drawable drawableUp = getDrawable();
                    if (drawableUp != null) {
                        drawableUp.mutate().clearColorFilter();
                    }
                    break;
            }

            return super.onTouchEvent(event);
        }
    }

    /**
     * 回调接口
     */
    public interface onClickImageListener {
        void onClickNineImage(int position, View view, String url, ArrayList<String> urlList);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
