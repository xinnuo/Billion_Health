package com.ruanmeng.billion_health

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import com.ruanmeng.adapter.GuideAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.http.toast
import kotlinx.android.synthetic.main.activity_guide.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class GuideActivity : BaseActivity() {

    private var isReady: Boolean = false
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isReady) {

                if (!getBoolean("isFirst")) startGuide()
                else {
                    startActivity(MainActivity::class.java)
                    onBackPressed()
                }
            } else {
                isReady = true
            }
        }
    }

    private fun startGuide() {
        val mLoopAdapter = GuideAdapter(baseContext)
        guide_banner.apply {
            setAdapter(mLoopAdapter)
            setHintViewVisibility(false)
            setOnItemSelectListener { position ->
                guide_in.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }
        }

        val imgs = listOf(R.mipmap.guide_01, R.mipmap.guide_02, R.mipmap.guide_03)
        mLoopAdapter.setImgs(imgs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //隐藏状态栏（全屏）
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //取消全屏
        // window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_guide)
        transparentStatusBar(false)

        GuideActivityPermissionsDispatcher.needPermissionWithCheck(this@GuideActivity)

        window.decorView.postDelayed({ handler.sendEmptyMessage(0) }, 2000)

        guide_in.setOnClickListener {
            putBoolean("isFirst", true)

            startActivity(MainActivity::class.java)
            onBackPressed()
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun needPermission() {
        handler.sendEmptyMessage(0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        GuideActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun permissionDenied() {
        showToask("请求权限被拒绝")
        onBackPressed()
    }
}
