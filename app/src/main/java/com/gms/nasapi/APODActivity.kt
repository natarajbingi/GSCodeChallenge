package com.gms.nasapi

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gms.nasapi.adapter.ApodViewModel
import com.gms.nasapi.adapter.GmsAPODAdapter
import com.gms.nasapi.adapter.GmsAPODAdapter.GmssViewHolder.Companion.arrDate
import com.gms.nasapi.adapter.GmsAPODAdapter.GmssViewHolder.Companion.isFavList
import com.gms.nasapi.adapter.OnItemClickListener
import com.gms.nasapi.databinding.ActivityMainBinding
import com.gms.nasapi.network.ApodCallBacks
import com.gms.nasapi.utils.Constants
import com.gms.nasapi.utils.DatePicker
import com.gms.nasapi.utils.NasaApiResponse
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class APODActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, ApodCallBacks,
    OnItemClickListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ApodViewModel
    private lateinit var progressDialog: AlertDialog
    private lateinit var adapter: GmsAPODAdapter

    private var TAG = APODActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@APODActivity, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(ApodViewModel::class.java)
        viewModel.onViewAvailable(this)
        binding.apodViewModel = viewModel

        // LiveData needs the lifecycle owner
        binding.lifecycleOwner = this

        setProgressbar()

        binding.filterGet.setOnClickListener(this)
        binding.favListIt.setOnClickListener(this)
        binding.imgDateClickTo.setOnClickListener(this)
        binding.SorryTryAgainBut.setOnClickListener(this)


        adapter = GmsAPODAdapter(this)
        binding.gmsRecycleView.adapter = adapter
        viewModel.getLiveData().observe(this, {
            isFavList = false
            binding.favListIt.setImageDrawable(getDrawable(R.drawable.ic_star))
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        checkFavList()
    }

    private fun checkFavList() {

        val item = Constants.getSessionData(Constants.FAV, this)
        if (item != null && item.isNotEmpty()) {
            arrDate.clear()
            viewModel.mFavLiveData.clear()
            val json = JSONArray(item)
            for (i in 0 until (json.length())) {
                val dataObj = json.getJSONObject(i)
                arrDate.add(dataObj.optString("date"))
                viewModel.mFavLiveData.add(
                    NasaApiResponse(
                        dataObj.optString("copyright"),
                        dataObj.optString("date"),
                        dataObj.optString("explanation"),
                        dataObj.optString("hdurl"),
                        dataObj.optString("media_type"),
                        dataObj.optString("service_version"),
                        dataObj.optString("title"),
                        dataObj.optString("url"),
                    )
                )
            }
           // Log.d(TAG, "onCreate() items: ${viewModel.mFavLiveData}")
        }
    }

    private fun setProgressbar() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog)
        progressDialog = builder.create()
    }

    override fun onPrShow() {
        progressDialog.show();
    }

    override fun onPrHide() {
        progressDialog.dismiss();
    }

    override fun onError(msg: String) {
        progressDialog.dismiss();
        binding.gmsRecycleView.visibility = View.GONE
        binding.SorryTryAgainLayout.visibility = View.VISIBLE
        binding.SorryTryAgainText.text = msg
        Log.e(TAG, "onError: {$msg}")
    }

    override fun onSuccess(data: NasaApiResponse) {
        progressDialog.dismiss();
        binding.gmsRecycleView.visibility = View.VISIBLE
        binding.SorryTryAgainLayout.visibility = View.GONE
        // Log.e(TAG, "onSuccess: $data")
    }

    override fun onSuccessList(list: List<NasaApiResponse>) {
        progressDialog.dismiss();
        binding.gmsRecycleView.visibility = View.VISIBLE
        binding.SorryTryAgainLayout.visibility = View.GONE
        // Log.e(TAG, "onSuccessList: $list")
    }

    override fun onDateSet(
        p0: android.widget.DatePicker?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        viewModel.dateStrSelected.value = year.toString() +
                "-" + Constants.oneDigToTwo(monthOfYear + 1) +
                "-" + Constants.oneDigToTwo(dayOfMonth)
    }

    override fun onItemClick(item: NasaApiResponse, position: Int) {
        if (arrDate.contains(item.date)){
             Constants.toastAction(this, "Item is already in favorite, long press to remove item")
        } else {
            viewModel.mFavLiveData.add(item)
            Constants.setSessionData(Constants.FAV, viewModel.mFavLiveData, this)
            checkFavList()
            adapter.notifyDataSetChanged()
            Constants.toastAction(this, "Item added to favorites")
        }
    }

    override fun onItemLongClick(item: NasaApiResponse, position: Int) {
        Log.e(TAG, "onItemLongClick: ${item.date}")
        if (arrDate.contains(item.date)){
            Constants.alertDialogShowYesNo(this,
                "Are you sure want to remove from Favorites.?",
                { dialog, which ->
                    viewModel.mFavLiveData.remove(item)
                    Constants.setSessionData(Constants.FAV, viewModel.mFavLiveData, this)
                    checkFavList()
                    adapter.notifyDataSetChanged()
                    Constants.toastAction(this, "Item remoed from favorites")
                })
        } else {
            Constants.toastAction(this, "Item is not in favorites,")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.filter_get,
            R.id.SorryTryAgainBut -> {
                viewModel.setCallApi(binding.dateTypeSpinner.selectedItem.toString())
            }
            R.id.img_date_click_to -> {
                DatePicker().show(supportFragmentManager, "DATE PICK")
            }
            R.id.favListIt -> {
                if (viewModel.isFavList.value == false){
                    isFavList = true
                    adapter.submitList(viewModel.mFavLiveData)
                    binding.favListIt.setImageDrawable(getDrawable(R.drawable.ic_star_favo))
                    if (viewModel.mFavLiveData.isEmpty()){
                        Constants.toastAction(this, "No Items available in favorites.")
                    }
                } else {
                    isFavList = false
                    adapter.submitList(viewModel.getLiveData().value)
                    binding.favListIt.setImageDrawable(getDrawable(R.drawable.ic_star))
                }
                adapter.notifyDataSetChanged()
                viewModel.isFavList.value = !viewModel.isFavList.value!!

            }
        }
    }

}