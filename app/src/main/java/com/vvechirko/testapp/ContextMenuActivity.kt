package com.vvechirko.testapp

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import kotlinx.android.synthetic.main.activity_context_menu.*
import android.view.MenuInflater
import androidx.appcompat.widget.PopupMenu


class ContextMenuActivity : AppCompatActivity() {

    var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_context_menu)

        registerForContextMenu(imageView)

        imageView2.setOnLongClickListener {
            if (actionMode != null) {
                false
            } else {
                actionMode = startSupportActionMode(actionModeCallback)
                it.isSelected = true
                true
            }
        }

        imageView3.setOnLongClickListener { v ->
            val popup = PopupMenu(this, v)
            popup.inflate(R.menu.context_menu)
            popup.setOnMenuItemClickListener {
                toast("${it.title}")
                false
            }
            popup.show()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo
        toast("${item.title} info - $info")
        return super.onContextItemSelected(item)
    }

    val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            Log.d("ActionMode", "onCreateActionMode")
            mode.menuInflater.inflate(R.menu.context_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            Log.d("ActionMode", "onPrepareActionMode")
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            Log.d("ActionMode", "onActionItemClicked")
            toast("${item.title} mode - $mode")
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            Log.d("ActionMode", "onDestroyActionMode")
            actionMode = null
        }
    }
}