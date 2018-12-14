package com.solar.hungnb.demovpn.service

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.solar.hungnb.demovpn.utils.CommonUtils
import com.solar.hungnb.demovpn.utils.MyDatabase
import de.blinkt.openvpn.core.VpnStatus
import java.util.*


class FavoriteAppService : Service() {
    private val TAG = "FavoriteAppService"

    private val timer = lazy { Timer() }
    private val task = lazy { MyTask(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        timer.value.scheduleAtFixedRate(task.value, 0, 100)
    }

    override fun onDestroy() {
        super.onDestroy()

        task.value.cancel()
        timer.value.cancel()
        timer.value.purge()
    }

    class MyTask(val context: Context) : TimerTask() {
        private val TAG = "MyTask"
        val database = lazy { MyDatabase.getInstance(context) }
        override fun run() {
            if (!VpnStatus.isVPNActive()) {
                for (favoriteAppPackage in database.value.favoriteAppDao().getAll()) {
                    if (getForegroundPackage() == favoriteAppPackage.packageName) {
                        CommonUtils.startDefaultVpn(context)
                    }
                }
            }
        }

        private fun getForegroundPackage(): String {
            var topPackageName: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val mUsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

                val time = System.currentTimeMillis()

                val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 30, System.currentTimeMillis() + 10 * 1000)
                val event = UsageEvents.Event()
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)
                }

                if (event != null && !TextUtils.isEmpty(event.packageName) && event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    return if (CommonUtils.isRecentActivity(event.className)) {
                        event.className
                    } else event.packageName
                } else {
                    topPackageName = ""
                }
            } else {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity

                if (CommonUtils.isRecentActivity(componentInfo.className)) {
                    return componentInfo.className
                }

                topPackageName = componentInfo.packageName
            }


            return topPackageName
        }


        private fun getForegroundTask(): String {
            var currentApp = ""
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap = TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (!mySortedMap.isEmpty() && mySortedMap[mySortedMap.lastKey()] != null) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                }
            } else {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val tasks = am.runningAppProcesses
                currentApp = tasks[0].processName
            }

            Log.e("Foreground App", "Current App in foreground is: $currentApp")
            return currentApp
        }
    }
}