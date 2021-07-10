package com.scg.hrst.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scg.hrst.data.models.AgentModel
import com.scg.hrst.data.models.BankDetails
import com.scg.hrst.repository.Repository
import okhttp3.RequestBody

class AgentViewModel {

    private var repository: Repository?=null

    private var agentDetailsLiveData : LiveData<AgentModel>? = null

    private var agentRegResponseLiveData : LiveData<AgentModel>? = null

    internal var bankDetailsLiveData : MutableLiveData<BankDetails>? = null


    init {
        repository = Repository()
        agentDetailsLiveData = MutableLiveData()
        agentRegResponseLiveData = MutableLiveData()
        bankDetailsLiveData = MutableLiveData()
    }

    fun getAgentDetails(){
        agentDetailsLiveData = repository?.getAgentDetails()
    }


    fun registerAgent() {
        agentRegResponseLiveData = repository?.registerAgent()
    }

    fun getBankDetails(Ifsc : String){
        bankDetailsLiveData = repository?.getBankDetails(Ifsc)
    }

}