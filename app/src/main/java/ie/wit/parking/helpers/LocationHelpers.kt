package ie.wit.parking.helpers


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create

val REQUEST_PERMISSIONS_REQUEST_CODE = 34

fun checkLocationPermissions(activity: FragmentActivity) : Boolean {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    else {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
        return false
    }
}
@SuppressLint("RestrictedApi")
fun createDefaultLocationRequest() : LocationRequest {
    val locationRequest = create().apply{
        interval = 30000
        fastestInterval = 20000
        priority = PRIORITY_HIGH_ACCURACY
    }
    return locationRequest
}