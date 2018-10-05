package com.vvechirko.testapp

import android.content.Context
import android.hardware.input.InputManager
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editText.setOnEditorActionListener { v, actionId, event ->
            Log.d("EditorAction", "actionId $actionId, event $event")
            false
        }

        window.decorView.onSoftKeyboard { visible ->
            Log.d("onSoftKeyboard", "SoftKeyboard visible $visible")
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

//        with(window.decorView) {
//            val rect = Rect()
//            viewTreeObserver.addOnGlobalLayoutListener {
//                getWindowVisibleDisplayFrame(rect)
//                Log.d("GlobalLayout", "rect.height ${rect.height()}, window.height $height")
//                if (height - rect.height() > resources.toDp(200f)) {
//
//                }
//            }
//        }
    }
}