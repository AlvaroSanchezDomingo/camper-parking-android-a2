package ie.wit.parking.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingModel
import timber.log.Timber

class EditViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()
    private val parking = MutableLiveData<ParkingModel>()

    var observableParking: LiveData<ParkingModel>
        get() = parking
        set(value) {parking.value = value.value}


    val observableStatus: LiveData<Boolean>
        get() = status

    fun addParking(firebaseUser: MutableLiveData<FirebaseUser>, parking: ParkingModel) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser,parking)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun getParking(userid:String, id: String?) {
        try {
            FirebaseDBManager.findById(userid, id!!, parking)
            Timber.i("Detail getParking() Success : ${
                parking.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : $e.message")
        }
    }
}