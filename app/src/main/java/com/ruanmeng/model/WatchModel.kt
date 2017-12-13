package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
data class WatchModel(
        var focue: List<CommonData> ?= ArrayList(),
        var msgcode: String = "",
        var success: String = ""
): Serializable