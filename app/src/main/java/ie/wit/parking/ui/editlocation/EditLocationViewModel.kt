package ie.wit.parking.ui.editlocation

import androidx.lifecycle.ViewModel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.parking.models.Location

class EditLocationViewModel : ViewModel() {
    private val _location = MutableLiveData<Location>()
    var observableLocation: LiveData<Location>
        get() = _location
        set(value) {_location.value = value.value}





    fun initMap(map: GoogleMap, activity: Activity) {
        val loc = LatLng(_location.value!!.lat, _location.value!!.lng)
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions()
            .title("Placemark")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, _location.value!!.zoom))

    }

    fun setLocation(location: Location){
        _location.value = location
    }
    fun doUpdateLocation(lat: Double, lng: Double, zoom: Float) {
        var location =Location()
        location.lat = lat
        location.lng = lng
        location.zoom = zoom

        _location.value = location

    }

    fun doOnBackPressed(activity: Activity) {
        val resultIntent = Intent()
        resultIntent.putExtra("location", _location.value)
        activity.setResult(Activity.RESULT_OK, resultIntent)
        activity.finish()
    }

    fun doUpdateMarker(marker: Marker) {
        val loc = LatLng(_location.value!!.lat, _location.value!!.lng)
        marker.snippet = "GPS : $loc"
    }
}