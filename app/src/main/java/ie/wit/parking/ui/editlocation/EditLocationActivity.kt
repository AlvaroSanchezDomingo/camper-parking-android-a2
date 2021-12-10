package ie.wit.parking.ui.editlocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import ie.wit.parking.databinding.ActivityMapsBinding
import ie.wit.parking.databinding.ContentLocationEditBinding
import ie.wit.parking.databinding.FragmentEditBinding
import ie.wit.parking.databinding.LoginBinding
import ie.wit.parking.models.Location
import ie.wit.parking.ui.auth.LoginRegisterViewModel
import ie.wit.parking.ui.home.Home


class EditLocationActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener
{

    private lateinit var editLocationViewModel : EditLocationViewModel
    private lateinit var binding : ActivityMapsBinding
    private lateinit var contentBinding : ContentLocationEditBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contentBinding = ContentLocationEditBinding.bind(binding.root)


        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{
            editLocationViewModel.initMap(it, this)
        }

        editLocationViewModel.observableLocation.observe(this, {
            renderLocation()
        })
        editLocationViewModel.initLocation(this.intent.extras?.getParcelable<Location>("location")!!)

    }
    private fun renderLocation() {
        contentBinding.editlocvm = editLocationViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        editLocationViewModel.initMap(googleMap, this)
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        editLocationViewModel.doUpdateLocation(marker.position.latitude,marker.position.longitude)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        editLocationViewModel.doUpdateMarker(marker)
        return false
    }

    override fun onBackPressed() {
        editLocationViewModel.doOnBackPressed(this)

    }
}