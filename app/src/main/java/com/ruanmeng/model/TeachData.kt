package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-06 14:30
 */
data class TeachData(
        var content: String = "",
        var courseCommentId: String = "",
        var createDate: String = "",
        var nickName: String = "",
        var userHead: String = ""
) : Serializable