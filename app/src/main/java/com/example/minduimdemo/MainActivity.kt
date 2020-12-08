package com.example.minduimdemo

import com.css.im_kit.ui.ConversationListFragment
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun layoutResource(): Int = R.layout.activity_main
    override fun initView() {
        val manager = this@MainActivity.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.container, ConversationListFragment())
        transaction.commit()
    }

    override fun initData() {
        binding.title = resources.getString(R.string.app_name)
    }

    override fun initListeners() {}
}