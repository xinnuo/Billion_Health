package com.ruanmeng.billion_health

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class BookDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_done)
        init_title("预约结果")
    }
}
