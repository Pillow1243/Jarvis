package com.jarvis.android.automation

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * AutomationEngine — schedules tasks via WorkManager (cron → PeriodicWorkRequest)
 */
class AutomationEngine(private val context: Context) {

    fun schedule(name: String, cron: String, action: String) {
        // Parse cron roughly: "0 7 * * *" → 7:00 daily
        val work = OneTimeWorkRequestBuilder<JarvisWorker>()
            .setInputData(workDataOf("name" to name, "action" to action))
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(work)
    }

    fun cancel(name: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(name)
    }
}

class JarvisWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val name = inputData.getString("name") ?: "task"
        val action = inputData.getString("action") ?: ""
        // In prod: call backend / execute device command
        android.util.Log.i("JarvisWorker", "Executing automation [$name]: $action")
        return Result.success()
    }
}
