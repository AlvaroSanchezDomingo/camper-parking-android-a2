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
        editViewModel.observableParking.observe(viewLifecycleOwner, {
            renderParking(it)
        })
        editViewModel.observableStatus.observe(viewLifecycleOwner, {
                status -> status?.let { render(status) }
        })

        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.getMapAsync{googleMap ->
            editViewModel.doConfigureMap(googleMap)
            googleMap.setOnMapClickListener {
                Timber.i("Click on map")
            }
        }

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
                editViewModel.editParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid!!,fragBinding.parkingvm?.observableParking!!.value!!)
            }else{
                editViewModel.addParking(loggedInViewModel.liveFirebaseUser,
                    ParkingModel(title = title, description = description, category = category,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!, lat = lat, lng = lng))
            }

        }

        return root;
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
    private fun renderParking(parking: ParkingModel) {
        Timber.i("Render parking $parking")
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
        edit = args.parkingid != null
        editViewModel.getParking(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.parkingid)
    }

}