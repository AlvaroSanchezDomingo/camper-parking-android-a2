package ie.wit.parking.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.models.ParkingStore
import timber.log.Timber

object FirebaseDBManager : ParkingStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(parkingsList: MutableLiveData<List<ParkingModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, parkingsList: MutableLiveData<List<ParkingModel>>) {

        database.child("user-parkings").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Parking error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ParkingModel>()
                    val children = snapshot.children
                    children.forEach {
                        val parking = it.getValue(ParkingModel::class.java)
                        localList.add(parking!!)
                    }
                    database.child("user-parkings").child(userid)
                        .removeEventListener(this)

                    parkingsList.value = localList
                }
            })
    }

    override fun findById(userid: String, parkingid: String, parking: MutableLiveData<ParkingModel>) {

        database.child("user-parkings").child(userid)
            .child(parkingid).get().addOnSuccessListener {
                parking.value = it.getValue(ParkingModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, parking: ParkingModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("parkings").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        Timber.i("Firebase DB create key : $key")
        parking.uid = key
        val parkingValues = parking.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/parkings/$key"] = parkingValues
        childAdd["/user-parkings/$uid/$key"] = parkingValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, parkingid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/parkings/$parkingid"] = null
        childDelete["/user-parkings/$userid/$parkingid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, parkingnid: String, parking: ParkingModel) {

        val parkingValues = parking.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["parkings/$parkingnid"] = parkingValues
        childUpdate["user-parkings/$userid/$parkingnid"] = parkingValues

        database.updateChildren(childUpdate)
    }
}