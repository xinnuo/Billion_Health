package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-08 10:26
 */
data class HospitalModel(
        var hospitalInfos: List<HospitalData>? = ArrayList(),
        var myHospitals: List<HospitalData>? = ArrayList(),
        var hospitalConsultancyScope: List<CommonData>? = ArrayList(),
        var hospitalRecommendDoctors: List<DoctorData>? = ArrayList(),
        var hospitalDetails: HospitalData? = null,
        var collect: String = "",
        var msgcode: String = "",
        var success: String = ""
) : Serializable