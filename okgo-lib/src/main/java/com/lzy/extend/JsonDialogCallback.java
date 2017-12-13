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

import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MToast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 网络数据请求时弹出进度对话框
 */
public abstract class JsonDialogCallback<T> extends JsonCallback<T> {

    private MProgressDialog mMProgressDialog;
    private Activity activity;
    private boolean isVisible = true;

    private void initDialog(Activity activity) {
        this.activity = activity;

        mMProgressDialog = new MProgressDialog.Builder(activity)
                .setCancelable(true)
                .isCanceledOnTouchOutside(false)
                .setDimAmount(0.5f)
                .build();
    }

    public JsonDialogCallback(Activity activity) {
        initDialog(activity);
    }

    public JsonDialogCallback(Activity activity, boolean isVisible) {
        this.isVisible = isVisible;
        initDialog(activity);
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (isVisible) mMProgressDialog.show();
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        mMProgressDialog.dismiss();
    }

    @Override
    public void onError(Response<T> response) {
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
        } else if (exception instanceof JsonSyntaxException) {
            MToast.makeTextShort(activity, "数据格式错误或解析失败！").show();
        } else {
            MToast.makeTextShort(activity, "网络数据请求失败！").show();
        }
    }
}
