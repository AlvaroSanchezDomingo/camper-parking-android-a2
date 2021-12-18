package ie.wit.parking.ui.view

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.models.Review
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ViewViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _parking = MutableLiveData<ParkingModel>()
    var observableParking: LiveData<ParkingModel>
        get() = _parking
        set(value) {_parking.value = value.value}

    private val _rating = MutableLiveData<Float>()
    var observableRating: LiveData<Float>
        get() = _rating
        set(value) {_rating.value = value.value}


    @SuppressLint("SimpleDateFormat")
    fun addReview(user: String, comment: String, rating: Float){
        try {
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val currentDate = sdf.format(Date())
            val review =
                    Review(
                        comment = comment,
                        user = user,
                        date = currentDate,
                        rating = rating
                    )

            FirebaseDBManager.addReview(_parking.value!!.uid, review )
            Timber.i("Add review successful : $review")
        }
        catch (e: Exception) {
            Timber.i("Add review fail : ${e.message}")
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

    fun calculateRating(){
        var count =0f
        var sum = 0f
        for ((key, value) in _parking.value!!.reviews) {
            Timber.i("Calculating rating review : $value")
            count +=1
            Timber.i("$key = $value")
            sum += value.rating!!
        }
        _rating.value = sum/count
        Timber.i("Calculating rating rating : ${_rating.value}")
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