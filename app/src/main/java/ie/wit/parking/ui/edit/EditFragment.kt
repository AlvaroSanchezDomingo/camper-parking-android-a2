package ie.wit.parking.ui.edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import ie.wit.parking.R
import ie.wit.parking.databinding.FragmentEditBinding
import ie.wit.parking.helpers.checkLocationPermissions
import ie.wit.parking.helpers.createDefaultLocationRequest
import ie.wit.parking.models.Location
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.ui.auth.LoggedInViewModel
import ie.wit.parking.ui.editlocation.EditLocationActivity
import timber.log.Timber


class EditFragment : Fragment() {

    private var _fragBinding: FragmentEditBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val args by navArgs<EditFragmentArgs>()
    private val fragBinding get() = _fragBinding!!
    private lateinit var editViewModel: EditViewModel
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    private lateinit var locationService : FusedLocationProviderClient
    val locationRequest = createDefaultLocationRequest()
    var edit: Boolean = false

    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        locationService = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentEditBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        Timber.i("STEP 1")
        editViewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        editViewModel.observableParking.observe(viewLifecycleOwner, {
            renderParking()
        })
        editViewModel.observableStatus.observe(viewLifecycleOwner, {
                status -> status?.let { render(status) }
        })

        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.getMapAsync{googleMap ->
            editViewModel.doConfigureMap(googleMap)
            googleMap.setOnMapClickListener {
                var location = editViewModel.getLocation()
                val launcherIntent = Intent(activity, EditLocationActivity::class.java).putExtra("location", location)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        Timber.i("STEP 2")
        fragBinding.saveButton.setOnClickListener {
            val category = when (fragBinding.Category.checkedRadioButtonId) {
                R.id.Nature -> 1
                R.id.Public -> 2
                R.id.Private -> 3
                R.id.Camping -> 4
                else -> 1
            }
            val title = fragBinding.title.text.toString()
            val description = fragBinding.description.text.toString()
            val lat = fragBinding.lat.text.toString().toDouble()
            val lng = fragBinding.lng.text.toString().toDouble()

            if(edit){
                Timber.i("EDIT PARKING ${fragBinding.parkingvm?.observableParking!!.value!!}")
                editViewModel.editParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid!!,fragBinding.parkingvm?.observableParking!!.value!!)
            }else{
                editViewModel.addParking(loggedInViewModel.liveFirebaseUser,
                    ParkingModel(title = title, description = description, category = category,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!, lat = lat, lng = lng, zoom=15f))
            }

        }
        Timber.i("STEP 3")
        fragBinding.pickImage.setOnClickListener {
            editViewModel.doSelectImage(imageIntentLauncher)
        }
        Timber.i("STEP 4")
        doPermissionLauncher()
        Timber.i("STEP 5")
        edit = args.parkingid != null
        if(edit){
            editViewModel.getParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid)
        }else{
            Timber.i("STEP 6")
            editViewModel.setDefaultParking()
            if (checkLocationPermissions(requireActivity())) {
                Timber.i("STEP 7")
                doSetCurrentLocation()
            }
        }

        registerImagePickerCallback()
        registerMapCallback()
        Timber.i("STEP 8")
        return root;
    }

    @SuppressLint("MissingPermission")
    fun doRestartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    val location = Location()
                    location.lat = l.latitude
                    location.lng = l.longitude
                    location.zoom = 15f
                    editViewModel.setLocation(location)
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        Timber.i("setting location from doSetLocation")
        locationService.lastLocation.addOnSuccessListener {
            val location = Location()
            location.lat = it.latitude
            location.lng = it.longitude
            location.zoom = 15f
            Timber.i("current location $location")
            editViewModel.setLocation(location)
        }
    }
    private fun doPermissionLauncher() {
        Timber.i("permission check called")
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    Timber.i("permission check called is GRANTED")
                    doSetCurrentLocation()
                } else {
                    Timber.i("permission check called is NOT GRANTED")
                    val location = Location()
                    location.lat = 40.0
                    location.lng = -10.0
                    location.zoom = 15f
                    editViewModel.setLocation(location)
                    renderParking()
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
                            Timber.i("Got Result ${result.data!!.data}")
                            //editViewModel.setImage(result.data!!.data!!)
                            Timber.i("Image updated")
                            Picasso.get()
                                .load(result.data!!.data!!)
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
                            Timber.i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location == $location")
                            editViewModel.setLocation(location)
                            renderParking()
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

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
    private fun renderParking() {
        editViewModel.locationUpdate()
        fragBinding.parkingvm = editViewModel
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        doRestartLocationUpdates()
    }
}

