package ie.wit.parking.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.models.ParkingStore
import timber.log.Timber

object FirebaseDBManager : ParkingStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(donationsList: MutableLiveData<List<ParkingModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, donationsList: MutableLiveData<List<ParkingModel>>) {

        database.child("user-donations").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ParkingModel>()
                    val children = snapshot.children
                    children.forEach {
                        val donation = it.getValue(ParkingModel::class.java)
                        localList.add(donation!!)
                    }
                    database.child("user-donations").child(userid)
                        .removeEventListener(this)

                    donationsList.value = localList
                }
            })
    }

    override fun findById(userid: String, donationid: String, donation: MutableLiveData<ParkingModel>) {

        database.child("user-donations").child(userid)
            .child(donationid).get().addOnSuccessListener {
                donation.value = it.getValue(ParkingModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, donation: ParkingModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("donations").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        donation.uid = key
        val donationValues = donation.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/donations/$key"] = donationValues
        childAdd["/user-donations/$uid/$key"] = donationValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, donationid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/donations/$donationid"] = null
        childDelete["/user-donations/$userid/$donationid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, donationid: String, donation: ParkingModel) {

        val donationValues = donation.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["donations/$donationid"] = donationValues
        childUpdate["user-donations/$userid/$donationid"] = donationValues

        database.updateChildren(childUpdate)
    }
}