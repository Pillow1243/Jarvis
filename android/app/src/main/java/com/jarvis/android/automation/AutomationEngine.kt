package com.jarvis.android.automation

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/**
 * AutomationEngine — schedules tasks via WorkManager.
 *
 * [schedule] creates a one-shot job as a placeholder bridge until the backend
 * orchestrator dispatches the action; [scheduleDaily] registers a real
 * periodic (every [repeatIntervalMinutes]) job and survives reboots.
 */
class AutomationEngine(private val context: Context) {

    fun schedule(name: String, action: String, initialDelayMinutes: Long = 1) {
        val work = OneTimeWorkRequestBuilder<JarvisWorker>()
            .setInputData(workDataOf("name" to name, "action" to action))
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(work)
    }

    fun scheduleDaily(name: String, action: String, hour: Int = 7, minute: Int = 0) {
        // WorkManager minimum period is 15 minutes; for daily jobs we tag unique work
        // so re-scheduling replaces the previous request instead of duplicating it.
        val work = PeriodicWorkRequestBuilder<JarvisWorker>(24, TimeUnit.HOURS)
            .setInputData(workDataOf("name" to name, "action" to action))
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .addTag(name)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("automation:$name", ExistingPeriodicWorkPolicy.UPDATE, work)
    }

    fun cancel(name: String) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork("automation:$name")
        wm.cancelAllWorkByTag(name)
    }
}

class JarvisWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val name = inputData.getString("name") ?: "task"
        val action = inputData.getString("action") ?: ""
        // Real execution is dispatched by the backend orchestrator → device via WebSocket.
        android.util.Log.i("JarvisWorker", "Executing automation [$name]: $action")
        return Result.success()
    }
}
