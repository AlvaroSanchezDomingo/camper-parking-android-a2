package ie.wit.parking.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.parking.R
import ie.wit.parking.adapters.ParkingAdapter
import ie.wit.parking.adapters.ParkingClickListener
import ie.wit.parking.databinding.FragmentListBinding
import ie.wit.parking.models.ParkingModel
import ie.wit.parking.ui.auth.LoggedInViewModel
import ie.wit.parking.utils.*
import timber.log.Timber

class ListFragment : Fragment(), ParkingClickListener {

    private var _fragBinding: FragmentListBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog
    private val listViewModel: ListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToEditFragment(null)
            findNavController().navigate(action)
        }
        showLoader(loader,"Downloading Parkings")
        listViewModel.observableParkingList.observe(viewLifecycleOwner, {
                parkings ->
            parkings?.let {
                render(parkings as ArrayList<ParkingModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })


        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting Parking")
                val adapter = fragBinding.recyclerView.adapter as ParkingAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                listViewModel.delete(listViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as ParkingModel).uid!!)
                hideLoader(loader)
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)


        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //onParkingClick(viewHolder.itemView.tag as ParkingModel)
                var parking = viewHolder.itemView.tag as ParkingModel
                Timber.i("parking.uid == ${parking.uid}")
                val action = ListFragmentDirections.actionListFragmentToEditFragment(parking.uid!!)
                findNavController().navigate(action)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)

        val item = menu.findItem(R.id.toggleDonations) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleDonations: SwitchCompat = item.actionView.findViewById(R.id.toggleButton)
        toggleDonations.isChecked = false

        toggleDonations.setOnCheckedChangeListener { buttonView, isChecked ->
            Timber.i("isChecked == $isChecked")
            if (isChecked) listViewModel.loadAll()
            else listViewModel.load()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.editFragment -> {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun render(donationsList: ArrayList<ParkingModel>) {
        fragBinding.recyclerView.adapter = ParkingAdapter(donationsList,this)
        if (donationsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.donationsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.donationsNotFound.visibility = View.GONE
        }
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Donations")
            listViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Downloading Donations")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                listViewModel.liveFirebaseUser.value = firebaseUser
                listViewModel.load()
            }
        })
        //hideLoader(loader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onParkingClick(parking: ParkingModel) {
        Timber.i("parking.uid == ${parking.uid}")
        val action = ListFragmentDirections.actionListFragmentToViewFragment(parking.uid!!)
        findNavController().navigate(action)
    }
}