package ie.wit.parking.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.Location
import ie.wit.parking.models.ParkingModel
import timber.log.Timber

class EditViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()
    private val parking = MutableLiveData<ParkingModel>()
    private var map: GoogleMap? = null
    var observableParking: LiveData<ParkingModel>
        get() = parking
        set(value) {parking.value = value.value}


    val observableStatus: LiveData<Boolean>
        get() = status

    fun addParking(firebaseUser: MutableLiveData<FirebaseUser>, parking: ParkingModel) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser,parking)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun editParking(userid: String, parkingid: String, parking: ParkingModel) {
        status.value = try {
            FirebaseDBManager.update(userid,parkingid, parking)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun getParking(userid:String, id: String?) {
        try {

            FirebaseDBManager.findById(userid, id!!, parking)
            Timber.i("Detail getParking() Success : ${
                parking.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : ${e.message}")
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
    }
    fun locationUpdate() {

        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val marker = LatLng(parking.value!!.lat, parking.value!!.lng)
        Timber.i("locationUpdate marker: $marker")
        val options = MarkerOptions().title(marker.toString()).position(LatLng(parking.value!!.lat, parking.value!!.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(parking.value!!.lat, parking.value!!.lng), 15f))
    }
}