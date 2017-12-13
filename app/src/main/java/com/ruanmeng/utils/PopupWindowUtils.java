package com.ruanmeng.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruanmeng.billion_health.R;
import com.ruanmeng.model.CommonData;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-13 14:42
 */

public class PopupWindowUtils {

    public static void showDoctorFilterPopWindow(
            final Context context,
            View anchor,
            final List<CommonData> list_zi,
            final PopupWindowFilterCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_filter_doctor, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        TextView tv_done = view.findViewById(R.id.pop_filter_done);
        TextView tv_reset = view.findViewById(R.id.pop_filter_reset);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        final TagFlowLayout tap_type = view.findViewById(R.id.filter_type);
        final TagFlowLayout tap_gender = view.findViewById(R.id.filter_gender);
        final TagFlowLayout tap_zhi = view.findViewById(R.id.filter_zhi);

        final TagAdapter<String> adapter_type;
        final TagAdapter<String> adapter_gender;
        final TagAdapter<CommonData> adapter_zi;

        tap_zhi.setAdapter(adapter_zi = new TagAdapter<CommonData>(list_zi) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData commonData) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_flow, tap_zhi, false);
                tv_name.setText(commonData.getCredentialName());
                return tv_name;
            }
        });

        tap_type.setAdapter(adapter_type = new TagAdapter<String>(new String[]{"私人医生", "医院医生"}) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_flow, tap_type, false);
                tv_name.setText(s);
                return tv_name;
            }
        });

        tap_gender.setAdapter(adapter_gender = new TagAdapter<String>(new String[]{"女", "男"}) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_flow, tap_gender, false);
                tv_name.setText(s);
                return tv_name;
            }
        });

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_type.notifyDataChanged();
                adapter_gender.notifyDataChanged();
                adapter_zi.notifyDataChanged();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                StringBuilder type = new StringBuilder();
                StringBuilder zhi = new StringBuilder();
                StringBuilder gender = new StringBuilder();

                for (Integer item: tap_type.getSelectedList()) { type.append(String.valueOf(item)).append(","); }
                for (Integer item: tap_gender.getSelectedList()) { gender.append(String.valueOf(item)).append(","); }
                for (Integer item: tap_zhi.getSelectedList()) { zhi.append(list_zi.get(item).getCredentialId()).append(","); }

                callBack.doWork(type.toString(), zhi.toString(), gender.toString());
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showHospitalFilterPopWindow(
            final Context context,
            View anchor,
            final List<CommonData> list_level,
            final List<CommonData> list_type,
            final PopupWindowFilterCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_filter_hospital, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        TextView tv_done = view.findViewById(R.id.pop_filter_done);
        TextView tv_reset = view.findViewById(R.id.pop_filter_reset);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        final TagFlowLayout tap_level = view.findViewById(R.id.filter_level);
        final TagFlowLayout tap_type = view.findViewById(R.id.filter_type);

        final TagAdapter<CommonData> adapter_level;
        final TagAdapter<CommonData> adapter_type;

        tap_level.setAdapter(adapter_level = new TagAdapter<CommonData>(list_level) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData commonData) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_flow, tap_level, false);
                tv_name.setText(commonData.getHospitalLevelName());
                return tv_name;
            }
        });

        tap_type.setAdapter(adapter_type = new TagAdapter<CommonData>(list_type) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData commonData) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_flow, tap_type, false);
                tv_name.setText(commonData.getHospitalTypeName());
                return tv_name;
            }
        });

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_level.notifyDataChanged();
                adapter_type.notifyDataChanged();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                StringBuilder level = new StringBuilder();
                StringBuilder type = new StringBuilder();

                for (Integer item: tap_level.getSelectedList()) { level.append(list_level.get(item).getHospitalLevelId()).append(","); }
                for (Integer item: tap_type.getSelectedList()) { type.append(list_type.get(item).getHospitalTypeId()).append(","); }

                callBack.doWork(level.toString(), type.toString(), "");
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showOrderPopWindow(
            final Context context,
            View anchor,
            int selected,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_order, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_pop_order);
        final RadioButton rb_check1 = (RadioButton) view.findViewById(R.id.rb_pop_order_check_1);
        final RadioButton rb_check2 = (RadioButton) view.findViewById(R.id.rb_pop_order_check_2);
        final RadioButton rb_check3 = (RadioButton) view.findViewById(R.id.rb_pop_order_check_3);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        switch (selected) {
            case 1:
                rb_check1.setChecked(true);
                break;
            case 2:
                rb_check2.setChecked(true);
                break;
            case 3:
                rb_check3.setChecked(true);
                break;
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                switch (checkedId) {
                    case R.id.rb_pop_order_check_1:
                        callBack.doWork(1, "综合");
                        break;
                    case R.id.rb_pop_order_check_2:
                        callBack.doWork(2, "咨询");
                        break;
                    case R.id.rb_pop_order_check_3:
                        callBack.doWork(3, "级别");
                        break;
                }
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showDatePopWindow(
            final Context context,
            View anchor,
            int selected,
            final List<CommonData> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_discrit, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (CommonData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getAreaName());
            rb.setId(items.indexOf(item));
            if (items.indexOf(item) == selected) rb.setChecked(true);
            rg.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            param.setMargins(DensityUtil.dp2px(10f), 0, DensityUtil.dp2px(10f), 0);
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId).getAreaName());
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showSickPopWindow(
            final Context context,
            View anchor,
            final String name_first,
            final String name_second,
            final List<CommonData> items,
            final PopupWindowSickCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_sick, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        final List<CommonData> item_second = new ArrayList<>();
        final NestedScrollView sv_right = (NestedScrollView) view.findViewById(R.id.sv_pop_near_right);
        final RadioGroup rg_left = (RadioGroup) view.findViewById(R.id.rg_pop_near_left);
        final RadioGroup rg_right = (RadioGroup) view.findViewById(R.id.rg_pop_near_right);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<CommonData> mlist = (List<CommonData>) msg.obj;

                item_second.clear();
                if (mlist != null) item_second.addAll(mlist);

                if (item_second.size() > 0) {
                    sv_right.setVisibility(View.VISIBLE);

                    for (CommonData item : item_second) {
                        RadioButton rb = new RadioButton(context);
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                        rb.setLayoutParams(params);
                        rb.setTextAppearance(context, R.style.Font14_selector);
                        rb.setGravity(Gravity.CENTER);
                        rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                        rb.setText(item.getDiseaseName());
                        rb.setId(item_second.indexOf(item));
                        rg_right.addView(rb);

                        RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                        View v = new View(context);
                        v.setLayoutParams(param);
                        v.setBackgroundResource(R.color.colorControlNormal);
                        rg_right.addView(v);
                    }
                } else sv_right.setVisibility(View.GONE);
            }
        };

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        rg_right.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();

                callBack.doWork(
                        item_second.get(checkedId).getDiseaseId(),
                        item_second.get(checkedId).getDiseaseName(),
                        items.get(rg_left.getCheckedRadioButtonId()).getDiseaseName());
            }
        });

        rg_left.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg_right.removeAllViews();
                callBack.getSecondList(items.get(checkedId).getDiseaseId(), handler);
            }
        });

        for (CommonData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getDiseaseName());
            rb.setId(items.indexOf(item));
            if (name_first != null && item.getDiseaseName().contains(name_first))
                rb.setChecked(true);
            rg_left.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg_left.addView(v);
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public interface PopupWindowSickCallBack {
        void getSecondList(String diseaseId, Handler handler);

        void doWork(String diseaseId, String name, String name_first);

        void onDismiss();
    }

    public interface PopupWindowFilterCallBack {
        void doWork(String type, String zi, String gender);

        void onDismiss();
    }

    public interface PopupWindowCallBack {
        void doWork(int position, String name);

        void onDismiss();
    }
}
