package ie.wit.parking.ui.locationedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.parking.models.Location


class LocationEditViewModel : ViewModel() {
    private val _location = MutableLiveData<Location>()

    var observableLocation: LiveData<Location>
        get() = _location
        set(value) {_location.value = value.value}

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

}