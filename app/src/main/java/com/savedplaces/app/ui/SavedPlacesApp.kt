package com.savedplaces.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savedplaces.app.location.LocationHelper
import com.savedplaces.app.ui.components.AddPlaceDialog
import com.savedplaces.app.ui.components.EmptyState
import com.savedplaces.app.ui.components.PlaceCard
import com.savedplaces.app.util.IntentUtils
import com.savedplaces.app.viewmodel.PlaceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesApp(viewModel: PlaceViewModel = viewModel()) {
    val context = LocalContext.current
    val places by viewModel.allPlaces.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationHelper = remember { LocationHelper(context) }

    var showAddDialog by remember { mutableStateOf(false) }
    var pendingLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun fetchCurrentLocationAndAsk() {
        scope.launch {
            isFetchingLocation = true
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    pendingLocation = location
                    showAddDialog = true
                } else {
                    snackbarHostState.showSnackbar(
                        "تعذّر تحديد الموقع الحالي، تأكد من تفعيل خدمة الموقع على جهازك"
                    )
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("حدث خطأ أثناء تحديد الموقع، حاول مجدداً")
            } finally {
                isFetchingLocation = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchCurrentLocationAndAsk()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("يجب منح إذن الموقع حتى يستطيع التطبيق حفظ مكانك الحالي")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("أماكني المحفوظة") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (hasLocationPermission()) {
                        fetchCurrentLocationAndAsk()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (isFetchingLocation) "جارٍ تحديد الموقع..." else "حفظ موقعي الحالي") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (places.isEmpty()) {
                EmptyState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(places, key = { it.id }) { place ->
                        PlaceCard(
                            place = place,
                            onNavigate = { IntentUtils.openNavigation(context, place) },
                            onShare = { IntentUtils.sharePlace(context, place) },
                            onDelete = { viewModel.deletePlace(place) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (showAddDialog && pendingLocation != null) {
        val location = pendingLocation
        if (location != null) {
            AddPlaceDialog(
                onConfirm = { name ->
                    viewModel.savePlace(
                        name = name.ifBlank { "موقع محفوظ" },
                        latitude = location.first,
                        longitude = location.second
                    )
                    showAddDialog = false
                    pendingLocation = null
                },
                onDismiss = {
                    showAddDialog = false
                    pendingLocation = null
                }
            )
        }
    }
}
