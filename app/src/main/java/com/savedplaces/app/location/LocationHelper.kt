package com.savedplaces.app.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * غلاف بسيط حول FusedLocationProviderClient (مجاني بالكامل، لا يحتاج أي مفتاح API)
 * لجلب الموقع الحالي بدقة عالية بشكل متزامن (suspend) داخل الكوروتينات.
 */
class LocationHelper(context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.getCurrentLocation(request, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    if (continuation.isActive) {
                        if (location != null) {
                            continuation.resume(Pair(location.latitude, location.longitude))
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
}
