package ca.burchill.cointracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.burchill.cointracker.database.getDatabase
import ca.burchill.cointracker.network.CoinApi
import ca.burchill.cointracker.network.CoinApiResponse
import ca.burchill.cointracker.network.NetworkCoin
import ca.burchill.cointracker.repository.CoinsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await


enum class CoinApiStatus { LOADING, ERROR, DONE }


class CoinListViewModel(application: Application) : AndroidViewModel(application) {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<CoinApiStatus>()
    val status: LiveData<CoinApiStatus>
        get() = _status


    //private val _coins = MutableLiveData<List<NetworkCoin>>()
    //val coins: LiveData<List<NetworkCoin>>
        //get() = _coins



    // or use viewModelScope
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val coinsRepository = CoinsRepository(getDatabase(application))

    val coins = coinsRepository.coins


    init {
        //getCoins()
        getCoinsFromRepo()
    }

    private fun getCoinsFromRepo() {

        coroutineScope.launch {
            try {
                coinsRepository.refreshCoins()
                _status.value = CoinApiStatus.LOADING

//                var coinResult = CoinApi.retrofitService.getCoins()
//                if (coinResult.coins.size > 0) {
//                    _coins.value = coinResult.coins
//                }
            } catch (t: Throwable) {
                _status.value = CoinApiStatus.ERROR
            }
        }
    }

    private fun getCoins() {

       coroutineScope.launch {
            try {
                var coinResult = CoinApi.retrofitService.getCoins()
                if (coinResult.coins.size > 0) {
                    //_coins.value = coinResult.coins
                }
            } catch (t: Throwable) {
               _status.value = CoinApiStatus.ERROR
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}