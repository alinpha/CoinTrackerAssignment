package ca.burchill.cointracker.work

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CoinTrackerApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun setupRecurringWork() {
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    private fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
}