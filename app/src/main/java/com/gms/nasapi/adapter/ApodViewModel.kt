package com.gms.nasapi.adapter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gms.nasapi.network.ApodCallBacks
import com.gms.nasapi.network.RestFullService
import com.gms.nasapi.utils.Constants
import com.gms.nasapi.utils.NasaApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApodViewModel(application: Application) : AndroidViewModel(application), ApodCallBacks {

    private lateinit var view: ApodCallBacks
    private var applica: Application = application
    private var mDataSetLiveData: MutableLiveData<List<NasaApiResponse>> = MutableLiveData()
    var mDataSet: MutableList<NasaApiResponse> = mutableListOf()
    var dateStrSelected: MutableLiveData<String> = MutableLiveData()

    fun onViewAvailable(view: ApodCallBacks) {
        this.view = view
    }

    fun onGetApodPictures(start_date: String, date: String) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            if (start_date.isEmpty())
                RestFullService.getNasaAPODdataDate(applica, date, this@ApodViewModel)
            else
                RestFullService.getNasaAPODdataList(applica, start_date, this@ApodViewModel)
        }
    }

    fun getLiveData(): LiveData<List<NasaApiResponse>> {
        return mDataSetLiveData
    }

    override fun onPrShow() {
        view.onPrShow()
    }

    override fun onPrHide() {
        view.onPrHide()
    }

    override fun onError(msg: String) {
        view.onPrHide()
        view.onError(msg)
    }

    override fun onSuccess(data: NasaApiResponse) {
        view.onPrHide()
        mDataSet.clear()
        mDataSet.add(data)
        mDataSetLiveData.postValue(mDataSet)

        view.onSuccess(data)
    }

    override fun onSuccessList(list: List<NasaApiResponse>) {
        view.onPrHide()
        mDataSet.clear()
        mDataSet.addAll(list)
        mDataSetLiveData.postValue(mDataSet)
        view.onSuccessList(list)
    }

    fun setCallApi(strDateType: String) {
        if (dateStrSelected.value == null || dateStrSelected.value!!.isEmpty()) {
            Constants.toastAction(applica, "Please select the date to proceed.")
        } else {
            val startDate = if (strDateType == "From Day") dateStrSelected.value else ""
            val date = if (strDateType == "Day") dateStrSelected.value else ""
            if (startDate != null && date != null) {
                onGetApodPictures(startDate, date)
            }
        }
    }


}