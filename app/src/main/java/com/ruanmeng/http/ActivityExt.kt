/**
 * created by 小卷毛, 2017/09/18
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
@file:Suppress("NOTHING_TO_INLINE")

package com.ruanmeng.http

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import com.maning.mndialoglibrary.MToast
import com.ruanmeng.utils.DialogHelper

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-18 11:39
 */

inline fun <reified T : View> Activity.find(@IdRes id: Int): T = findViewById<T>(id) as T

inline fun Activity.toast(text: CharSequence) = MToast.makeTextShort(this, text).show()

inline fun Activity.showLoadingDialog() = DialogHelper.showDialog(this)

inline fun Activity.cancelLoadingDialog() = DialogHelper.dismissDialog()

fun <T> ArrayList<T>.addItems(items: List<T>? = null): ArrayList<T> {
    if (items != null && items.isNotEmpty()) addAll(items)
    return this
}