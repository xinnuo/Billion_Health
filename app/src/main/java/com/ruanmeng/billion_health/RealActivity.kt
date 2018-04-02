package com.ruanmeng.billion_health

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_real.*
import java.io.File
import java.util.ArrayList

class RealActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    private var gender = ""

    private var image_type = 1
    private var image_first = ""
    private var image_second = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real)
        init_title("实名认证")
    }

    override fun init_title() {
        super.init_title()
        bt_submit.apply {
            setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            isClickable = false
        }

        et_name.addTextChangedListener(this@RealActivity)
        et_card.addTextChangedListener(this@RealActivity)

        rg_check.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_check1 -> gender = "0"
                R.id.rb_check2 -> gender = "1"
            }
        }

        rb_check1.performClick()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.real_img1 -> {
                image_type = 1

                if (real_del1.visibility == View.VISIBLE) return

                PictureSelector.create(this@RealActivity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .previewImage(true)
                        .previewVideo(false)
                        .compressGrade(Luban.THIRD_GEAR)
                        .isCamera(true)
                        .isZoomAnim(true)
                        .setOutputCameraPath(Const.SAVE_FILE)
                        .compress(true)
                        .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                        .glideOverride(160, 160)
                        .enableCrop(true)
                        .withAspectRatio(4, 3)
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
            R.id.real_del1 -> {
                real_del1.visibility = View.INVISIBLE
                real_img1.setImageResource(R.mipmap.my_smrz)
                image_first = ""
            }
            R.id.real_img2 -> {
                image_type = 2

                if (real_del2.visibility == View.VISIBLE) return

                PictureSelector.create(this@RealActivity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .previewImage(true)
                        .previewVideo(false)
                        .compressGrade(Luban.THIRD_GEAR)
                        .isCamera(true)
                        .isZoomAnim(true)
                        .setOutputCameraPath(Const.SAVE_FILE)
                        .compress(true)
                        .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                        .glideOverride(160, 160)
                        .enableCrop(true)
                        .withAspectRatio(4, 3)
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
            R.id.real_del2 -> {
                real_del2.visibility = View.INVISIBLE
                real_img2.setImageResource(R.mipmap.my_smrz)
                image_second = ""
            }
            R.id.bt_submit -> {
                if (image_first == "" || image_second == "") {
                    showToask("请上传身份证照！")
                    return
                }

                if (!CommonUtil.IDCardValidate(et_card.text.toString())) {
                    showToask("请输入正确的身份证号！")
                    return
                }

                OkGo.post<String>(HttpIP.certification_sub)
                        .tag(this@RealActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("userName", et_name.text.toString())
                        .params("cardNo", et_card.text.toString())
                        .params("sex", gender)
                        .params("img", File(image_first))
                        .params("img2", File(image_second))
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "提交成功，请等待审核",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                showToask(msg)

                                ActivityStack.getScreenManager().popActivities(this@RealActivity::class.java)
                            }

                        })
            }
        }
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

                    when (image_type) {
                        1 -> {
                            image_first = selectList[0].compressPath
                            real_del1.visibility = View.VISIBLE

                            Glide.with(baseContext)
                                    .load(image_first)
                                    .apply(RequestOptions()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(real_img1)
                        }
                        2 -> {
                            image_second = selectList[0].compressPath
                            real_del2.visibility = View.VISIBLE

                            Glide.with(baseContext)
                                    .load(image_second)
                                    .apply(RequestOptions()
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(real_img2)
                        }
                    }
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.toString() != ""
                && et_card.text.toString() != "") {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_green)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_d5d5d5)
            bt_submit.isClickable = false
        }
    }
}
