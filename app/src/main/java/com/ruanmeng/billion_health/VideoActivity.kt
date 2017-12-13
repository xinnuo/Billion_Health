package com.ruanmeng.billion_health

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wechatsmallvideoview.SurfaceVideoViewDownloadCreator
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File

class VideoActivity : AppCompatActivity() {

    private var surfaceVideoViewCreator: SurfaceVideoViewDownloadCreator? = null
    private var videoImg = ""
    private var videoPath = ""
    private var imgWidth = ""
    private var imgHeight = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏状态栏
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= 19) {
            // 虚拟导航栏透明
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }

        setContentView(R.layout.activity_video)

        videoImg = intent.getStringExtra("img")
        videoPath = intent.getStringExtra("video")
        imgWidth = intent.getStringExtra("width")
        imgHeight = intent.getStringExtra("height")

        init_title()
    }

    fun init_title() {

        surfaceVideoViewCreator = object : SurfaceVideoViewDownloadCreator(this@VideoActivity, video_container, videoPath) {

            override fun downloadVideo(
                    videoPath: String,
                    fileDir: String,
                    fileName: String) {
                OkGo.get<File>(videoPath)
                        .tag(this@VideoActivity)
                        .execute(object : FileCallback(fileDir, fileName) {

                            override fun onStart(request: Request<File, out Request<Any, Request<*, *>>>) {
                                OkLogger.i("onStart：开始下载")
                                downloadBefore()
                            }

                            override fun onSuccess(response: Response<File>) {
                                OkLogger.i("onSuccess：" + response.body().absolutePath)
                                play(response.body().absolutePath)
                            }

                            override fun onError(response: Response<File>) {
                                OkLogger.e("onError：" + response.body().absoluteFile)
                                if (response.body().exists()) response.body().delete()
                            }

                            override fun downloadProgress(progress: Progress) {
                                OkLogger.i("downloadProgress：" + progress.toString())
                                updateProgress((progress.fraction * 100).toInt())
                            }
                        })
            }

            override fun getActivity(): Activity = this@VideoActivity

            override fun setAutoPlay(): Boolean = true

            override fun getSurfaceWidth(): Int = 0

            override fun getSurfaceHeight(): Int =
                    CommonUtil.getScreenWidth(this@VideoActivity) * imgHeight.toInt() / imgWidth.toInt()

            override fun setThumbImage(thumbImageView: ImageView) {
                Glide.with(baseContext)
                        .load(videoImg)
                        .apply(RequestOptions
                                .centerCropTransform()
                                .placeholder(R.mipmap.not_1)
                                .error(R.mipmap.not_1)
                                .dontAnimate())
                        .into(thumbImageView)
            }

            override fun getSecondVideoCachePath(): String? = null
        }
    }

    override fun onPause() {
        super.onPause()
        surfaceVideoViewCreator!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        surfaceVideoViewCreator!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        surfaceVideoViewCreator!!.onDestroy()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        surfaceVideoViewCreator!!.onKeyEvent(event)
        /** 声音的大小调节  */
        return super.dispatchKeyEvent(event)
    }

    companion object {

        fun startVieoView(context: Context, view: View, width: String, height: String, img: String, video: String) {

            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.putExtra("img", img)
            intent.putExtra("video", video)

            //android V4包的类,用于两个activity转场时的缩放效果实现
            val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, 0, 0)
            try {
                ActivityCompat.startActivity(context, intent, optionsCompat.toBundle())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                context.startActivity(intent)
                (context as Activity).overridePendingTransition(com.maning.imagebrowserlibrary.R.anim.browser_enter_anim, 0)
            }

        }
    }
}
