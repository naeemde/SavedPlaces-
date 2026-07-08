package com.savedplaces.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.savedplaces.app.data.Place
import com.savedplaces.app.data.PlaceDatabase
import com.savedplaces.app.data.PlaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlaceRepository

    val allPlaces: StateFlow<List<Place>>

    init {
        val dao = PlaceDatabase.getInstance(application).placeDao()
        repository = PlaceRepository(dao)
        allPlaces = repository.allPlaces.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /** يحفظ مكاناً جديداً بالاسم والإحداثيات المُعطاة. */
    fun savePlace(name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.insert(Place(name = name, latitude = latitude, longitude = longitude))
        }
    }

    /** يحذف مكاناً محفوظاً مسبقاً. */
    fun deletePlace(place: Place) {
        viewModelScope.launch {
            repository.delete(place)
        }
    }
}
