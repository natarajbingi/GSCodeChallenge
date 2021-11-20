package com.gms.nasapi.network

import android.content.Context
import android.util.Log
import com.gms.nasapi.utils.Constants
import com.gms.nasapi.utils.NasaApiResponse
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RestFullService {
    companion object {

        private fun cache(context: Context): Cache {
            return Cache(
                File(context.cacheDir, "gmsCache"),
                Constants.cacheSize.toLong()
            )
        }

        private fun offlineInterceptor(context: Context): Interceptor {
            return Interceptor { chain ->
                Log.d("TAG", "offline interceptor: called.")
                var request = chain.request()
                if (!Constants.isOnline(context)) {
                    val cacheControl = CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()
                    request = request.newBuilder()
                        .removeHeader(Constants.HEADER_PRAGMA)
                        .removeHeader(Constants.HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build()
                }
                chain.proceed(request)
            }
        }

        private fun networkInterceptor(): Interceptor {
            return Interceptor { chain ->
                Log.d("TAG", "network interceptor: called.")
                val response = chain.proceed(chain.request())
                val cacheControl = CacheControl.Builder()
                    .maxAge(5, TimeUnit.SECONDS)
                    .build()
                response.newBuilder()
                    .removeHeader(Constants.HEADER_PRAGMA)
                    .removeHeader(Constants.HEADER_CACHE_CONTROL)
                    .header(Constants.HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build()
            }
        }

        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d(
                    "TAG",
                    "log: http log: $message"
                )
            }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return httpLoggingInterceptor
        }

        private fun getClient(context: Context): APIServices {
            val clientWith60sTimeout = OkHttpClient()
                .newBuilder()
                .cache(cache(context))
                .addInterceptor(httpLoggingInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(offlineInterceptor(context))
                .addNetworkInterceptor(networkInterceptor())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientWith60sTimeout)
                .build()
            return retrofit.create(APIServices::class.java)
        }

        suspend fun getNasaAPODdataList(context: Context, start_date: String, callBacks: ApodCallBacks) {
            getClient(context).getNasaAPODdataList(start_date).enqueue(object: Callback<List<NasaApiResponse>> {
                override fun onResponse( call: Call<List<NasaApiResponse>>, response: Response<List<NasaApiResponse>>) {
                    Constants.logPrint(call.request().toString(),null,response)
                    callBacks.onSuccessList(response.body() as List<NasaApiResponse>)
                }

                override fun onFailure(call: Call<List<NasaApiResponse>>, t: Throwable) {
                    callBacks.onError("Failed Please try again.")
                    Constants.logPrint(call.request().toString(),null,t.localizedMessage)
                }

            })
        }
        suspend fun getNasaAPODdataDate(context: Context, date: String,callBacks: ApodCallBacks) {
            getClient(context).getNasaAPODdataDate( date).enqueue(object: Callback<NasaApiResponse> {
                override fun onResponse( call: Call<NasaApiResponse>, response: Response<NasaApiResponse>) {
                    Constants.logPrint(call.request().toString(),null,response)
                    callBacks.onSuccess(response.body() as NasaApiResponse)
                }

                override fun onFailure(call: Call<NasaApiResponse>, t: Throwable) {
                    callBacks.onError("Failed Please try again.")
                    Constants.logPrint(call.request().toString(),null,t.localizedMessage)
                }

            })
        }
    }
}