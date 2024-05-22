package com.example.onspot.utils

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

fun getAddressLatLng(context: Context, addressStr: String, defaultLatLng: LatLng = LatLng(46.7712, 23.6236)): LatLng {
    val geocoder = Geocoder(context)
    try {
        val addressList = geocoder.getFromLocationName(addressStr, 1)
        if (!addressList.isNullOrEmpty()) {
            val address = addressList[0]
            return LatLng(address.latitude, address.longitude)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return defaultLatLng
}