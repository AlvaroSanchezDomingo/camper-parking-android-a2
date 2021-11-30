package ie.wit.donationx.ui.donate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.donationx.models.DonationManager
import ie.wit.donationx.models.DonationModel

class DonateViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addDonation(donation: DonationModel) {
        status.value = try {
            DonationManager.create(donation)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}