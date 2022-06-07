package com.neutron.fuloc.models

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val image: Int? = null,
    val latLng: LatLng = LatLng(latitude, longitude)
)
