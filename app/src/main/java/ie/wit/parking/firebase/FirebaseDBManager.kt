package ie.wit.parking.firebase


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ie.wit.parking.helpers.readImageFromPath
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.models.ParkingStore
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import kotlin.system.measureTimeMillis


object FirebaseDBManager: ParkingStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var st: StorageReference = FirebaseStorage.getInstance().reference

    override fun findAll(parkingsList: MutableLiveData<List<ParkingModel>>) {
        database.child("parkings")
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
                    database.child("parkings")
                        .removeEventListener(this)

                    parkingsList.value = localList
                }
            })
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

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, parking: ParkingModel, context: Context) {
        Timber.i("Firebase DB Reference : $database")
        val uid = firebaseUser.value!!.uid
        val key = database.child("parkings").push().key
        Timber.i("Firebase DB create key : $key")
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        Timber.i("IMAGE STRING CREATE : ${parking.image}")
        parking.uid = key
        val parkingValues = parking.toMap()
        val childAdd = HashMap<String, Any>()
        childAdd["/parkings/$key"] = parkingValues
        childAdd["/user-parkings/$uid/$key"] = parkingValues
        database.updateChildren(childAdd)
        updateImage(parking.image, uid, key, context)
    }

    override fun delete(userid: String, parkingid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/parkings/$parkingid"] = null
        childDelete["/user-parkings/$userid/$parkingid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, parkingid: String, parking: ParkingModel, context: Context, imageChanged: Boolean) {

        val parkingValues = parking.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["parkings/$parkingid"] = parkingValues
        childUpdate["user-parkings/$userid/$parkingid"] = parkingValues

        database.updateChildren(childUpdate)
        if(imageChanged){
            updateImage(parking.image, userid, parkingid, context)
        }
    }

    private fun updateImage(imageStr: String, uid:String, key: String, context: Context){

        Timber.i("IMAGE STRING UPDATEIMAGE : ${imageStr}")
        if(imageStr != ""){
            var imageRef = st.child("$uid/$key")
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, imageStr)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()
            val uploadTask = imageRef.putBytes(data)
            Timber.i("updateImage before listener")
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Timber.i("updateImage uploaded")
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    Timber.i("updateImage downloadUrl")
                    var imageURL = it.toString()
                    Timber.i("IMAGE URL UPDATEIMAGE : $imageURL")
                    database.child("parkings").child(key).child("image").setValue(imageURL)
                    database.child("user-parkings").child(uid).child(key).child("image").setValue(imageURL)
                //updateImageRef(uid, key,imageURL)
                }
            }.addOnFailureListener{
                var errorMessage = it.message
                Timber.i("Failure: $errorMessage")
            }

        }

    }

    fun updateImageRef(userid: String,imageUri: String) {

        val userParkings = database.child("user-parkings").child(userid)
        val allParkings = database.child("parkings")

        userParkings.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        Timber.i("DataChange")
                        //Update Users imageUri
                        it.ref.child("image").setValue(imageUri)
                        //Update all donations that match 'it'
                        val parking = it.getValue(ParkingModel::class.java)
                        Timber.i("DataChange $parking" )
                        allParkings.child(parking!!.uid!!).child("image").setValue(imageUri)
                    }
                }
            })
    }
}