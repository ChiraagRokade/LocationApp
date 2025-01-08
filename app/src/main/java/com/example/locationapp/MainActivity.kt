package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.helper.LocationUtils
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationApp(
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                        )
                }
            }
        }
    }
}

@Composable
fun LocationApp(modifier: Modifier, viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils, viewModel, context)
}

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context){
    val location = viewModel.location.value
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // I have access to location
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }else{
                // Ask for permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)

                if(rationaleRequired){
                    Toast.makeText(context,"Location Permission is required for this " +
                            "feature to work",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Location Permission is required. Please " +
                            "enable it in the Android Settings",
                                   Toast.LENGTH_SHORT).show()
                }
            }
        })

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
           )
    {
        if (location != null){
            Text(
                text = "Address: ${location.latitude} ${location.longitude}"
                )
        }else {
            Text(
                text = "Location not available"
                )
        }

        Button(
            onClick = {
                if (locationUtils.hasLocationPermission(context)){
                    // Permission already granted update the location
                    locationUtils.requestLocationUpdates(viewModel)
                }else{
                    // Request location permission
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                               )
                                                    )
                }
            }
              ) {
            Text(
                "Get Location"
                )
        }
    }
}