package su.salut.billingexample.features.purchases.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import su.salut.billingexample.databinding.FragmentPurchasesBinding
import su.salut.billingexample.extensions.android.recyclerview.BaseMarginItemDecoration
import su.salut.billingexample.extensions.android.recyclerview.applyUpdatesFrom
import su.salut.billingexample.features.purchases.view.adapter.PurchaseItemAdapterBinder
import su.salut.billingexample.features.purchases.viewmodel.PurchasesViewModel

class PurchasesFragment : Fragment() {

    private val purchasesViewModel: PurchasesViewModel by viewModels { PurchasesViewModel.factory }

    private var _binding: FragmentPurchasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        return@lazy PurchaseItemAdapterBinder(purchasesViewModel::onAcknowledge)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemDecoration = BaseMarginItemDecoration()
        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.addItemDecoration(itemDecoration)
        (binding.recyclerViewProducts.itemAnimator as? SimpleItemAnimator)
            ?.supportsChangeAnimations = false

        purchasesViewModel.purchases.observe(viewLifecycleOwner, adapter::applyUpdatesFrom)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}