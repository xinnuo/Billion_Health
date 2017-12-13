package com.ruanmeng.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WrapWebView extends WebView {

	public WrapWebView(Context context) {
		super(context);
	}

	public WrapWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                            MeasureSpec.AT_MOST);

            super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
