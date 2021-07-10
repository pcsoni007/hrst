package com.scg.hrst.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BankURL = "https://ifsc.razorpay.com/"
const val fuelURL = "https://fuelprice-api-india.herokuapp.com/"


class ApiClient {
    companion object{
        private var retrofit: Retrofit? =null
        private val interceptor = HttpLoggingInterceptor()



        public fun getBankApiClient(): Retrofit {
            /*val gson = GsonBuilder()
                .setLenient()
                .create()*/
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(BankURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            if (retrofit == null) {
            }
            return retrofit!!
        }

        public fun getFuelApiClient(): Retrofit {
            /*val gson = GsonBuilder()
                .setLenient()
                .create()*/
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(fuelURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            if (retrofit == null) {
            }
            return retrofit!!
        }


    }



}




