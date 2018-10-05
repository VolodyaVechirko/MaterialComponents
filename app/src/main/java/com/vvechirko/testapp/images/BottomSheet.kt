package com.vvechirko.testapp.images

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vvechirko.testapp.R

class BottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        return BottomSheetDialog(context!!, theme).apply {
            setContentView(R.layout.bottom_sheet)
            show()
        }.apply {
            setOnShowListener {
                
            }
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

//        val contentView = View.inflate(context, R.layout.bottom_sheet, null)
//        dialog.setContentView(contentView)
//        val parent = contentView.parent as View
//        parent.setFitsSystemWindows(true)
//        val bottomSheetBehavior = BottomSheetBehavior.from(parent)
//        contentView.measure(0, 0)
//        bottomSheetBehavior.setPeekHeight(contentView.getMeasuredHeight())
//
//        val params = parent.getLayoutParams() as CoordinatorLayout.LayoutParams
//        if (params.getBehavior() is BottomSheetBehavior) {
//            (params.getBehavior() as BottomSheetBehavior).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                }
//
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                        dismiss()
//                    }
//                }
//            })
//        }
//        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
//        parent.setLayoutParams(params)
    }
}