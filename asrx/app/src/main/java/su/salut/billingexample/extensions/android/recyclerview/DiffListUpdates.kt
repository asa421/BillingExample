package su.salut.billingexample.extensions.android.recyclerview

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class DiffListUpdates<T : Any> {

    protected abstract val newList: List<T>

    private lateinit var oldList: List<T>

    protected abstract fun getTheSame(item: T): Any

    fun dispatchUpdatesTo(adapter: BaseAdapterBinder<T, out RecyclerView.ViewHolder>) {
        oldList = adapter.items
        adapter.items = newList
        // Dispatch
        DiffUtil.calculateDiff(getCallback()).dispatchUpdatesTo(adapter)
    }

    private fun getCallback() = object : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return getTheSame(oldItem) == getTheSame(newItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem == newItem
        }
    }
}