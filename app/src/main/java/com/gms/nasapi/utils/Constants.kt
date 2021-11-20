package com.gms.nasapi.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gms.nasapi.R
import com.google.gson.Gson

class Constants {
    companion object {

        const val BASE_URL = "https://api.nasa.gov/planetary/"
        val NETWORK_TIMEOUT = 3000
        const val PerPage = 10
        const val cacheSize = 5 * 1024 * 1024 // 5 MB size


        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"


        fun setGilde(context: Context?, logoImagePath: String?, view: ImageView?) {
            Glide.with(context!!)
                .load(logoImagePath)
                .apply(
                    RequestOptions() //  .centerCrop()
                        //  .circleCrop()
                        .error(R.drawable.blankimge)
                        .placeholder(R.drawable.blankimge)
                )
                .into(view!!)
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    //It will check for both wifi and cellular network
                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
                return false
            } else {
                val netInfo = cm.activeNetworkInfo
                return netInfo != null && netInfo.isConnectedOrConnecting
            }
        }

        fun logPrint(call: String, req: Any?, res: Any?) {
            val g = Gson()
            Log.d("Request-", call + "")
            Log.d("LogReq-", g.toJson(req))
            Log.d("LogRes-", g.toJson(res))
        }


        fun toastAction(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }

        fun oneDigToTwo(value: Int): String {
            var dd = ""
            dd = if (value < 10) {
                "0$value"
            } else {
                value.toString() + ""
            }
            return dd
        }
    }
}