package com.scg.hrst.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.scg.hrst.data.api.ApiClient
import com.scg.hrst.data.api.ApiInterface
import com.scg.hrst.data.models.AgentModel
import com.scg.hrst.data.models.BankDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {


    fun registerAgent(): MutableLiveData<AgentModel> {
        var responseData = MutableLiveData<AgentModel>()


        return responseData
    }


    fun getAgentDetails(): MutableLiveData<AgentModel> {
        var responseData = MutableLiveData<AgentModel>()


        return responseData
    }


    fun getBankDetails(Ifsc: String): MutableLiveData<BankDetails> {

        var responseData = MutableLiveData<BankDetails>()

        val apiInterface = ApiClient.getBankApiClient().create(ApiInterface::class.java)


        val call: Call<BankDetails> = apiInterface.getBankDetails(Ifsc)

        call.enqueue(object : Callback<BankDetails> {

            override fun onResponse(
                call: Call<BankDetails>,
                response: retrofit2.Response<BankDetails>
            ) {
                val res = response.body()
                if (response.isSuccessful && res != null) {
//                        TransResponse.value = res

                    responseData.value = response.body()
                    Log.e("BankRepo", res.toString())
                } else {
//                        TransResponse.value = null
//                        responseData.value = null
//                    responseData.value = null

                    responseData.value = BankDetails(
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        null.toString(),
                        true,
                        null.toString(),
                        true,
                        true,
                        null.toString(),
                        null.toString(),
                        true
                    )

                    Log.e("BankRepo", "Error Occurred while getting Bank Details")
                    Log.e("BankRepo", "${response.errorBody()}")
                    Log.e("BankRepo", "${response.code()}")
                    Log.e("BankRepo", response.message())
                    Log.e("BankRepo", response.headers().toString())
                    Log.e("BankRepo", "$res")

                }
            }

            override fun onFailure(call: Call<BankDetails>, t: Throwable) {

                responseData.value = BankDetails(
                    "null","null",
                    "null", "null","null","null","null","null","null",
                    false,
                    "null",
                    false,
                    RTGS = false,
                    STATE = "null",
                    SWIFT = "null",
                    UPI = false
                )

                Log.e("BankRepo", "Posting Data Failed : ${t.localizedMessage}")
                Log.e("BankRepo", "Error : ${t.message}")
            }
        })


        return responseData
    }


    fun getDistrictList(state: String): MutableLiveData<ArrayList<String>>{

        val data = MutableLiveData<ArrayList<String>>()

        val apiInterface = ApiClient.getFuelApiClient().create(ApiInterface::class.java)

        val call: Call<ArrayList<String>> = apiInterface.getDistrict(state)


        call.enqueue(object : Callback<ArrayList<String>> {

            override fun onResponse(
                call: Call<ArrayList<String>>,
                response: Response<ArrayList<String>>
            ) {
                val res = response.body()
                if (response.code() == 200 && res != null) {

                    data.value = response.body()
                    Log.e("District", res.toString())

                } else {

                    Log.e("District", "Some Error Occurred")
                    Log.e("District", "Some Error Occurred While Registering Truck Owner")
                    Log.e("District", "${response.errorBody()?.string()}")
                    Log.e("District", "${response.code()}")
                    Log.e("District", response.message())
                    Log.e("District", response.headers().toString())
                    Log.e("District", "$res")

                }
            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {

                Log.e("District", "Posting Data Failed : ${t.localizedMessage}")
                Log.e("District", "Error : ${t.message}")
            }
        })


        return data
    }


}