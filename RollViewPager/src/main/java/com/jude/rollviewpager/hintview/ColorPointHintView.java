package com.jude.rollviewpager.hintview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.jude.rollviewpager.Util;

public class ColorPointHintView extends ShapeHintView {
    private int focusColor;
    private int normalColor;

    public ColorPointHintView(Context context,int focusColor,int normalColor) {
        super(context);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
    }

    @Override
    public Drawable makeFocusDrawable() {
        GradientDrawable dot_focus = new GradientDrawable();
        dot_focus.setColor(focusColor);
        dot_focus.setCornerRadius(Util.dip2px(getContext(), 4));
        dot_focus.setSize(Util.dip2px(getContext(), 6), Util.dip2px(getContext(), 6));
        return dot_focus;
    }

    @Override
    public Drawable makeNormalDrawable() {
        GradientDrawable dot_normal = new GradientDrawable();
        dot_normal.setColor(normalColor);
        dot_normal.setCornerRadius(Util.dip2px(getContext(), 4));
        dot_normal.setSize(Util.dip2px(getContext(), 6), Util.dip2px(getContext(), 6));
        return dot_normal;
    }
}
