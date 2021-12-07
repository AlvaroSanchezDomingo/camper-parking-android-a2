package ie.wit.parking.ui.locationedit



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ie.wit.parking.R


class LocationEditFragment : Fragment() {
    private lateinit var LocationEditViewModel: LocationEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocationEditViewModel =
            ViewModelProvider(this).get(ie.wit.parking.ui.locationedit.LocationEditViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_location_edit, container, false)

        return root
    }
}