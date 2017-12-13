package com.ruanmeng.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ruanmeng.billion_health.R;
import com.ruanmeng.model.CommentData;
import com.ruanmeng.model.CommonData;

import java.util.ArrayList;
import java.util.List;

public class SwitcherTextView extends ViewSwitcher implements ViewSwitcher.ViewFactory {

    private List<CommonData> demoBeans = new ArrayList<>();
    private int mIndex;
    private ItemDataListener itemDataListener;
    private View view; //click view
    private static final int AUTO_RUN_FLIP_TEXT = 11;
    private static final int WAIT_TIME = 3000;

    public SwitcherTextView(Context context) {
        super(context);
        init();
    }

    public SwitcherTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFactory(this);
        setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.trans_bottom_to_top_in_fast));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.trans_bottom_to_top_out_fast));
    }

    @Override
    public View makeView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_switcher_text, null);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(params);
        return view;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_RUN_FLIP_TEXT:

                    if (demoBeans.size() > 0) {
                        mHandler.sendEmptyMessageDelayed(AUTO_RUN_FLIP_TEXT, WAIT_TIME);
                        setText(demoBeans.get(mIndex).getTitle());
                    }
                    mIndex++;
                    if (mIndex > demoBeans.size() - 1) {
                        mIndex = 0;
                    }
                    if (itemDataListener != null) {
                        view.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mIndex == 0) {
                                    itemDataListener.onItemClick(demoBeans.size() - 1);
                                } else {
                                    itemDataListener.onItemClick(mIndex - 1);
                                }
                            }
                        });
                    }
                    break;
            }
        }
    };

    public void setData(List<CommonData> datas, ItemDataListener listener, View v) {
        view = v;
        itemDataListener = listener;
        if (demoBeans.size() > 0) {
            demoBeans.clear();
        }
        demoBeans.addAll(datas);
        mIndex = 0;
        mHandler.removeMessages(AUTO_RUN_FLIP_TEXT);
        mHandler.sendEmptyMessage(AUTO_RUN_FLIP_TEXT);
    }

    public void setText(String title) {
        final View view = getNextView();
        TextView tv_title = (TextView) view.findViewById(R.id.switcher_title);
        tv_title.setText(title);
        showNext();
    }

    public abstract interface ItemDataListener {
        public void onItemClick(int position);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return SwitcherTextView.class.getName();
    }

}
