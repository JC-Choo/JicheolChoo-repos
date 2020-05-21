package dev.chu.memo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.chu.memo.R
import dev.chu.memo.databinding.ActivityMainBinding
import dev.chu.memo.etc.click.ClickBindingComponent
import dev.chu.memo.etc.extension.TAG
import dev.chu.memo.ui.bottom.BottomNavigationActivity
import dev.chu.memo.ui.bottom_sheet.BottomSheetFragment
import dev.chu.memo.ui.fav_tv_shows.FavTvShowsActivity
import dev.chu.memo.ui.library_image.ImageLibraryComparisonActivity
import dev.chu.memo.ui.map.CoronaActivity
import dev.chu.memo.ui.memo.MemoActivity
import dev.chu.memo.ui.merge_adapter.SearchRepositoriesActivity
import dev.chu.memo.ui.motion_layout.MotionActivity
import dev.chu.memo.ui.mvi.MviActivity
import dev.chu.memo.ui.recycler_multi_viewtype.ui.RecyclerViewActivity
import dev.chu.memo.ui.rv_coroutine.UserActivity
import dev.chu.memo.ui.rx_activity.repos.GithubReposActivity

//class MainActivity : BaseActivity<ActivityMainBinding>() {
//
//    @LayoutRes
//    override fun getLayoutRes(): Int = R.layout.activity_main

class MainActivity: AppCompatActivity(), View.OnClickListener {

    private val bottomSheetFragment by lazy { BottomSheetFragment() }
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var binding: ActivityMainBinding

    // region lifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "initView")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, ClickBindingComponent(lifecycle))
        binding.lifecycleOwner = this
        binding.clickListener = this
        binding.activity = this

        setBottomSheet()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
    // endregion

    private fun setBottomSheet() {
//        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
//        val dialog = BottomSheetDialog(this)
//        dialog.setContentView(dialogView)
//        dialog.show()

        sheetBehavior = BottomSheetBehavior.from(binding.mainBs.bottomSheet)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.mainBtBottomSheet.text = "Close Sheet"
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.mainBtBottomSheet.text = "Expand Sheet"
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
    }

    // region onClick
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.main_bt_memo -> startActivity(Intent(this, MemoActivity::class.java))
            R.id.main_bt_corona -> startActivity(Intent(this, CoronaActivity::class.java))
            R.id.main_bt_bottom_navigation -> startActivity(Intent(this, BottomNavigationActivity::class.java))
            R.id.main_bt_github -> startActivity(Intent(this, GithubReposActivity::class.java))
            R.id.main_bt_user -> startActivity(Intent(this, UserActivity::class.java))
            R.id.main_bt_mvi -> startActivity(Intent(this, MviActivity::class.java))
            R.id.main_bt_motion -> startActivity(Intent(this, MotionActivity::class.java))
            R.id.main_bt_search_repositories -> startActivity(Intent(this, SearchRepositoriesActivity::class.java))
            R.id.main_bt_fav_tv_shows -> startActivity(Intent(this, FavTvShowsActivity::class.java))
            R.id.main_bt_Fav_tv_speech -> startActivity(Intent(this, SpeechActivity::class.java))
            R.id.main_bt_image_library -> startActivity(Intent(this, ImageLibraryComparisonActivity::class.java))
            R.id.main_bt_bottom_sheet -> bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            R.id.main_bt_rv -> {
                Log.e(TAG, "rvrvrvrv")
                startActivity(Intent(this, RecyclerViewActivity::class.java))
            }
        }
    }

//    fun onClickRecyclerView() {
//        Log.e(TAG, "rvrvrvrv")
//        startActivity(Intent(this, RecyclerViewActivity::class.java))
//    }
    // endregion
}
