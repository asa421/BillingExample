package su.salut.billingexample.ui.settings

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import su.salut.billingexample.R
import su.salut.billingexample.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.factory }

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val productIds: MutableList<String> = mutableListOf()
    private val adapter: BaseAdapter by lazy(LazyThreadSafetyMode.NONE) {
        return@lazy ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            productIds
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listViewProductIds.adapter = adapter

        settingsViewModel.applicationId.observe(viewLifecycleOwner, ::updateApplicationId)
        settingsViewModel.productIds.observe(viewLifecycleOwner, ::updateProductIds)
    }

    private fun updateApplicationId(value: String) {
        val id = getString(R.string.text_id, value)
        binding.textApplicationId.text = Html.fromHtml(id, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun updateProductIds(value: List<String>) {
        productIds.clear()
        productIds.addAll(value)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}