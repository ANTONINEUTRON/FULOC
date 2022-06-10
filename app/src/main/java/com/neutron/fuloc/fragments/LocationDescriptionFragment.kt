package com.neutron.fuloc.fragments

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.neutron.fuloc.MapDirectionActivity
import com.neutron.fuloc.data.LocationsInFulafia
import com.neutron.fuloc.databinding.FragmentLocationDescriptionDialogBinding
import com.neutron.fuloc.models.Place
import java.math.BigDecimal
import java.math.RoundingMode

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
class LocationDescriptionFragment(val index: Int, val userLatLng: LatLng) : BottomSheetDialogFragment() {

    private val fulafiaLocation:Place = LocationsInFulafia.listOfPlaces[index]
    private var _binding: FragmentLocationDescriptionDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocationDescriptionDialogBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //access the list of places and get details
        val place: Place = LocationsInFulafia.listOfPlaces[index]
        binding.name.text = place.name
        binding.description.text = place.description
        binding.distance.text = "Distance: ${BigDecimal(getDistance()).setScale(2, RoundingMode.HALF_EVEN)} meters"

        binding.dirBtn.setOnClickListener {
            val url:String = "https://www.google.com/maps/dir/${userLatLng.latitude},${userLatLng.longitude}/${fulafiaLocation.latitude},${fulafiaLocation.longitude}"
            //"https://www.google.com/maps?saddr=Current+Location&daddr=${fulafiaLocation.longitude},${fulafiaLocation.latitude}"
            startActivity(
                Intent(requireContext(), MapDirectionActivity::class.java)
                    .putExtra("URL",url)
            )
        }
    }

    private fun getDistance(): Double {
        return LocationsInFulafia.calculateDistance(
            fulafiaLocation.latLng,
            userLatLng
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}