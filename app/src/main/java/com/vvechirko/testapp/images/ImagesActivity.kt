package com.vvechirko.testapp.images

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.vvechirko.testapp.*
import com.vvechirko.testapp.data.RecipeModel
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity() {

    companion object {
        const val STATE_NORMAL = 1
        const val STATE_SELECT = 2
        const val STATE_SHEET = 3
    }

    private var state: Int = STATE_NORMAL
    private var adapter: Adapter? = null

    private val viewModel: ImagesViewModel by lazy { ImagesViewModel() }

    private var appBarShow: Float = 0f
    private var appBarHide: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            when (state) {
                STATE_NORMAL -> onBackPressed()
                STATE_SELECT -> adapter?.clearSelection()
//                STATE_SHEET -> closeBottomSheet
            }
        }

        adapter = Adapter().apply {
            setOnItemSelectListener {
                val state = if (it.isEmpty()) STATE_NORMAL else STATE_SELECT
                notifyAppBar(state)

                if (state == STATE_NORMAL) {
                    toolbar.title = getString(R.string.app_name)
                } else {
                    toolbar.title = "Selected: ${it.size}"
                }
            }

            setOnItemClickListener { openItem(it) }
        }
        recyclerView.adapter = adapter

        subscribeViewModel()

        fabAdd.setOnClickListener {
            ImageActivity.start(this, it.centerLocation())
        }

        fabShare.hide()
        bottomAppBar.onPreDraw {
            appBarHide = bottomAppBar.height.toFloat()
            bottomAppBar.translationY = appBarHide
        }

        bottomAppBar.inflateMenu(R.menu.menu_selection)
        bottomAppBar.setOnMenuItemClickListener {

            true
        }
    }

//    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        menuInflater.inflate(R.menu.context_menu, menu)
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
//        return when (item.itemId) {
//            R.id.action_copy -> { toast("action_copy"); true }
//            R.id.action_edit -> { toast("action_edit"); true }
//            R.id.action_delete -> { toast("action_delete"); true }
//            else -> super.onContextItemSelected(item)
//        }
//    }

    private fun subscribeViewModel() {
        viewModel.init()
//        viewModel.data.observe(this, ResourceObserver(
//                { data -> showProgress(false); adapter?.setData(data?.recipes ?: listOf()) },
//                { error -> showProgress(false); toast(error) },
//                { showProgress(true) }
//        ))

        viewModel.data.observeResource(this,
                { data -> showProgress(false); adapter?.setData(data?.recipes ?: listOf()) },
                { error -> showProgress(false); toast(error) },
                { showProgress(true) }
        )
    }

    private fun notifyAppBar(state: Int) {
        if (this.state == state) {
            return
        }

        this.state = state
        when (state) {
            STATE_NORMAL -> {
                toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)
                appBar.setBackgroundResource(R.color.colorPrimary)
                window.statusBarColorRes(R.color.colorPrimaryDark)

                hideBottomAppBar()
            }
            STATE_SELECT -> {
//                toolbar.setNavigationAnim(R.drawable.animate_home)
                toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp)
                appBar.setBackgroundResource(R.color.colorDark)
                window.statusBarColorRes(R.color.colorDarkPrimaryDark)

                showBottomAppBar()
            }
        }
        invalidateOptionsMenu()
    }

    private fun showBottomAppBar() {
        ObjectAnimator.ofFloat(bottomAppBar, "translationY", appBarHide, appBarShow)
                .addInterpolator(AccelerateInterpolator())
                .onStart { fabAdd.hide() }
                .onEnd { fabShare.show() }
                .start()
    }

    private fun hideBottomAppBar() {
        ObjectAnimator.ofFloat(bottomAppBar, "translationY", appBarShow, appBarHide)
                .addInterpolator(DecelerateInterpolator())
                .onStart { fabShare.hide() }
                .onEnd { fabAdd.show() }
                .start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (state) {
            STATE_NORMAL -> menuInflater.inflate(R.menu.menu_images, menu)
//            STATE_SELECT -> menuInflater.inflate(R.menu.menu_selection, menu)
            STATE_SELECT -> menu.clear()
            STATE_SHEET -> menu.clear()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_settings -> openBottomSheet()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openItem(it: RecipeModel) {

    }

    private fun openBottomSheet(): Boolean {
//        BottomSheetDialog(this).apply {
//            setContentView(R.layout.bottom_sheet)
//            show()
//        }
        BottomSheet().show(supportFragmentManager, "")
        return true
    }

    fun showProgress(show: Boolean) {
        progressBar.visible = show
    }
}