package ie.wit.parking.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingModel
import timber.log.Timber
import java.lang.Exception

class ListViewModel : ViewModel() {

    private val parkingList =
        MutableLiveData<List<ParkingModel>>()

    val observableParkingList: LiveData<List<ParkingModel>>
        get() = parkingList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!,
                parkingList)
            Timber.i("List Load Success : ${parkingList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("List Load Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid,id)
            Timber.i("List Delete Success")
        }
        catch (e: Exception) {
            Timber.i("List Delete Error : $e.message")
        }
    }
}

