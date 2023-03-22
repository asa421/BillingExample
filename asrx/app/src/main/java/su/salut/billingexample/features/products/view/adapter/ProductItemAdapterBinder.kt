package su.salut.billingexample.features.products.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import su.salut.billingexample.R
import su.salut.billingexample.databinding.ViewProductItemBinding
import su.salut.billingexample.application.domain.models.PurchasesProduct
import su.salut.billingexample.extensions.android.recyclerview.BaseAdapterBinder
import su.salut.billingexample.extensions.android.recyclerview.DiffListUpdates

class ProductItemAdapterBinder(
    private val onClick: (PurchasesProduct) -> Unit
) : BaseAdapterBinder<PurchasesProduct, ProductItemAdapterBinder.ProductHolder>(),
    View.OnClickListener {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ProductHolder {
        val binding = ViewProductItemBinding.inflate(inflater, parent, false)
        val holder = ProductHolder(binding)
        holder.binding.buttonMakePurchase.setOnClickListener(this@ProductItemAdapterBinder)

        return holder
    }

    override fun onBindViewHolder(holder: ProductHolder, item: PurchasesProduct) {
        val context = holder.binding.root.context
        val id = context.getString(R.string.text_id, item.product.productId)
        val price = context.getString(R.string.text_price, item.product.priceLabel)

        holder.binding.textId.text = Html.fromHtml(id, Html.FROM_HTML_MODE_LEGACY)
        holder.binding.textPrice.text = Html.fromHtml(price, Html.FROM_HTML_MODE_LEGACY)

        holder.binding.textPurchased.isEnabled = item.isPurchased
        holder.binding.textPending.isEnabled = item.isPending
        holder.binding.textAcknowledge.isEnabled = !item.isAcknowledged

        holder.binding.buttonMakePurchase.tag = item
        holder.binding.buttonMakePurchase.isEnabled = item.purchases.isEmpty()
    }

    override fun onClick(v: View?) {
        val item = v?.tag as? PurchasesProduct
        item?.let(onClick::invoke)
    }

    class ProductHolder(val binding: ViewProductItemBinding) : RecyclerView.ViewHolder(binding.root)

    class ProductItemDiffListUpdates(
        override val newList: List<PurchasesProduct>
    ) : DiffListUpdates<PurchasesProduct>() {
        override fun getTheSame(item: PurchasesProduct): Any = item
    }
}