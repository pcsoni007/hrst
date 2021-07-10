package com.scg.hrst.data.api

import com.scg.hrst.data.models.BankDetails
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

public interface ApiInterface {

    ///////////////////////  IFSC Code API Requests   /////////////////////////
    @GET("{Ifsc}")
    public fun getBankDetails(@Path("Ifsc") Ifsc : String): Call<BankDetails>




    @GET("{state}/districts/")
    public fun getDistrict(@Path("state") state : String): Call<ArrayList<String>>

//    @GET("price/{state}/{district}")
//    public fun getFuelPrice(@Path("state") state : String, @Path("district") district : String): Call<List<FuelPrice>>

}




