package com.solar.hungnb.demovpn.task

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import com.solar.hungnb.demovpn.model.AppInfoWrapper
import com.solar.hungnb.demovpn.model.FavoriteApp
import com.solar.hungnb.demovpn.utils.MyDatabase
import java.lang.ref.WeakReference

class LoadFavoriteAppTask(context: Context, private val listener: OnLoadAppListener) : AsyncTask<Void, Void, List<AppInfoWrapper>>() {
    private var reff: WeakReference<Context>? = null

    init {
        this.reff = WeakReference(context)
    }

    override fun onPreExecute() {
        listener.onStart()
    }

    override fun doInBackground(vararg params: Void?): List<AppInfoWrapper> {
        return if (reff?.get() != null) {
            val context = reff?.get()!!

            //Get all app on device
            val allApps = getAllApp(context)
            //Get favorite apps
            val favoriteApps = MyDatabase.getInstance(context).favoriteAppDao().getAll()

            //Create list favorite app, every item has field isLike
            val result = ArrayList<AppInfoWrapper>()
            allApps.filter { applicationInfo ->
                val isLike = isFavoriteApp(applicationInfo, favoriteApps)
                result.add(AppInfoWrapper(applicationInfo, isLike))
                isLike
            }
            result
        } else ArrayList()
    }

    private fun getAllApp(context: Context): ArrayList<ApplicationInfo> {
        val pm = context.packageManager
        //get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val installApps = ArrayList<ApplicationInfo>()
        installApps.addAll(packages.filter { applicationInfo ->
            (!isSystemApp(applicationInfo) && isAppUsingInternet(applicationInfo, pm))
        })

        return installApps
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun isAppUsingInternet(applicationInfo: ApplicationInfo, packageManager: PackageManager): Boolean {
        return packageManager.checkPermission(Manifest.permission.INTERNET, applicationInfo.packageName) == PackageManager.PERMISSION_GRANTED
    }

    private fun isFavoriteApp(packageInfo: ApplicationInfo, allApps: List<FavoriteApp>): Boolean {
        allApps.filter { app ->
            val isLike = app.packageName == packageInfo.packageName
            if (isLike) return true
            true
        }
        return false
    }

    override fun onPostExecute(allApps: List<AppInfoWrapper>) {
        listener.onComplete(allApps)
    }

    interface OnLoadAppListener {
        fun onStart()
        fun onComplete(allApps: List<AppInfoWrapper>)
    }
}