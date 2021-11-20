package com.gms.nasapi.network

import com.gms.nasapi.utils.NasaApiResponse

interface ApodCallBacks {
    fun onPrShow()
    fun onPrHide()
    fun onError(msg: String)
    fun onSuccess(data: NasaApiResponse)
    fun onSuccessList(list: List<NasaApiResponse>)
}