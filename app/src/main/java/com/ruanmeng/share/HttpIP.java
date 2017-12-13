/**
 * created by 小卷毛, 2016/11/22
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */
package com.ruanmeng.share;

import com.ruanmeng.billion_health.BuildConfig;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-02-16 18:45
 */
public class HttpIP {

    public static String BaseUrl = BuildConfig.API_HOST;
    public static String BaseImg = BaseUrl + "/";
    public static String IP = BaseUrl + "/api";

    public static String login_sub = IP + "/login_sub.rm";                       //登录
    public static String identify_get = IP + "/identify_get.rm";                 //注册获取验证码
    public static String register_sub = IP + "/register_sub.rm";                 //注册提交
    public static String identify_getbyforget = IP + "/identify_getbyforget.rm"; //忘记密码获取验证码
    public static String pwd_forget_sub = IP + "/pwd_forget_sub.rm";             //忘记密码找回提交
    public static String logout_sub = IP + "/logout_sub.rm";                     //退出登录
    public static String gvrp = IP + "/gvrp.rm";                                 //注册协议

    public static String index_data = IP + "/index_data.rm";           //首页
    public static String indexsearch_sub = IP + "/indexsearch_sub.rm"; //搜索
    public static String zszx = IP + "/zszx.rm";                       //招商中心
    public static String getnews_data = IP + "/getnews_data.rm";       //公告详情

    public static String index_firstdisease_data = IP + "/index_firstdisease_data.rm";     //疾病一级分类项目列表
    public static String second_disease_data = IP + "/second_disease_data.rm";             //疾病二级分类项目列表
    public static String about_doctor_data = IP + "/about_doctor_data.rm";                 //疾病医生列表
    public static String get_doctor_data = IP + "/get_doctor_data.rm";                     //医生详情资料
    public static String get_doctorMsg_data = IP + "/get_doctorMsg_data.rm";               //医生通知消息
    public static String get_doctorcredential_data = IP + "/get_doctorcredential_data.rm"; //医生资质
    public static String doctor_consult = IP + "/doctor_consult.rm";                       //医生咨询

    public static String index_famousdoctors_data = IP + "/index_famousdoctors_data.rm"; //名医推荐

    public static String index_course_data = IP + "/index_course_data.rm";     //健康讲堂首页数据
    public static String course_slider = IP + "/course_slider.rm";             //课程讲堂搜索页面轮播图
    public static String search_course_data = IP + "/search_course_data.rm";   //课程讲堂搜索页面
    public static String course_details_data = IP + "/course_details_data.rm"; //课程详情
    public static String course_comment_sub = IP + "/course_comment_sub.rm";   //课程发表评论
    public static String praise_sub = IP + "/praise_sub.rm";                   //课程点赞

    public static String orther_dynamic_data = IP + "/orther_dynamic_data.rm"; //他人主页动态详情
    public static String user_homepage_msg = IP + "/user_homepage_msg.rm";     //他人主页详情
    public static String friendCircle_data = IP + "/friendCircle_data.rm";     //朋友圈 广场/好友/我的
    // public static String dynamic_sub = IP + "/dynamic_sub.rm";              //发布动态图片
    public static String dynamic_sub = IP + "/dynamic_sub.rm";                 //发布动态视频
    public static String dynamic_praise_sub = IP + "/dynamic_praise_sub.rm";   //朋友圈点赞
    public static String get_dynamic = IP + "/get_dynamic.rm";                 //动态详情+评论
    public static String dynamiccomment_sub = IP + "/dynamiccomment_sub.rm";   //动态评论
    public static String dynamic_del_sub = IP + "/dynamic_del_sub.rm";         //动态删除

