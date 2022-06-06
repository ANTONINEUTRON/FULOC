package com.neutron.fuloc.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.neutron.fuloc.R
import com.neutron.fuloc.databinding.FragmentLocationDescriptionDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    LocationDescriptionFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class LocationDescriptionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLocationDescriptionDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocationDescriptionDialogBinding.inflate(inflater,container,false)//.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): LocationDescriptionFragment =
            LocationDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}