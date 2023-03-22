package su.salut.billingexample.features.purchases.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import su.salut.billingexample.R
import su.salut.billingexample.databinding.ViewPurchaseItemBinding
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.lib.recyclerview.BaseAdapterBinder
import su.salut.billingexample.lib.recyclerview.DiffListUpdates

class PurchaseItemAdapterBinder(
    private val onClick: (Purchase) -> Unit
) : BaseAdapterBinder<Purchase, PurchaseItemAdapterBinder.ProductHolder>(),
    View.OnClickListener {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ProductHolder {
        val binding = ViewPurchaseItemBinding.inflate(inflater, parent, false)
        val holder = ProductHolder(binding)
        holder.binding.buttonAcknowledge.setOnClickListener(this@PurchaseItemAdapterBinder)

        return holder
    }

    override fun onBindViewHolder(holder: ProductHolder, item: Purchase) {
        val context = holder.binding.root.context
        val id = context.getString(R.string.text_id, item.purchaseToken)

        holder.binding.textId.text = Html.fromHtml(id, Html.FROM_HTML_MODE_LEGACY)

        holder.binding.textPurchased.isEnabled = item.isPurchased
        holder.binding.textPending.isEnabled = item.isPending
        holder.binding.textTrialPeriod.isEnabled = item.isTrialPeriod
        holder.binding.textAcknowledge.isEnabled = !item.isAcknowledged

        holder.binding.buttonAcknowledge.tag = item
        holder.binding.buttonAcknowledge.isEnabled = !item.isAcknowledged
    }

    override fun onClick(v: View?) {
        val item = v?.tag as? Purchase
        item?.let(onClick::invoke)
    }

    class ProductHolder(val binding: ViewPurchaseItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PurchaseItemDiffListUpdates(
        override val newList: List<Purchase>
    ) : DiffListUpdates<Purchase>() {
        override fun getTheSame(item: Purchase): Any = item
    }
}