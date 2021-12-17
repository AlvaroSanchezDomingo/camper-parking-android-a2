package ie.wit.parking.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ParkingModel(
      var uid: String? = "",
      var title: String = "",
      var description: String = "",
      var category: Int = 1,
      var email: String? = "joe@bloggs.com",
      var image: String = "",
      var lat : Double = 0.0,
      var lng: Double = 0.0,
      var zoom: Float = 0f)
      : Parcelable


{
      @Exclude
      fun toMap(): Map<String, Any?> {
            return mapOf(
                  "uid" to uid,
                  "title" to title,
                  "description" to description,
                  "category" to category,
                  "email" to email,
                  "image" to image,
                  "lat" to lat,
                  "lng" to lng,
                  "zoom" to zoom
            )
      }
}

@Parcelize
data class Location(  var lat: Double = 0.0,
                      var lng: Double = 0.0,
                      var zoom: Float = 0f) : Parcelable


