package ie.wit.parking.main

import android.app.Application
import timber.log.Timber

class ParkingXApp : Application() {

    //lateinit var donationsStore: ParkingStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
      //  donationsStore = ParkingManager()
        Timber.i("Camper Parking Application Started")
    }
}