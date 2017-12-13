package com.ruanmeng.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-07 9:40
 */

public class NameLengthFilter implements InputFilter {

    private int MAX_EN;                          // 最大英文/数字长度 一个汉字算两个字母
    private String regEx = "[\\u4e00-\\u9fa5]";  // unicode编码，判断是否为汉字

    public NameLengthFilter(int mAX_EN) {
        super();
        MAX_EN = mAX_EN;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        int destCount = dest.toString().length()
                + getChineseCount(dest.toString());
        int sourceCount = source.toString().length()
                + getChineseCount(source.toString());

        if (destCount + sourceCount > MAX_EN) return "";
        else return source;
    }

    private int getChineseCount(String str) {
        int count = 0;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }

}
