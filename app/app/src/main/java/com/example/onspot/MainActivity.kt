package com.example.onspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.onspot.navigation.NavigationGraph
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : ComponentActivity() {
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDltybvhZtouQ8bLIUHtop4AERtiwaUihw")
        }
        placesClient = Places.createClient(this)

        setContent {
            NavigationGraph(placesClient = placesClient)
        }
    }
}