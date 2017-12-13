/**
 * created by 小卷毛, 2017/09/05
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
 * 创建时间：2017-09-05 18:13
 */
data class CommonData(
        var slider: List<SliderData> = ArrayList(),

        //公告
        var newsId: String = "",

        //朋友圈
        var dynamicVideoImgPath: String = "",
        var dynamicVideoPath: String = "",
        var age: String = "0",                 // 11
        var browseNum: String = "0",           // 144
        var commentCount: String = "",         // 8
        var content: String = "",              // 说点什么吧~
        var createDate: String = "",           // 2017-09-05 10:16:10
        var dynamicHead: String = "",          // upload/dynamic/8F176A2E3ABD49B1A101AC7344ABC6CA/D7119CDDD44C4D1CB263FC8076F1DDEF.png
        var dynamicId: String = "",            // 8F176A2E3ABD49B1A101AC7344ABC6CA
        var imgs: String = "",                 // upload/dynamic/8F176A2E3ABD49B1A101AC7344ABC6CA/D7119CDDD44C4D1CB263FC8076F1DDEF.png,upload/dynamic/8F176A2E3ABD49B1A101AC7344ABC6CA/39A0460EA9AE49C38B724042160E3BBB.png,upload/dynamic/8F176A2E3ABD49B1A101AC7344ABC6CA/85074BA8E6354BD8B654CB28133D23BD.png
        var isFocue: String = "",              // 0
        var nickName: String = "",             // 18625879630
        var praise: String = "",               // 0
        var praiseCount: String = "0",         // 1
        var sex: String = "",                  //
        var userHead: String = "",             // upload/userhead/0A9401AF2E8D49FB9C631C47FC277163/B62595966230494389050B1A54611D69.png
        var userInfoId: String = "",           // 0A9401AF2E8D49FB9C631C47FC277163

        //朋友圈详情
        var authenticate: String = "",
        var city: String = "",
        var dynamicVoicePath: String = "",
        var sign: String = "",

        //朋友圈主页
        var district: String = "",
        var isPass: String = "",
        var mobile: String = "",
        var province: String = "",
        var img: String = "",
        var userhead: String = "",

        //视频宽高
        var width: String = "",
        var height: String = "",

        //课程详情
        var courseId: String = "",
        var imgHead: String = "",

        //我的关注
        var focueId: String = "",
        var focusType: String = "",
        var isMutual: String = "",

        //我的课程
        var collectId: String = "",
        var readCount: String = "",
        var title: String = "",

        //获取区域
        var areaCode: String = "",
        var areaId: String = "",
        var areaName: String = "",

        //病症一级分类
        var diseaseId: String = "",
        var diseaseName: String = "",
        var isChecked: Boolean = false,
        var indexs: String = "",

        //医生资质
        var credentialId: String = "",
        var credentialName: String = "",

        //医院等级和类型
        var sysDate: String = "",
        var hospitalLevelId: String = "",
        var hospitalLevelName: String = "",
        var hospitalLevelCode: String = "",
        var remark: String = "",

        //医院等级和类型
        var hospitalTypeId: String = "",
        var hospitalTypeName: String = "",
        var hospitalTypeCode: String = "",
        var hospitalTypeRemark: String = "",

        //心理测试
        var psychologyHead: String = "",
        var psychologyId: String = "",
        var psychologyTitle: String = "",
        var status: String = "",

        //帮助列表
        var dataItemDetailsId: String = "",
        var dataItemDetailsVal: String = "",
        var htmlId: String = "",
        var htmlKey: String = ""
): Serializable