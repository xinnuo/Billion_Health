package com.ruanmeng.billion_health

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.GlideApp
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NumberHelper
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_info.*
import org.json.JSONObject
import java.io.File
import java.util.*

class InfoActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        init_title("个人资料")

        loadUserHead(getString("userhead"))
        info_tel.setRightString(getString("mobile", ""))
        info_gender.setRightString(if (getString("sex") == "0") "女" else "男")
        info_age.setRightString((getString("age") ?: "0") + "岁")
    }

    override fun onStart() {
        super.onStart()

        info_name.setRightString(getString("nickName"))
    }

    override fun init_title() {
        super.init_title()
        info_img_ll.setOnClickListener(this@InfoActivity)
        info_name.setOnClickListener(this@InfoActivity)
        info_gender.setOnClickListener(this@InfoActivity)
        info_age.setOnClickListener(this@InfoActivity)
        info_sign.setOnClickListener(this@InfoActivity)
        info_pwd.setOnClickListener(this@InfoActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList[0].isCompressed) getData(selectList[0].compressPath)
                }
            }
        }
    }

    private fun loadUserHead(path: String) {
        GlideApp.with(this@InfoActivity)
                .load(HttpIP.BaseImg + path)
                .placeholder(R.mipmap.my_tx_mr) // 等待时的图片
                .error(R.mipmap.my_tx_mr)       // 加载失败的图片
                .dontAnimate()
                .into(info_img)
    }

    private fun getData(path: String) {
        OkGo.post<String>(HttpIP.userinfo_uploadhead_sub)
                .tag(this@InfoActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("img", File(path))
                .execute(object : StringDialogCallback(this@InfoActivity) {
                    /*{
                        "msg": "头像上传成功",
                        "msgcode": 100,
                        "object": "upload/userhead/31743A18B53842298BC9DDF861651658/F6FD163A606A46E8AC27C7AF9E866F23.jpg"
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        showToask(msg)
                        val userhead = JSONObject(response.body()).getString("object")
                        putString("userhead", userhead)

                        RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                getString("token"),
                                getString("nickName"),
                                Uri.parse(HttpIP.BaseImg + getString("userhead"))))

                        loadUserHead(userhead)
                    }

                })
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.info_img_ll -> {
                DialogHelper.showCameraDialog(this@InfoActivity, { name ->
                    when(name) {
                        "相册" -> {
                            PictureSelector.create(this@InfoActivity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(4)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .previewImage(true)
                                    .previewVideo(false)
                                    .compressGrade(Luban.THIRD_GEAR)
                                    .isCamera(false)
                                    .isZoomAnim(true)
                                    .setOutputCameraPath(Const.SAVE_FILE)
                                    .compress(true)
                                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                                    .glideOverride(160, 160)
                                    .enableCrop(true)
                                    .withAspectRatio(1, 1)
                                    .hideBottomControls(true)
                                    .freeStyleCropEnabled(false)
                                    .circleDimmedLayer(false)
                                    .showCropFrame(true)
                                    .showCropGrid(true)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList.apply { clear() })
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                        "拍照" -> {
                            PictureSelector.create(this@InfoActivity)
                                    .openCamera(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .previewImage(true)
                                    .previewVideo(false)
                                    .compressGrade(Luban.THIRD_GEAR)
                                    .isCamera(false)
                                    .isZoomAnim(true)
                                    .setOutputCameraPath(Const.SAVE_FILE)
                                    .compress(true)
                                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                                    .glideOverride(160, 160)
                                    .enableCrop(true)
                                    .withAspectRatio(1, 1)
                                    .hideBottomControls(true)
                                    .freeStyleCropEnabled(false)
                                    .circleDimmedLayer(false)
                                    .showCropFrame(true)
                                    .showCropGrid(true)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList.apply { clear() })
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                    }
                })
            }
            R.id.info_name -> { startActivity(NicknameActivity::class.java) }
            R.id.info_gender -> {
                DialogHelper.showGenderDialog(baseContext, { name ->

                    OkGo.post<String>(HttpIP.sex_change_sub)
                            .tag(this@InfoActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("sex", name)
                            .execute(object : StringDialogCallback(this@InfoActivity) {
                                /*{
                                    "msg": "更新成功",
                                    "msgcode": 100
                                }*/
                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    showToask(msg)
                                    putString("sex", name)

                                    info_gender.setRightString(if (name == "0") "女" else "男")
                                }

                            })
                })
            }
            R.id.info_age -> {
                DialogHelper.showDateDialog(this@InfoActivity,
                        1950,
                        Calendar.getInstance().get(Calendar.YEAR),
                        3,
                        "选择出生日期",
                        true,
                        true,
                        { _, _, _, _, _, date ->
                            val age = NumberHelper.getAgeByBirthday(date)

                            OkGo.post<String>(HttpIP.age_change_sub)
                                    .tag(this@InfoActivity)
                                    .isMultipart(true)
                                    .headers("token", getString("token"))
                                    .params("age", age)
                                    .execute(object : StringDialogCallback(this@InfoActivity) {
                                        /*{
                                            "msg": "修改成功",
                                            "msgcode": 100
                                        }*/
                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                            showToask(msg)
                                            putString("age", age.toString())

                                            info_age.setRightString(age.toString() + "岁")
                                        }

                                    })
                        })
            }
            R.id.info_sign -> { startActivity(SignActivity::class.java) }
            R.id.info_pwd -> { startActivity(PasswordActivity::class.java) }
        }
    }
}
