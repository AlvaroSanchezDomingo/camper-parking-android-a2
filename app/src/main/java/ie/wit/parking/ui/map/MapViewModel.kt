package ie.wit.parking.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingModel
import timber.log.Timber

class MapViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _parkings = MutableLiveData<List<ParkingModel>>()
    var observableParkings: LiveData<List<ParkingModel>>
        get() = _parkings
        set(value) {_parkings.value = value.value}

    fun getUserParkings(userid:String) {
        try {
            FirebaseDBManager.findAll(userid, _parkings)
            Timber.i("Detail getUserParkings() Success : ${
                _parkings.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getUserParkings() Error : ${e.message}")
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        map?.uiSettings?.isZoomControlsEnabled = true
    }
    fun mapLocationUpdate() {
        map?.clear()
        var marker = LatLng(0.0, 0.0)
        _parkings.value!!.forEach {
            println("parking in forEach $it")
            marker = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(marker.toString()).position(marker)
            map?.addMarker(options)
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))

    }

}