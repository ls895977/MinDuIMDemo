package com.css.im_kit.ui.activity

import com.bumptech.glide.Glide
import com.css.im_kit.R
import com.css.im_kit.databinding.ActivityBigPicBinding
import com.css.im_kit.ui.base.BaseActivity

class BigPicActivity : BaseActivity<ActivityBigPicBinding>() {
    override fun layoutResource(): Int = R.layout.activity_big_pic
    override fun initView() {
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        Glide.with(this).load(imageUrl).into(binding.photoView)
    }

    override fun initData() = Unit
    override fun initListeners() = binding.photoView.setOnClickListener { onBackPressed() }
}