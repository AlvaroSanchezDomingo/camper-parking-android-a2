package ie.wit.parking.api

import ie.wit.parking.models.ParkingModel
import retrofit2.Call
import retrofit2.http.*


interface ParkingService {
    @GET("/donations")
    fun findall(): Call<List<ParkingModel>>

    @GET("/donations/{email}")
    fun findall(@Path("email") email: String?)
            : Call<List<ParkingModel>>

    @GET("/donations/{email}/{id}")
    fun get(@Path("email") email: String?,
            @Path("id") id: String): Call<ParkingModel>

    @DELETE("/donations/{email}/{id}")
    fun delete(@Path("email") email: String?,
               @Path("id") id: String): Call<ParkingWrapper>

    @POST("/donations/{email}")
    fun post(@Path("email") email: String?,
             @Body donation: ParkingModel)
            : Call<ParkingWrapper>

    @PUT("/donations/{email}/{id}")
    fun put(@Path("email") email: String?,
            @Path("id") id: String,
            @Body donation: ParkingModel
    ): Call<ParkingWrapper>
}

