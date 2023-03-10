package su.salut.billingexample.ui.settings

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

        settingsViewModel.applicationId.observe(viewLifecycleOwner, ::updateApplicationId)
        settingsViewModel.productIds.observe(viewLifecycleOwner, ::updateProductIds)
    }

    private fun updateApplicationId(value: String) {
        val textApplicationId = getString(R.string.text_application_id, value)

        @Suppress("DEPRECATION")
        binding.textApplicationId.text = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> Html.fromHtml(textApplicationId)
            else -> Html.fromHtml(textApplicationId, Html.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun updateProductIds(value: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, value)
        binding.listViewProductIds.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}