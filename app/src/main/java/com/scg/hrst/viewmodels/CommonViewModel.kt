package com.scg.hrst.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scg.hrst.data.models.AgentModel
import com.scg.hrst.data.models.BankDetails
import com.scg.hrst.repository.Repository
import okhttp3.RequestBody

class CommonViewModel {

    private var repository: Repository?=null

    internal var districtListLiveData : LiveData<ArrayList<String>>? = null

    var bankDetailsLiveData : MutableLiveData<BankDetails>? = null


    init {
        repository = Repository()
        districtListLiveData = MutableLiveData()
        bankDetailsLiveData = MutableLiveData()
    }

    fun getDistrictList(state: String){
        districtListLiveData = repository?.getDistrictList(state)
    }


    fun getBankDetails(Ifsc : String){
        bankDetailsLiveData = repository?.getBankDetails(Ifsc)
    }

}