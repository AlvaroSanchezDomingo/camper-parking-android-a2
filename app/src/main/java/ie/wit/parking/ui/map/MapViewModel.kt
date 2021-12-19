package ie.wit.parking.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import ie.wit.parking.R

import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingModel
import timber.log.Timber
import androidx.core.content.ContextCompat

import android.graphics.drawable.Drawable

import com.google.android.gms.maps.model.BitmapDescriptor




class MapViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _parkings = MutableLiveData<List<ParkingModel>>()
    var observableParkings: LiveData<List<ParkingModel>>
        get() = _parkings
        set(value) {_parkings.value = value.value}

    private val _parking = MutableLiveData<ParkingModel>()
    var observableParking: LiveData<ParkingModel>
        get() = _parking
        set(value) {_parking.value = value.value}

    private val _rating = MutableLiveData<Float>()
    var observableRating: LiveData<Float>
        get() = _rating
        set(value) {_rating.value = value.value}

    fun load(email:String) {
        try {
            FirebaseDBManager.findAll(email, _parkings)
            Timber.i("Detail getUserParkings() Success : %s", _parkings.value.toString())
        }
        catch (e: Exception) {
            Timber.i("Detail getUserParkings() Error : ${e.message}")
        }
    }
    fun loadAll() {
        try {
            FirebaseDBManager.findAll(_parkings)
            Timber.i("List Load All Success : ${_parkings.value.toString()}")
        }
        catch (e: java.lang.Exception) {
            Timber.i("List Load All Error : $e.message")
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        map?.uiSettings?.isZoomControlsEnabled = true
        map!!.setOnMarkerClickListener {
            Timber.i("setOnMarkerClickListener $it")
            doMarkerSelected(it)
            true
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.360401, -6.043043), 10f))
    }
    fun mapLocationUpdate(context: Context) {
        Timber.i("Step1")
        map?.clear()
        var marker = LatLng(0.0, 0.0)
        _parkings.value!!.forEach {
            Timber.i("Step2 $it")
            marker = LatLng(it.lat, it.lng)

            var bitmap = when(it.category) {
                1 -> bitmapDescriptorFromVector(context, R.drawable.nature_parking)!!
                2 -> bitmapDescriptorFromVector(context, R.drawable.public_parking)!!
                3 -> bitmapDescriptorFromVector(context, R.drawable.private_parking)!!
                4 -> bitmapDescriptorFromVector(context, R.drawable.camping_parking)!!
                else -> {
                    bitmapDescriptorFromVector(context, R.drawable.camper_2)!!
                }
            }


//            val bitmap = if(it.category == 1){
//                Timber.i("Step4 nature")
//                bitmapDescriptorFromVector(context, R.drawable.nature_parking)
//            }else if(it.category == 2){
//                Timber.i("Step4 public")
//                bitmapDescriptorFromVector(context, R.drawable.public_parking)
//            }else if(it.category == 3){
//                Timber.i("Step4 private")
//                bitmapDescriptorFromVector(context, R.drawable.private_parking)
//            }else if(it.category == 4){
//                Timber.i("Step4 camping")
//                bitmapDescriptorFromVector(context, R.drawable.camping_parking)
//            }else{
//                Timber.i("Step4 else")
//                bitmapDescriptorFromVector(context, R.drawable.camper_2)
//            }

            Timber.i("Step5")
            val options = MarkerOptions().title(marker.toString()).position(marker).icon(bitmap)
            Timber.i("Step6")
            map?.addMarker(options)?.tag = it.uid

        }


    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun loadCategoryImage(imageView: ImageView){
        when (_parking.value!!.category) {
            1 -> imageView.setImageResource(R.drawable.nature_parking)
            2 -> imageView.setImageResource(R.drawable.public_parking)
            3 -> imageView.setImageResource(R.drawable.private_parking)
            4 -> imageView.setImageResource(R.drawable.camping_parking)
            else -> {
                imageView.setImageResource(R.drawable.camper)
            }
        }
    }
    fun loadImage(imageView: ImageView){
        Timber.i("loadImage")
        if (_parking.value!!.image != ""){
            Picasso.get()
                .load(_parking.value!!.image)
                .resize(200, 200)
                .into(imageView)
        }
    }
    fun calculateRating(){
        Timber.i("calculateRating")
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
    fun doMarkerSelected(marker: Marker){
        Timber.i("doMarkerSelected")
        val tag = marker.tag as String
        FirebaseDBManager.findById(tag, _parking)
    }

}


