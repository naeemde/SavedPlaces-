package com.savedplaces.app.data

import kotlinx.coroutines.flow.Flow

/**
 * طبقة وسيطة بين قاعدة البيانات وباقي التطبيق، تسهّل استبدال مصدر البيانات مستقبلاً.
 */
class PlaceRepository(private val placeDao: PlaceDao) {

    val allPlaces: Flow<List<Place>> = placeDao.getAllPlaces()

    suspend fun insert(place: Place): Long = placeDao.insertPlace(place)

    suspend fun delete(place: Place) = placeDao.deletePlace(place)
}
