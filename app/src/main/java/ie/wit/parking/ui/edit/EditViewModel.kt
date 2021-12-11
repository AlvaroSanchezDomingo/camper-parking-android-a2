package ie.wit.parking.ui.edit


import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.helpers.showImagePicker
import ie.wit.parking.models.Location
import ie.wit.parking.models.ParkingModel
import timber.log.Timber


class EditViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _parking = MutableLiveData<ParkingModel>()
    var observableParking: LiveData<ParkingModel>
        get() = _parking
        set(value) {_parking.value = value.value}

    private val _status = MutableLiveData<Boolean>()
    val observableStatus: LiveData<Boolean>
        get() = _status


    fun radioButtonCategory(category: Int){
        _parking.value!!.category = category
    }

    fun addParking(firebaseUser: MutableLiveData<FirebaseUser>) {
        _status.value = try {
            var parking = ParkingModel(title = _parking.value!!.title, description = _parking.value!!.description, category = _parking.value!!.category,
                email = firebaseUser.value?.email!!, lat = _parking.value!!.lat, lng = _parking.value!!.lng, zoom=15f)
            FirebaseDBManager.create(firebaseUser,parking)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun editParking(userid: String, parkingid: String, parking: ParkingModel) {
        _status.value = try {
            FirebaseDBManager.update(userid,parkingid, parking)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
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

    fun doSelectImage(intentLauncher: ActivityResultLauncher<Intent>) {
        showImagePicker(intentLauncher)
    }

    fun setImage(image: Uri){
        _parking.value!!.image = image
        Timber.i("Parking after saving image: ${_parking.value}")
    }
    fun setDefaultParking(userid:String){
        _parking.value = ParkingModel(email=userid)
    }

    fun setParkingLocation(location: Location){
        _parking.value!!.lat = location.lat
        _parking.value!!.lng = location.lng
        _parking.value!!.zoom = location.zoom
    }

    fun getLocation():Location{
        val location = Location()
        if(_parking.value?.lat != null){
            location.lat = _parking.value!!.lat
            location.lng = _parking.value!!.lng
            location.zoom = _parking.value!!.zoom
        }else{
            location.lat = 0.0
            location.lng = 0.0
            location.zoom = 0f
        }
        return location
    }

}