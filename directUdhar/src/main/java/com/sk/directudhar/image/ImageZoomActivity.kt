package com.sk.user.agent.ui.image

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.sk.user.agent.R
import com.sk.user.agent.databinding.ActivityImageZoomBinding
import com.sk.user.agent.utils.Utils

class ImageZoomActivity : AppCompatActivity() {
    var mBinding: ActivityImageZoomBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_zoom)
        val extras = intent.extras
        try {
            if (extras != null) {
                mBinding!!.tvTicketId.text = "Ticket id: #" + extras.getString("TICKET_ID")
                Glide.with(this).load(extras.getString("IMAGE_URL")).into(mBinding!!.ivTicketImage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mBinding!!.back.setOnClickListener { v: View? -> onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Utils.rightTransaction(this)
    }
}