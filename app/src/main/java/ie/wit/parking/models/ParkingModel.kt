package ie.wit.parking.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ParkingModel(
      var uid: String? = "",
      var title: String = "N/A",
      var description: String = "N/A",
      var category: String = "N/A",
      var email: String? = "joe@bloggs.com")
      : Parcelable

{
      @Exclude
      fun toMap(): Map<String, Any?> {
            return mapOf(
                  "uid" to uid,
                  "title" to title,
                  "description" to description,
                  "category" to category,
                  "email" to email
            )
      }
}



