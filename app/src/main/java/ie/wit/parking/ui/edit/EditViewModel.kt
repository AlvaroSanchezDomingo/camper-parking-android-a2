package ie.wit.parking.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.parking.models.ParkingManager
import ie.wit.parking.models.ParkingModel

class EditViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addDonation(donation: ParkingModel) {
        status.value = try {
            ParkingManager.create(donation)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}