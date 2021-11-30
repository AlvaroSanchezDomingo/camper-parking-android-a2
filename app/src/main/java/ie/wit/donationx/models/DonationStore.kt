package ie.wit.donationx.models

import androidx.lifecycle.MutableLiveData

interface DonationStore {
    fun findAll(donationsList:
                MutableLiveData<List<DonationModel>>)
    fun findAll(email:String,
                donationsList:
                MutableLiveData<List<DonationModel>>)
    fun findById(email:String, id: String,
                 donation: MutableLiveData<DonationModel>)
    fun create(donation: DonationModel)
    fun delete(email:String,id: String)
    fun update(email:String,id: String,donation: DonationModel)
}

