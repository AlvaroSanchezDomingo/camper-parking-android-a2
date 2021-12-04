package ie.wit.parking.models

import androidx.lifecycle.MutableLiveData
import ie.wit.parking.api.ParkingClient
import ie.wit.parking.api.ParkingWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object ParkingManager : ParkingStore {

    override fun findAll(donationsList: MutableLiveData<List<ParkingModel>>) {

        val call = ParkingClient.getApi().findall()

        call.enqueue(object : Callback<List<ParkingModel>> {
            override fun onResponse(call: Call<List<ParkingModel>>,
                                    response: Response<List<ParkingModel>>
            ) {
                donationsList.value = response.body() as ArrayList<ParkingModel>
                Timber.i("Retrofit findAll() = ${response.body()}")
            }

            override fun onFailure(call: Call<List<ParkingModel>>, t: Throwable) {
                Timber.i("Retrofit findAll() Error : $t.message")
            }
        })
    }

    override fun findAll(email: String, donationsList: MutableLiveData<List<ParkingModel>>) {

        val call = ParkingClient.getApi().findall(email)

        call.enqueue(object : Callback<List<ParkingModel>> {
            override fun onResponse(call: Call<List<ParkingModel>>,
                                    response: Response<List<ParkingModel>>
            ) {
                donationsList.value = response.body() as ArrayList<ParkingModel>
                Timber.i("Retrofit findAll() = ${response.body()}")
            }

            override fun onFailure(call: Call<List<ParkingModel>>, t: Throwable) {
                Timber.i("Retrofit findAll() Error : $t.message")
            }
        })
    }

    override fun findById(email: String, id: String, donation: MutableLiveData<ParkingModel>)   {

        val call = ParkingClient.getApi().get(email,id)

        call.enqueue(object : Callback<ParkingModel> {
            override fun onResponse(call: Call<ParkingModel>, response: Response<ParkingModel>) {
                donation.value = response.body() as ParkingModel
                Timber.i("Retrofit findById() = ${response.body()}")
            }

            override fun onFailure(call: Call<ParkingModel>, t: Throwable) {
                Timber.i("Retrofit findById() Error : $t.message")
            }
        })
    }

    override fun create( donation: ParkingModel) {

        val call = ParkingClient.getApi().post(donation.email,donation)
        Timber.i("Retrofit ${call.toString()}")

        call.enqueue(object : Callback<ParkingWrapper> {
            override fun onResponse(call: Call<ParkingWrapper>,
                                    response: Response<ParkingWrapper>
            ) {
                val donationWrapper = response.body()
                if (donationWrapper != null) {
                    Timber.i("Retrofit ${donationWrapper.message}")
                    Timber.i("Retrofit ${donationWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ParkingWrapper>, t: Throwable) {
                Timber.i("Retrofit Error : $t.message")
                Timber.i("Retrofit create Error : $t.message")
            }
        })
    }

    override fun delete(email: String,id: String) {

        val call = ParkingClient.getApi().delete(email,id)

        call.enqueue(object : Callback<ParkingWrapper> {
            override fun onResponse(call: Call<ParkingWrapper>,
                                    response: Response<ParkingWrapper>
            ) {
                val donationWrapper = response.body()
                if (donationWrapper != null) {
                    Timber.i("Retrofit Delete ${donationWrapper.message}")
                    Timber.i("Retrofit Delete ${donationWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ParkingWrapper>, t: Throwable) {
                Timber.i("Retrofit Delete Error : $t.message")
            }
        })
    }

    override fun update(email: String,id: String, donation: ParkingModel) {

        val call = ParkingClient.getApi().put(email,id,donation)

        call.enqueue(object : Callback<ParkingWrapper> {
            override fun onResponse(call: Call<ParkingWrapper>,
                                    response: Response<ParkingWrapper>
            ) {
                val donationWrapper = response.body()
                if (donationWrapper != null) {
                    Timber.i("Retrofit Update ${donationWrapper.message}")
                    Timber.i("Retrofit Update ${donationWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ParkingWrapper>, t: Throwable) {
                Timber.i("Retrofit Update Error : $t.message")
            }
        })
    }
}