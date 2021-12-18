package ie.wit.parking.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ParkingStore {
    fun findAll(parkingsList:
                MutableLiveData<List<ParkingModel>>)
    fun findAll(userid:String,
                parkingsList:
                MutableLiveData<List<ParkingModel>>)
    fun findById(userid:String, parkingid: String,
                 parking: MutableLiveData<ParkingModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, parking: ParkingModel, context: Context)
    fun delete(userid:String,parkingid: String)
    fun update(userid:String,parkingid: String,parking: ParkingModel, context: Context, imageChanged: Boolean)
}

