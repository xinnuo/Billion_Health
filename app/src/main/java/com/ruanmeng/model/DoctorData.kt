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
 * 创建时间：2017-09-06 15:17
 */
class DoctorData : Serializable {
    //医生列表
    var credentialName: String = ""
    var doctorId: String = ""
    var doctorName: String = ""
    var userHead: String = ""

    //医生详情、更多医生
    var consultCount: String = ""
    var content: String = ""
    var diseaseNames: String = ""
    var doctorAdept: String = ""
    var doctorHead: String = ""
    var hospitalName: String = ""
    var serviceYears: String = ""

    //医生风采
    var createDate: String = ""
    var sysDate: String = ""
    var attachMentId: String = ""
    var businessKey: String = ""
    var pojoName: String = ""
    var realPath: String = ""

    //我的医生
    var credentialId: String = ""
    var doctorhead: String = ""
    var focueId: String = ""
    var fromUserId: String = ""
    var toUserId: String = ""
    var userInfoId: String = ""
}
