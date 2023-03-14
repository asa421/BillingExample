package su.salut.billingexample.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import su.salut.billingexample.databinding.FragmentProductsBinding
import su.salut.billingexample.lib.recyclerview.BaseMarginItemDecoration
import su.salut.billingexample.lib.recyclerview.applyUpdatesFrom

class ProductsFragment : Fragment() {

    private val productsViewModel: ProductsViewModel by viewModels { ProductsViewModel.factory }

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        return@lazy ProductItemAdapterBinder(productsViewModel::onSubscription)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = BaseMarginItemDecoration()
        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.addItemDecoration(itemDecoration)
        (binding.recyclerViewProducts.itemAnimator as? SimpleItemAnimator)
            ?.supportsChangeAnimations = false



        productsViewModel.purchasesProduct.observe(viewLifecycleOwner, adapter::applyUpdatesFrom)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}