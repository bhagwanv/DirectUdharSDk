package com.sk.imagepicker.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.imagepicker.R
import com.sk.imagepicker.constant.ImageProvider
import com.sk.imagepicker.listener.DismissListener
import com.sk.imagepicker.listener.ResultListener

/**
 * Show Dialog
 *
 * @author Dhaval Patel
 * @version 1.0
 * @since 04 January 2018
 */
internal object DialogHelper {

    /**
     * Show Image Provide Picker Dialog. This will streamline the code to pick/capture image
     *
     */
    fun showChooseAppDialog(
        context: Context,
        listener: ResultListener<ImageProvider>,
        dismissListener: DismissListener?
    ) {

        var dialog =
            BottomSheetDialog(
                context,
                R.style.Theme_Design_BottomSheetDialog
            )
        val layoutInflater = LayoutInflater.from(context)
        val customView = layoutInflater.inflate(R.layout.dialog_choose_app_bottom_sheet, null)
        dialog.setContentView(customView)
        dialog.show()

        customView.findViewById<View>(R.id.llCamera).setOnClickListener {
            listener.onResult(ImageProvider.CAMERA)
            dialog.dismiss()
        }

        customView.findViewById<View>(R.id.llGallery).setOnClickListener {
            listener.onResult(ImageProvider.GALLERY)
            dialog.dismiss()
        }

        customView.findViewById<View>(R.id.llCancel).setOnClickListener {
            dialog.dismiss()
        }
    }
}
