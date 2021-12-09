package ie.wit.parking.ui.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import ie.wit.parking.R
import ie.wit.parking.databinding.FragmentViewBinding
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.ui.auth.LoggedInViewModel
import timber.log.Timber


class ViewFragment : Fragment() {

    private var _fragBinding: FragmentViewBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val args by navArgs<ViewFragmentArgs>()
    private val fragBinding get() = _fragBinding!!
    private lateinit var viewViewModel: ViewViewModel


    var edit: Boolean = false

    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentViewBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        viewViewModel = ViewModelProvider(this).get(ViewViewModel::class.java)

        viewViewModel.observableParking.observe(viewLifecycleOwner, {
            renderParking()
        })

        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.getMapAsync{googleMap ->
            viewViewModel.doConfigureMap(googleMap)
            googleMap.setOnMapClickListener {
                Timber.i("Click on map")
            }
        }

        return root;
    }


    private fun renderParking() {
        viewViewModel.locationUpdate()
        fragBinding.viewvm = viewViewModel
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_view, menu)
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
        viewViewModel.getParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid)
    }

}