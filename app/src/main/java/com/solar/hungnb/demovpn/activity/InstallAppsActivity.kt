package com.solar.hungnb.demovpn.activity

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.adapter.InstallAppAdapter
import kotlinx.android.synthetic.main.activity_install_apps.*


class InstallAppsActivity : AppCompatActivity() {
    private val TAG = "InstallAppsActivity"
    private val installApps = ArrayList<ApplicationInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_install_apps)

        getAllApp()
        rvInstallApp.adapter = InstallAppAdapter(installApps)
    }

    private fun getAllApp() {
        val pm = packageManager
        //get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        installApps.addAll(packages.filter { applicationInfo -> !isSystemApp(applicationInfo) })
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}
