package com.example.onspot.data.repository

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class PlacesRepository(application: Application) {
    private val placesClient: PlacesClient = Places.createClient(application)

    fun getPlacesClient(): PlacesClient {
        return placesClient
    }
}
