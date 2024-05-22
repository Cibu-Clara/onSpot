package com.example.onspot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onspot.viewmodel.OfferViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchBar(
    placesClient: PlacesClient,
    offerViewModel: OfferViewModel,
    autocompleteAddress: String,
    onSuggestionSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(autocompleteAddress) }
    var expanded by remember { mutableStateOf(false) }
    val suggestions = offerViewModel.suggestions.collectAsState().value
    val scope = rememberCoroutineScope()

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = { newText ->
                    searchText = newText
                    expanded = newText.isNotEmpty()
                    if (newText.isNotEmpty()) {
                        offerViewModel.fetchPlaces(newText, placesClient)
                    }
                },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = {  isExpanded -> expanded = isExpanded && searchText.isNotEmpty()},
                placeholder = { Text("Search location") },
                leadingIcon = {
                    if (expanded) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { expanded = false}
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = {  isExpanded -> expanded = isExpanded && searchText.isNotEmpty() },
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        suggestions.forEach { prediction ->
            DropdownMenuItem(
                onClick = {
                    scope.launch {
                        val latLng = offerViewModel.getPlaceLatLng(prediction.placeId, placesClient)
                        onSuggestionSelected(latLng)
                        expanded = false
                        searchText = prediction.getFullText(null).toString()
                    }
                },
                text = {
                    Text(text = prediction.getFullText(null).toString())
                }
            )
        }
    }
}