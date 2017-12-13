package com.ruanmeng.utils;

import android.app.Activity;

import java.util.Stack;

public class ActivityStack {

    /**
     * 注意：mActivityStack 中包含已经 finished 的 activity
     */
    private static Stack<Activity> mActivityStack;
    private static ActivityStack instance;

    private ActivityStack() {
    }

    public static ActivityStack getScreenManager() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }

    /**
     * 移除栈顶的activity
     */
    public void popActivity() {
        Activity activity = mActivityStack.lastElement();
        if (activity != null) activity.finish();
    }

    /**
     * 移除一个activity
     */
    private void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            mActivityStack.remove(activity);
        }
    }

    /**
     * 获取栈顶的activity，先进后出原则
     */
    public Activity currentActivity() {
        // lastElement()获取最后个子元素，这里是栈顶的Activity
        if (mActivityStack == null || mActivityStack.size() == 0) {
            return null;
        }
        return mActivityStack.lastElement();
    }

    /**
     * 将当前Activity推入栈中
     */
    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 是否包含指定的Activity
     */
    public boolean isContainsActivity(Class<?> cls) {
        if (mActivityStack == null || mActivityStack.size() == 0) {
            return false;
        }
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls) && !activity.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 弹出栈中指定Activity
     */
    public boolean popOneActivity(Class<?> cls) {
        if (mActivityStack == null || mActivityStack.size() == 0) return false;
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                if (!activity.isDestroyed()) {
                    popActivity(activity);
                    return true;
                } else popActivity();
            }
        }
        return false;
    }

    /**
     * 弹出栈中所有Activity，保留指定的一个Activity
     */
    public void popAllActivityExceptOne(Class<?> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) break;
            if (activity.getClass().equals(cls)) break;
            popActivity(activity);
        }
    }

    /**
     * 移除指定的多个activity
     */
    public void popActivities(Class<?>... clss) {
        for (Class<?> cls : clss) {
            if (isContainsActivity(cls))
                popOneActivity(cls);
        }
    }

    /**
     * 弹出栈中所有Activity，保留指定的Activity
     */
    public void popAllActivityExcept(Class<?>... clss) {
        for (int i = mActivityStack.size() - 1; i >= 0; i--) {
            Activity activity = mActivityStack.get(i);
            boolean isNotFinish = false;
            for (Class<?> cls : clss) {
                if (activity.getClass().equals(cls)) isNotFinish = true;
            }
            if (!isNotFinish) popActivity(activity);
        }
    }

    /**
     * 弹出栈中所有Activity
     */
    public void popAllActivitys() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) break;
            popActivity(activity);
        }
    }

}
