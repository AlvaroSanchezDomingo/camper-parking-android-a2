package ie.wit.parking.main

import android.app.Application
import ie.wit.parking.firebase.FirebaseDBManager
import ie.wit.parking.models.ParkingStore
import timber.log.Timber

class ParkingXApp : Application() {

    //lateinit var ParkingStore: ParkingStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        //ParkingStore = FirebaseDBManager(applicationContext)
        Timber.i("Camper Parking Application Started")
    }
}