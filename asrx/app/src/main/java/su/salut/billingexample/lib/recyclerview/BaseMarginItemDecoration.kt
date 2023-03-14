package su.salut.billingexample.lib.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BaseMarginItemDecoration(
    private val topMargin: Int? = null
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            val topMargin = topMargin ?: parent.paddingTop
            outRect.set(0, topMargin, 0, 0)
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}