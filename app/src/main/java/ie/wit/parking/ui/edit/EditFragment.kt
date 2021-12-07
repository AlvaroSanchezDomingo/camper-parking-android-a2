package ie.wit.parking.ui.edit

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.parking.R
import ie.wit.parking.databinding.FragmentEditBinding
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.ui.auth.LoggedInViewModel
import timber.log.Timber


class EditFragment : Fragment() {

    private var _fragBinding: FragmentEditBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val args by navArgs<EditFragmentArgs>()
    private val fragBinding get() = _fragBinding!!
    private lateinit var editViewModel: EditViewModel

    var edit: Boolean = false

    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentEditBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        editViewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        editViewModel.observableParking.observe(viewLifecycleOwner, Observer { render() })
        editViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })
        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.getMapAsync(callback)

        setButtonListener(fragBinding)
        return root;
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val lat = 37.3
        val lng = -5.98
        val zoom = 15f
        googleMap.clear()
        googleMap.uiSettings.setZoomControlsEnabled(true)
        val marker = LatLng(lat, lng)
        val options = MarkerOptions().title(marker.toString()).position(marker)
        googleMap.addMarker(options)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, zoom))
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
    private fun render() {
        fragBinding.parkingvm = editViewModel
        Timber.i("Retrofit fragBinding.parkingvm == $fragBinding.parkingvm")
    }

    fun setButtonListener(layout: FragmentEditBinding) {
        layout.saveButton.setOnClickListener {
            val category = when (layout.Category.checkedRadioButtonId) {
                R.id.Nature -> "Nature"
                R.id.Public -> "Public"
                R.id.Private -> "Private"
                R.id.Camping -> "Camping"
                else -> "N/A"
            }
            val title = layout.title.text.toString()
            val description = layout.description.text.toString()
                editViewModel.addParking(loggedInViewModel.liveFirebaseUser,
                    ParkingModel(title = title, description = description, category = category,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!))
            }
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
        Timber.i("###Fragment args.parkingid == ${args.parkingid}")
        Timber.i("###Fragment args.location == ${args.location}")
        editViewModel.getParking(loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.parkingid)
    }

}