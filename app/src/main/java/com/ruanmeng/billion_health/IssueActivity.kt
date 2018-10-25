package com.ruanmeng.billion_health

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.compress.OnCompressListener
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.PictureSelectionConfig
import com.luck.picture.lib.dialog.PictureDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.GridImageAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.MainSecondEvent
import com.ruanmeng.model.StateMessageEvent
import com.ruanmeng.share.Const
import com.ruanmeng.share.HttpIP
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.BitmapHelper
import com.ruanmeng.utils.BitmapHelper.getVideoThumbnail
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_issue.*
import kotlinx.android.synthetic.main.layout_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class IssueActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var mType: String = ""

    private var compressDialog: PictureDialog? = null
    private val imageAdapter by lazy {
        GridImageAdapter(this@IssueActivity).apply {
            setList(selectList)
            setSelectMax(9)
            setOnItemClickListener { position, _ ->
                if (selectList.isNotEmpty()) {
                    val media = selectList[position]
                    val pictureType = media.pictureType
                    val mediaType = PictureMimeType.pictureToVideo(pictureType)
                    when (mediaType) {
                        1 -> PictureSelector.create(this@IssueActivity).externalPicturePreview(position, selectList)
                        2 -> PictureSelector.create(this@IssueActivity).externalPictureVideo(media.path)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)
        init_title("发布动态")
    }

    override fun init_title() {
        super.init_title()
        iv_nav_right.visibility = View.VISIBLE
        iv_nav_right.setImageResource(R.mipmap.fb_baishe)

        issue_grid.apply {
            layoutManager = FullyGridLayoutManager(this@IssueActivity, 4, GridLayoutManager.VERTICAL, false)
            adapter = imageAdapter
        }

        issue_type.setOnClickListener(this@IssueActivity)
    }

    override fun onClick(v: View) {
        super.onClick(v)

        DialogHelper.showTypeDialog(this@IssueActivity, arrayOf("广场", "好友", "我的")) { type ->
            mType = type

            when(type) {
                "1" -> issue_type.setRightString("广场")
                "2" -> issue_type.setRightString("好友")
                "3" -> issue_type.setRightString("我的")
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

                    if (selectList[0].mimeType == PictureMimeType.ofVideo()) {
                        showCompressDialog()
                        Thread(Runnable {
                            val item_path = BitmapHelper.saveBitmap(
                                    getVideoThumbnail(selectList[0].path),
                                    PictureFileUtils.createCameraFile(
                                            this@IssueActivity,
                                            PictureConfig.TYPE_IMAGE,
                                            PictureSelectionConfig.getInstance().outputCameraPath))

                            Luban.compress(this@IssueActivity, File(item_path))
                                    .putGear(Luban.THIRD_GEAR)
                                    .launch(object : OnCompressListener {
                                        override fun onError(e: Throwable) {}
                                        override fun onStart() { }

                                        override fun onSuccess(file: File) {
                                            selectList[0].compressPath = file.absolutePath

                                            runOnUiThread {
                                                dismissCompressDialog()
                                                imageAdapter.setList(selectList)
                                                imageAdapter.notifyDataSetChanged()
                                            }
                                        }

                                    })
                        }).start()
                    } else {
                        imageAdapter.setList(selectList)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.iv_nav_right -> {
                DialogHelper.showIssueCameraDialog(this@IssueActivity) { name ->
                    when(name) {
                        "拍摄" -> {
                            if (selectList.isNotEmpty() && selectList[0].mimeType != PictureMimeType.ofVideo()) {
                                showToask("不能同时选择图片或视频")
                                return@showIssueCameraDialog
                            }

                            PictureSelector.create(this@IssueActivity)
                                    .openGallery(PictureMimeType.ofVideo())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(4)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .previewImage(true)
                                    .previewVideo(true)
                                    .compressGrade(Luban.THIRD_GEAR)
                                    .isCamera(true)
                                    .isZoomAnim(true)
                                    .setOutputCameraPath(Const.SAVE_FILE)
                                    .compress(true)
                                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                                    .glideOverride(160, 160)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList)
                                    .videoQuality(0)
                                    .recordVideoSecond(30)
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                        "拍照" -> {
                            if (selectList.isNotEmpty() && selectList[0].mimeType == PictureMimeType.ofVideo()) {
                                showToask("不能同时选择图片或视频")
                                return@showIssueCameraDialog
                            }

                            PictureSelector.create(this@IssueActivity)
                                    .openCamera(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(9)
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
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList.apply { clear() })
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                        "相册" -> {
                            if (selectList.isNotEmpty() && selectList[0].mimeType == PictureMimeType.ofVideo()) {
                                showToask("不能同时选择图片或视频")
                                return@showIssueCameraDialog
                            }

                            PictureSelector.create(this@IssueActivity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(9)
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
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList)
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                    }
                }
            }
            R.id.bt_issue -> {
                if (mType == "") {
                    showToask("请选择发布类型")
                    return
                }
                if (et_content.text.toString() == "") {
                    showToask("请输入内容")
                    return
                }

                if (selectList.isNotEmpty() && selectList[0].mimeType == PictureMimeType.ofVideo()) {
                    OkGo.post<String>(HttpIP.dynamic_sub)
                            .tag(this@IssueActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("dynamicRole", mType)
                            .params("dynamicType", intent.getStringExtra("dynamicType"))
                            .params("content", et_content.text.toString())
                            .params("type", "VIDEO")
                            .params("video", File(selectList[0].path))
                            .params("videoImg", File(selectList[0].compressPath))
                            .execute(object : StringDialogCallback(this@IssueActivity) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    if (intent.getBooleanExtra("isMine", false))
                                        EventBus.getDefault().post(StateMessageEvent(mType, "我的动态"))
                                    else EventBus.getDefault().post(MainSecondEvent(mType, "朋友圈"))

                                    ActivityStack.getScreenManager().popActivities(this@IssueActivity::class.java)
                                }

                            })
                } else {
                    OkGo.post<String>(HttpIP.dynamic_sub)
                            .tag(this@IssueActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
                            .params("dynamicRole", mType)
                            .params("dynamicType", intent.getStringExtra("dynamicType"))
                            .params("content", et_content.text.toString())
                            .params("type", "IMGS")
                            .apply {
                                when (selectList.size) {
                                    9 -> {
                                        params("imgs9", File(selectList[8].compressPath))
                                        params("imgs8", File(selectList[7].compressPath))
                                        params("imgs7", File(selectList[6].compressPath))
                                        params("imgs6", File(selectList[5].compressPath))
                                        params("imgs5", File(selectList[4].compressPath))
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    8 -> {
                                        params("imgs8", File(selectList[7].compressPath))
                                        params("imgs7", File(selectList[6].compressPath))
                                        params("imgs6", File(selectList[5].compressPath))
                                        params("imgs5", File(selectList[4].compressPath))
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    7 -> {
                                        params("imgs7", File(selectList[6].compressPath))
                                        params("imgs6", File(selectList[5].compressPath))
                                        params("imgs5", File(selectList[4].compressPath))
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    6 -> {
                                        params("imgs6", File(selectList[5].compressPath))
                                        params("imgs5", File(selectList[4].compressPath))
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    5 -> {
                                        params("imgs5", File(selectList[4].compressPath))
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    4 -> {
                                        params("imgs4", File(selectList[3].compressPath))
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    3 -> {
                                        params("imgs3", File(selectList[2].compressPath))
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    2 -> {
                                        params("imgs2", File(selectList[1].compressPath))
                                        params("imgs1", File(selectList[0].compressPath))
                                    }
                                    1 -> params("imgs1", File(selectList[0].compressPath))
                                }

                                /*when {
                                    selectList.size > 0 -> params("imgs1", File(selectList[0].compressPath))
                                    selectList.size > 1 -> params("imgs2", File(selectList[1].compressPath))
                                    selectList.size > 2 -> params("imgs3", File(selectList[2].compressPath))
                                    selectList.size > 3 -> params("imgs4", File(selectList[3].compressPath))
                                    selectList.size > 4 -> params("imgs5", File(selectList[4].compressPath))
                                    selectList.size > 5 -> params("imgs6", File(selectList[5].compressPath))
                                    selectList.size > 6 -> params("imgs7", File(selectList[6].compressPath))
                                    selectList.size > 7 -> params("imgs8", File(selectList[7].compressPath))
                                    selectList.size > 8 -> params("imgs9", File(selectList[8].compressPath))
                                }*/
                            }
                            .execute(object : StringDialogCallback(this@IssueActivity) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    if (intent.getBooleanExtra("isMine", false))
                                        EventBus.getDefault().post(StateMessageEvent(mType, "我的动态"))
                                    else EventBus.getDefault().post(MainSecondEvent(mType, "朋友圈"))

                                    ActivityStack.getScreenManager().popActivities(this@IssueActivity::class.java)
                                }

                            })
                }
            }
        }
    }

    /**
     * compress loading dialog
     */
    private fun showCompressDialog() {
        if (!isFinishing) {
            dismissCompressDialog()
            compressDialog = PictureDialog(this)
            compressDialog!!.show()
        }
    }

    /**
     * dismiss compress dialog
     */
    private fun dismissCompressDialog() {
        if (!isFinishing
                && compressDialog != null
                && compressDialog!!.isShowing) {
            compressDialog!!.dismiss()
        }
    }
}
