package com.gms.nasapi.adapter

import com.gms.nasapi.utils.NasaApiResponse

interface OnItemClickListener {
    fun onItemClick(item: NasaApiResponse, position: Int)

    fun onItemLongClick(item: NasaApiResponse, position: Int)
}