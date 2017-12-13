package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-05 18:13
 */
data class CityData(
        var areaCode: String = "",
        var areaId: String = "",
        var areaName: String = "",
        var firstLetter: String = "",
        var lat: String = "",
        var lng: String = ""
): Serializable