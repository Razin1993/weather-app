package com.razin.weather.ui.uiUtils

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.razin.weather.R
import com.razin.weather.databinding.DialogPermissionRationaleBinding
import com.razin.weather.util.PermissionUtil

object DialogUtil {

    fun showPermissionRationaleDialog(title:String,message:String,activity: Activity){

        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        val dialogBinding = DialogPermissionRationaleBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.txtTitle.text = title
        dialogBinding.txtMessage.text = message

        dialogBinding.txtCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        dialogBinding.txtSettings.setOnClickListener {
            bottomSheetDialog.dismiss()
            PermissionUtil.launchPermissionSettings(activity)
        }

        bottomSheetDialog.show()

    }

    fun showLocationSettingDialog(title:String,message:String,activity: Activity){

        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        val dialogBinding = DialogPermissionRationaleBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.txtTitle.text = title
        dialogBinding.txtMessage.text = message

        dialogBinding.txtCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        dialogBinding.txtSettings.setOnClickListener {
            bottomSheetDialog.dismiss()
            PermissionUtil.launchLocationSetting(activity)
        }

        bottomSheetDialog.show()

    }
}