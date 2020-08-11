package dev.chu.memo.ui.rv_simple.amimation

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AnimatedItemHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun onEnterFromTop()
    abstract fun onExitToTop()
    abstract fun onEnterFromBottom()
    abstract fun onExitToBottom()
}