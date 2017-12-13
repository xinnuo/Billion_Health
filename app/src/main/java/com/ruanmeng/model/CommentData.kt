package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-06 14:30
 */
data class CommentData(
        var commentContent: String = "",
        var createDate: String = "",
        var dynamicCommentId: String = "",
        var nikeName: String = "",
        var replyNikeName: String = "",
        var replyUserHead: String = "",
        var replyUserId: String = "",
        var userHead: String = "",
        var userInfoId: String = ""
) : Serializable