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
package com.ruanmeng.http

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.support.annotation.ArrayRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.widget.ListAdapter

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-18 11:32
 */

fun Context.dialog(
        title: String? = null,
        message: String? = null,
        init: (KAlertDialogBuilder.() -> Unit)? = null) = KAlertDialogBuilder(this).apply {
    if (title != null) title(title)
    if (message != null) message(message)
    if (init != null) init()
}

class KAlertDialogBuilder(val ctx: Context) {

    val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
    var dialog: AlertDialog? = null

    fun dismiss() = dialog?.dismiss()

    fun show(): KAlertDialogBuilder {
        dialog = builder.create()
        dialog!!.show()
        return this
    }

    fun title(title: CharSequence) {
        builder.setTitle(title)
    }

    fun title(@StringRes resource: Int) {
        builder.setTitle(resource)
    }

    fun message(title: CharSequence) {
        builder.setMessage(title)
    }

    fun message(@StringRes resource: Int) {
        builder.setMessage(resource)
    }

    fun icon(@DrawableRes icon: Int) {
        builder.setIcon(icon)
    }

    fun icon(icon: Drawable) {
        builder.setIcon(icon)
    }

    fun customTitle(title: View) {
        builder.setCustomTitle(title)
    }

    fun customView(view: View) {
        builder.setView(view)
    }

    fun cancellable(value: Boolean = true) {
        builder.setCancelable(value)
    }

    fun onCancel(f: () -> Unit) {
        builder.setOnCancelListener { f() }
    }

    fun onKey(f: (keyCode: Int, e: KeyEvent) -> Boolean) {
        builder.setOnKeyListener({ _, keyCode, event -> f(keyCode, event) })
    }

    fun neutralButton(@StringRes textResource: Int = android.R.string.ok, f: DialogInterface.() -> Unit = { dismiss() }) {
        neutralButton(ctx.getString(textResource), f)
    }

    fun neutralButton(title: String, f: DialogInterface.() -> Unit = { dismiss() }) {
        builder.setNeutralButton(title, { dialog, _ -> dialog.f() })
    }

    fun positiveButton(@StringRes textResource: Int = android.R.string.ok, f: DialogInterface.() -> Unit) {
        positiveButton(ctx.getString(textResource), f)
    }

    fun positiveButton(title: String, f: DialogInterface.() -> Unit) {
        builder.setPositiveButton(title, { dialog, _ -> dialog.f() })
    }

    fun negativeButton(@StringRes textResource: Int = android.R.string.cancel, f: DialogInterface.() -> Unit = { dismiss() }) {
        negativeButton(ctx.getString(textResource), f)
    }

    fun negativeButton(title: String, f: DialogInterface.() -> Unit = { dismiss() }) {
        builder.setNegativeButton(title, { dialog, _ -> dialog.f() })
    }

    fun items(@ArrayRes itemsId: Int, f: (which: Int) -> Unit) {
        items(ctx.resources!!.getTextArray(itemsId), f)
    }

    fun items(items: List<CharSequence>, f: (which: Int) -> Unit) {
        items(items.toTypedArray(), f)
    }

    fun items(items: Array<CharSequence>, f: (which: Int) -> Unit) {
        builder.setItems(items, { _, which -> f(which) })
    }

    fun adapter(adapter: ListAdapter, f: (which: Int) -> Unit) {
        builder.setAdapter(adapter, { _, which -> f(which) })
    }

    fun adapter(cursor: Cursor, labelColumn: String, f: (which: Int) -> Unit) {
        builder.setCursor(cursor, { _, which -> f(which) }, labelColumn)
    }
}