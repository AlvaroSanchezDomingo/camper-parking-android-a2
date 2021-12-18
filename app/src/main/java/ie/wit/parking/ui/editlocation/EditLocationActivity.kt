package ie.wit.parking.ui.editlocation


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import ie.wit.parking.databinding.ActivityMapsBinding
import ie.wit.parking.models.Location
import ie.wit.parking.ui.auth.LoginRegisterViewModel
import ie.wit.parking.ui.home.Home
import timber.log.Timber


class EditLocationActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener
{

    private lateinit var editLocationViewModel : EditLocationViewModel
    private lateinit var binding : ActivityMapsBinding


    private lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editLocationViewModel = ViewModelProvider(this).get(EditLocationViewModel::class.java)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume();
        binding.mapView.getMapAsync(this)

        editLocationViewModel.observableLocation.observe(this, {
            renderLocation()
        })
        editLocationViewModel.setLocation(this.intent.extras?.getParcelable<Location>("location")!!)

    }

    private fun renderLocation() {
        binding.editlocvm = editLocationViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Timber.i("MAP READY")
        map = googleMap
        editLocationViewModel.initMap(googleMap)
        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerDragStart(marker: Marker) {
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        editLocationViewModel.doUpdateLocation(marker.position.latitude,marker.position.longitude, map.cameraPosition.zoom)
        renderLocation()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        editLocationViewModel.doUpdateMarker(marker)
        return false
    }

    override fun onBackPressed() {
        editLocationViewModel.doOnBackPressed(this)

    }
}