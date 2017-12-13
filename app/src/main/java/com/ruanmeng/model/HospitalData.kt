/**
 * created by 小卷毛, 2017/09/06
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
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-06 15:19
 */
data class HospitalData(
        var content: String = "",
        var hospitalAddress: String = "",
        var hospitalHead: String = "",
        var hospitalId: String = "",
        var hospitalImgs: String = "",
        var hospitalName: String = "",
        var hospitalSynopsis: String = "",

        var consultCount: String = "",
        var distance: String = "",
        var hospitalLevelId: String = "",
        var hospitallevelName: String = "",
        var hospitaltypeId: String = "",
        var hospitaltypeName: String = "",

        var cityName: String = "",
        var districtName: String = "",
        var lat: String = "",
        var lng: String = "",
        var mobile: String = "",
        var provinceName: String = "",

        //我的医院
        var collectId: String = ""
) : Serializable