    public static String index_enterHospital_data = IP + "/index_enterHospital_data.rm";             //首页入驻医院展示
    public static String hospital_data = IP + "/hospital_data.rm";                                   //医院搜索
    public static String hospital_details = IP + "/hospital_details.rm";                             //医院详情
    public static String hospital_consultancyScope_data = IP + "/hospital_consultancyScope_data.rm"; //医院详情—咨询范围
    public static String hospital_recommendDoctor_data = IP + "/hospital_recommendDoctor_data.rm";   //医院详情—医生介绍
    public static String reserve_sub = IP + "/reserve_sub.rm";                                       //预约提交
    public static String reserve_details = IP + "/reserve_details.rm";                               //查看预约
    public static String find_hospitalInfo = IP + "/find_hospitalInfo.rm";                           //找医院
    public static String find_hospitalotherInfo = IP + "/find_hospitalotherInfo.rm";                 //医院等级和类型

    public static String focue_sub = IP + "/focue_sub.rm";                     //添加关注
    public static String cancel_focue_sub = IP + "/cancel_focue_sub.rm";       //取消关注
    public static String collect_sub = IP + "/collect_sub.rm";                 //加入收藏
    public static String cancel_collect_sub = IP + "/cancel_collect_sub.rm";   //取消收藏
    public static String me_focue_list = IP + "/me_focue_list.rm";             //我关注的
    public static String focue_me_list = IP + "/focue_me_list.rm";             //关注我的
    public static String dynamic_data = IP + "/dynamic_data.rm";               //我的动态
    public static String my_course_data = IP + "/my_course_data.rm";           //我的课程
    public static String my_hospital_data = IP + "/my_hospital_data.rm";       //我的医院
    public static String my_doctor_data = IP + "/my_doctor_data.rm";           //我的医生
    public static String psychology_data = IP + "/psychology_data.rm";         //心理测试
    public static String psychology_details = IP + "/psychology_details.rm";   //心理测试详情
    public static String certification_sub = IP + "/certification_sub.rm";     //实名认证
    public static String htmlPort = IP + "/htmlPort.rm";                       //静态页面信息公共接口
    public static String consult_sub = IP + "/consult_sub.rm";                 //意见反馈

    public static String userinfo_uploadhead_sub = IP + "/userinfo_uploadhead_sub.rm";  //修改头像
    public static String nickName_change_sub = IP + "/nickName_change_sub.rm";          //修改昵称
    public static String sex_change_sub = IP + "/sex_change_sub.rm";                    //修改性别
    public static String age_change_sub = IP + "/age_change_sub.rm";                    //修改年龄
    public static String sign_change_sub = IP + "/sign_change_sub.rm";                  //修改签名
    public static String password_change_sub = IP + "/password_change_sub.rm";          //修改密码
    public static String user_msg_data = IP + "/user_msg_data.rm";                      //用户个人资料

    public static String city1_data = IP + "/city1_data.rm";              //获取所有省份
    public static String city2_data = IP + "/city2_data.rm";              //获取某省份所有市
    public static String area_data = IP + "/area_data.rm";                //获取区域
    public static String city_data = IP + "/city_data.rm";                //获取所有城市
    public static String area_code = IP + "/area_code.rm";                //获取区域代码
    public static String allarea_code = IP + "/allarea_code.rm";          //获取位置城市区编码
    public static String address_code = IP + "/address_code.rm";          //根据经纬度获取省市区编码
    public static String about_us = IP + "/about_us.rm";                  //关于我们
    public static String help_list = IP + "/help_list.rm";                //帮助中心列表
    public static String help_center = IP + "/help_center.rm";            //帮助中心明细
    public static String get_version = IP + "/get_version.rm";            //获取最新版本
    public static String diseasetype_data = IP + "/diseasetype_data.rm";  //获取大分类
    public static String diseasetype_all = IP + "/diseasetype_all.rm";    //根据大分类获取子分类

    public static String focuetype_sub = IP + "/focuetype_sub.rm";     //关注类型
    public static String userlist_data = IP + "/userlist_data.rm";     //获取用户资料
    public static String frienddata_list = IP + "/frienddata_list.rm"; //朋友列表
}
