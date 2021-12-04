package ie.wit.parking.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.parking.models.ParkingManager
import ie.wit.parking.models.ParkingModel
import timber.log.Timber
import java.lang.Exception

class ListViewModel : ViewModel() {

    private val donationsList =
        MutableLiveData<List<ParkingModel>>()

    val observableDonationsList: LiveData<List<ParkingModel>>
        get() = donationsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            ParkingManager.findAll(liveFirebaseUser.value?.email!!, donationsList)
            Timber.i("Report Load Success : ${donationsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun delete(email: String, id: String) {
        try {
            ParkingManager.delete(email,id)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }
}

