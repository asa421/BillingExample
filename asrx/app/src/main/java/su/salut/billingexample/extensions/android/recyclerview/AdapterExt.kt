package su.salut.billingexample.extensions.android.recyclerview

import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun <T: Any> BaseAdapterBinder<T, out ViewHolder>.applyUpdatesFrom(diffUtil: DiffListUpdates<T>) {
    diffUtil.dispatchUpdatesTo(this)
}