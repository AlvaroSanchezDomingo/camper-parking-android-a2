package ie.wit.parking.ui.view

import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.Location
import ie.wit.parking.models.ParkingModel
import timber.log.Timber

class ViewViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _parking = MutableLiveData<ParkingModel>()
    var observableParking: LiveData<ParkingModel>
        get() = _parking
        set(value) {_parking.value = value.value}


    fun getParking(userid:String, id: String?) {
        try {

            FirebaseDBManager.findById(userid, id!!, _parking)
            Timber.i("Detail getParking() Success : ${
                _parking.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : ${e.message}")
        }
    }
    fun loadImage(image: String, imageView: ImageView){
        Picasso.get()
            .load(image)
            .resize(200, 200)
            .into(imageView)
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        map?.uiSettings?.isZoomControlsEnabled = true
    }
    fun mapLocationUpdate() {
        map?.clear()
        val marker = LatLng(_parking.value!!.lat, _parking.value!!.lng)
        val options = MarkerOptions().title(marker.toString()).position(marker)
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
    }

}