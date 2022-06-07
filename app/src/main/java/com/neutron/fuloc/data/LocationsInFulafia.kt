package com.neutron.fuloc.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.neutron.fuloc.models.Place

object LocationsInFulafia {
    val listOfPlaces = listOf<Place>(
        Place(
            "Permanent site",
            "The gate of FULAFIA",
            8.478464,
            8.558770
        ),
        Place(
            "Take-off site",
            "The take-off site of the university where it all started",
            8.471179,
            8.587331
        ),
        Place(
            "Faculty of computing",
            "The building housing the faculty of computing and also the school ICT department",
            8.473772,
            8.558185
        ),
        Place(
            "Library",
            "The library of FULAFIA",
            8.473613,
            8.555529
        ),
        Place(
            "Central Admin Building",
            "The school building housing the administrative unit and senate chair",
            8.473701,
            8.556472
        )
    )
    fun calculateDistance(pointA: LatLng, pointB: LatLng): Double {
        return SphericalUtil.computeDistanceBetween(pointA, pointB) //return distance from point to point b in meters
    }
}