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
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.Window;

import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.request.base.Request;
import com.maning.mndialoglibrary.MProgressDialog;

public abstract class BitmapDialogCallback extends BitmapCallback {

    private MProgressDialog mMProgressDialog;

    public BitmapDialogCallback(Activity activity) {
        super(1000, 1000);
        mMProgressDialog = new MProgressDialog.Builder(activity)
                .setCancelable(true)
                .isCanceledOnTouchOutside(false)
                .setDimAmount(0.5f)
                .build();
    }

    @Override
    public void onStart(Request<Bitmap, ? extends Request> request) {
        mMProgressDialog.show();
    }

    @Override
    public void onFinish() {
        mMProgressDialog.dismiss();
    }
}
