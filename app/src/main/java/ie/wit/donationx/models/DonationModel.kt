package ie.wit.donationx.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class DonationModel(
      var _id: String = "N/A",
      @SerializedName("paymenttype")
      val paymentmethod: String = "N/A",
      var message: String = "n/a",
      var amount: Int = 0,
      var upvotes: Int = 0,
      var email: String = "joe@bloggs.com") : Parcelable



