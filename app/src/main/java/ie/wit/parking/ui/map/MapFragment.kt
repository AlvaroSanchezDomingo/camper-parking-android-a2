package ie.wit.parking.ui.map

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import ie.wit.parking.R
import ie.wit.parking.databinding.FragmentMapBinding
import ie.wit.parking.ui.auth.LoggedInViewModel
import timber.log.Timber


class MapFragment : Fragment() , OnMapReadyCallback {

    private var _fragBinding: FragmentMapBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var mapViewModel: MapViewModel


    var edit: Boolean = false
    var mapReady: Boolean = false
    var locationReady: Boolean = false

    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentMapBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        mapViewModel.observableParkings.observe(viewLifecycleOwner, {
            renderParkings()
        })
        mapViewModel.observableParking.observe(viewLifecycleOwner, {
            renderParking()
        })
        mapViewModel.observableRating.observe(viewLifecycleOwner, {
            renderRating()
        })


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
        mapViewModel.doConfigureMap(m)
        mapReady = true
        locationUpdate()

    }
    private fun locationUpdate(){
        if(mapReady && locationReady){
            Timber.i("MAP AND LOCATION READY $mapReady $locationReady")
            mapViewModel.mapLocationUpdate(requireContext())
        }
    }
    private fun renderParkings() {
        fragBinding.mapvm = mapViewModel
        Timber.i("ON LOCATION READY")
        locationReady = true
        locationUpdate()
    }
    private fun renderParking() {
        fragBinding.mapvm = mapViewModel
        mapViewModel.loadCategoryImage(fragBinding.categoryIcon)
        mapViewModel.loadImage(fragBinding.imageIcon)
        mapViewModel.calculateRating()
    }
    private fun renderRating() {
        fragBinding.mapvm = mapViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)

        val item = menu.findItem(R.id.toggleUserParkings) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleDonations: SwitchCompat = item.actionView.findViewById(R.id.toggleButton)
        toggleDonations.isChecked = false

        toggleDonations.setOnCheckedChangeListener { buttonView, isChecked ->
            Timber.i("isChecked == $isChecked")
            if (isChecked) mapViewModel.loadAll()
            else mapViewModel.load(loggedInViewModel.liveFirebaseUser.value?.email!!)
        }

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
        mapViewModel.load(loggedInViewModel.liveFirebaseUser.value?.email!!)
    }

//    override fun onMarkerClick(marker: Marker):Boolean {
//        Timber.i("onMarkerClick")
//        mapViewModel.doMarkerSelected(marker)
//        mapViewModel.loadCategoryImage(fragBinding.categoryIcon)
//        mapViewModel.loadImage(fragBinding.imageIcon)
//        return true
//    }

}