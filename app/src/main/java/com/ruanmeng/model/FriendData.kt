package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-05 18:13
 */
data class FriendData(
        var head: String = "",
        var id: String = "",
        var name: String = "",
        var type: String = "",
        var userId: String = "",
        var letter: String = ""
): Serializable