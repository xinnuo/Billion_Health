/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.extend;

import android.app.Activity;
import android.text.TextUtils;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.lzy.okgo.utils.OkLogger;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public abstract class StringDialogCallback extends StringCallback {

    private MProgressDialog mMProgressDialog;
    private Activity activity;
    private boolean isVisible = true;

    public StringDialogCallback(Activity activity) {
        this(activity, true);
    }

    public StringDialogCallback(Activity activity, boolean isVisible) {
        this.activity = activity;
        this.isVisible = isVisible;

        mMProgressDialog = new MProgressDialog.Builder(activity)
                .setCancelable(true)
                .isCanceledOnTouchOutside(false)
                .setDimAmount(0.5f)
                .build();
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (isVisible) mMProgressDialog.show();
    }

    @Override
    public void onSuccess(Response<String> response) {
        OkLogger.i(response.body());

        try {
            JSONObject obj = new JSONObject(response.body());

            String msgCode = obj.getString("msgcode");
            String msg = obj.isNull("msg") ? "请求成功！" : obj.getString("msg");

            if (!TextUtils.equals("100", msgCode)) {
                MToast.makeTextShort(activity, msg).show();

                onSuccessResponseErrorCode(response, msg, msgCode);
            } else {
                onSuccessResponse(response, msg, msgCode);
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }
    }

    public abstract void onSuccessResponse(Response<String> response, String msg, String msgCode);

    public void onSuccessResponseErrorCode(Response<String> response, String msg, String msgCode) { }

    @Override
    public void onFinish() {
        mMProgressDialog.dismiss();
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        Throwable exception = response.getException();
        if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
            MToast.makeTextShort(activity, "网络连接失败，请连接网络！").show();
        } else if (exception instanceof SocketTimeoutException) {
            MToast.makeTextShort(activity, "网络请求超时！").show();
        } else if (exception instanceof HttpException) {
            MToast.makeTextShort(activity, "服务器发生未知错误！").show();
        } else if (exception instanceof StorageException) {
            MToast.makeTextShort(activity, "SD卡不存在或没有权限！").show();
        } else {
            MToast.makeTextShort(activity, "网络数据请求失败！").show();
        }
    }
}
