package ie.wit.donationx.api

import ie.wit.donationx.models.DonationModel
import retrofit2.Call
import retrofit2.http.*


interface DonationService {
    @GET("/donations")
    fun findall(): Call<List<DonationModel>>

    @GET("/donations/{email}")
    fun findall(@Path("email") email: String?)
            : Call<List<DonationModel>>

    @GET("/donations/{email}/{id}")
    fun get(@Path("email") email: String?,
            @Path("id") id: String): Call<DonationModel>

    @DELETE("/donations/{email}/{id}")
    fun delete(@Path("email") email: String?,
               @Path("id") id: String): Call<DonationWrapper>

    @POST("/donations/{email}")
    fun post(@Path("email") email: String?,
             @Body donation: DonationModel)
            : Call<DonationWrapper>

    @PUT("/donations/{email}/{id}")
    fun put(@Path("email") email: String?,
            @Path("id") id: String,
            @Body donation: DonationModel
    ): Call<DonationWrapper>
}

