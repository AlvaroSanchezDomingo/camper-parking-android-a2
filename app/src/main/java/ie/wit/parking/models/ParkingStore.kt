package ie.wit.parking.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ParkingStore {
    fun findAll(donationsList:
                MutableLiveData<List<ParkingModel>>)
    fun findAll(userid:String,
                donationsList:
                MutableLiveData<List<ParkingModel>>)
    fun findById(userid:String, id: String,
                 donation: MutableLiveData<ParkingModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, donation: ParkingModel)
    fun delete(userid:String,id: String)
    fun update(userid:String,id: String,donation: ParkingModel)
}

