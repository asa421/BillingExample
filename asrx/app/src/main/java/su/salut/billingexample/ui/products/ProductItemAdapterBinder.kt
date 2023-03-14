package su.salut.billingexample.ui.products

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import su.salut.billingexample.R
import su.salut.billingexample.databinding.ViewProductItemBinding
import su.salut.billingexample.domain.models.PurchasesProduct
import su.salut.billingexample.lib.recyclerview.BaseAdapterBinder
import su.salut.billingexample.lib.recyclerview.DiffListUpdates

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
        val id = holder.binding.root.context.getString(R.string.text_id, item.product.productId)
        val price = holder.binding.root.context.getString(R.string.text_price, item.product.priceLabel)

        holder.binding.textId.text = Html.fromHtml(id, Html.FROM_HTML_MODE_LEGACY)
        holder.binding.textPrice.text = Html.fromHtml(price, Html.FROM_HTML_MODE_LEGACY)
        holder.binding.buttonMakePurchase.tag = item
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