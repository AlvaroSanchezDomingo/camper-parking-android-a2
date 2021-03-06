package ie.wit.parking.ui.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.squareup.picasso.Picasso
import ie.wit.parking.R
import ie.wit.parking.databinding.FragmentEditBinding
import ie.wit.parking.helpers.checkLocationPermissions
import ie.wit.parking.helpers.createDefaultLocationRequest
import ie.wit.parking.models.Location
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.ui.auth.LoggedInViewModel
import ie.wit.parking.ui.editlocation.EditLocationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class EditFragment : Fragment() , OnMapReadyCallback {

    private var _fragBinding: FragmentEditBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val args by navArgs<EditFragmentArgs>()
    private val fragBinding get() = _fragBinding!!
    private lateinit var editViewModel: EditViewModel
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var locationService : FusedLocationProviderClient
    private val locationRequest = createDefaultLocationRequest()
    var edit: Boolean = false
    var mapReady : Boolean = false
    var locationReady : Boolean = false
    var locationEdited : Boolean = false
    var imageChanged : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        locationService = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        registerImagePickerCallback()
        registerMapCallback()
        doPermissionLauncher()

        _fragBinding = FragmentEditBinding.inflate(inflater, container, false)
        val root = fragBinding.root


        editViewModel = ViewModelProvider(this).get(EditViewModel::class.java)

        editViewModel.observableParking.observe(viewLifecycleOwner, {
            renderParking(it)
        })

        editViewModel.observableStatus.observe(viewLifecycleOwner, {
                status -> status?.let { render(status) }
        })

        edit = args.parkingid != null && args.parkingid != "null"
        if(edit){
            Timber.i("EDIT ${args.parkingid}")
            editViewModel.getParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid)
        }else{
            Timber.i("NEW")
            editViewModel.setDefaultParking(loggedInViewModel.liveFirebaseUser.value?.uid!!)
            if (checkLocationPermissions(requireActivity())) {
                doSetCurrentLocation()
            }
        }

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.onResume();
        fragBinding.mapView.getMapAsync(this)
    }

    override fun onMapReady(m: GoogleMap) {
        Timber.i("ON MAP READY")
        editViewModel.doConfigureMap(m)
        mapReady = true
        locationUpdate()

    }
    private fun locationUpdate(){
        if(mapReady && locationReady){
            Timber.i("MAP AND LOCATION READY $mapReady $locationReady")
            editViewModel.mapLocationUpdate()
        }
    }
    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.parkingError),Toast.LENGTH_LONG).show()
        }
    }

    private fun renderParking(parking:ParkingModel) {
        fragBinding.parkingvm = editViewModel

        if(edit){
            Timber.i("ON LOCATION READY")
            locationReady = true
            locationUpdate()
            if(parking.image != ""){
                editViewModel.loadImage(parking.image, fragBinding.parkingImage)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.listFragment -> {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
            R.id.mapFragment -> {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
            R.id.save -> {
                if(edit){
                    Timber.i("EDIT PARKING ${fragBinding.parkingvm?.observableParking!!.value!!}")
                    editViewModel.editParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid!!,fragBinding.parkingvm?.observableParking!!.value!!, requireContext(), imageChanged)
                }else{
                    editViewModel.addParking(loggedInViewModel.liveFirebaseUser, requireContext())
                }
                true
            }

            R.id.location -> {
                var location = editViewModel.getLocation()
                val launcherIntent = Intent(activity, EditLocationActivity::class.java).putExtra("location", location)
                mapIntentLauncher.launch(launcherIntent)
                true
            }
            R.id.image -> {
                editViewModel.doSelectImage(imageIntentLauncher)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        doRestartLocationUpdates()

    }
    @SuppressLint("MissingPermission")
    fun doRestartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && !locationEdited) {
                    val l = locationResult.locations.last()
                    val location = Location()
                    location.lat = l.latitude
                    location.lng = l.longitude
                    location.zoom = 15f
                    editViewModel.setParkingLocation(location)
                    Timber.i("LOCATION READY")
                    locationReady = true
                    locationUpdate()
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            val location = Location()
            location.lat = it.latitude
            location.lng = it.longitude
            location.zoom = 15f
            Timber.i("CURRENT LOCATION $location")
            editViewModel.setParkingLocation(location)
            Timber.i("LOCATION READY")
            locationReady = true
            locationUpdate()
        }
    }
    private fun doPermissionLauncher() {
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    doSetCurrentLocation()
                } else {
                    val location = Location()
                    location.lat = 40.0
                    location.lng = -10.0
                    location.zoom = 15f
                    editViewModel.setParkingLocation(location)
                    Timber.i("LOCATION READY")
                    locationReady = true
                    locationUpdate()
                }
            }
    }

    private fun registerImagePickerCallback() {

        imageIntentLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Image Result ${result.data!!.data.toString()}")
                            editViewModel.setImage(result.data!!.data.toString())
                            imageChanged = true
                            Picasso.get()
                                .load(result.data!!.data!!.toString())
                                .resize(200, 200)
                                .into(fragBinding.parkingImage)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location EXTRA $location")
                            editViewModel.setParkingLocation(location)
                            Timber.i("LOCATION READY")
                            locationEdited = true
                            locationReady = true
                            locationUpdate()
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }


}

