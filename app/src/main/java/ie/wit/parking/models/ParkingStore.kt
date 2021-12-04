package ie.wit.parking.models

import androidx.lifecycle.MutableLiveData

interface ParkingStore {
    fun findAll(donationsList:
                MutableLiveData<List<ParkingModel>>)
    fun findAll(email:String,
                donationsList:
                MutableLiveData<List<ParkingModel>>)
    fun findById(email:String, id: String,
                 donation: MutableLiveData<ParkingModel>)
    fun create(donation: ParkingModel)
    fun delete(email:String,id: String)
    fun update(email:String,id: String,donation: ParkingModel)
}

