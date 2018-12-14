package com.solar.hungnb.demovpn.activity

import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.adapter.InstallAppAdapter
import com.solar.hungnb.demovpn.model.AppInfoWrapper
import com.solar.hungnb.demovpn.model.FavoriteApp
import com.solar.hungnb.demovpn.task.LoadFavoriteAppTask
import com.solar.hungnb.demovpn.task.SimpleTask
import com.solar.hungnb.demovpn.utils.MyDatabase
import kotlinx.android.synthetic.main.activity_install_apps.*
import java.util.*


class InstallAppsActivity : AppCompatActivity() {
    private val TAG = "InstallAppsActivity"
    private val RC_USAGE_PERMISSION = 11

    private val listApp = ArrayList<AppInfoWrapper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_install_apps)

        initView()
        addEvent()
    }

    private fun initView() {
        //Load all app on device and show
        LoadFavoriteAppTask(this, object : LoadFavoriteAppTask.OnLoadAppListener {
            override fun onStart() {
                pbLoading.visibility = View.VISIBLE
            }

            override fun onComplete(allApps: List<AppInfoWrapper>) {
                pbLoading.visibility = View.GONE
                rvInstallApp.adapter = InstallAppAdapter(allApps)
            }
        }).execute()

        LoadFavoriteAppTask(this, object : LoadFavoriteAppTask.OnLoadAppListener {
            override fun onStart() {}

            override fun onComplete(allApps: List<AppInfoWrapper>) {
                listApp.clear()
                listApp.addAll(allApps)
            }
        }).execute()
    }

    private fun addEvent() {
        btnSelectAll.setOnClickListener { selectAllApp() }
        btnSaveFavoriteApps.setOnClickListener { checkAndSaveApps() }
    }

    private fun selectAllApp() {
        val adapter = rvInstallApp.adapter
        if (adapter is InstallAppAdapter) {
            adapter.setCheckAll()
        }
    }

    private fun checkAndSaveApps() {
        if (isUsageAccessGranted()) {
            saveFavoriteApps()
        } else {
            requestUsagePermission()
        }
    }

    private fun requestUsagePermission(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need permission")
                .setMessage(String.format(getString(R.string.request_usage_permisson), getString(R.string.app_name)))
                .setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivityForResult(intent, RC_USAGE_PERMISSION)

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                .setCancelable(false)
        builder.show()
    }

    private fun isUsageAccessGranted(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun saveFavoriteApps() {
        val adapter = rvInstallApp.adapter
        if (adapter is InstallAppAdapter) {
            val currentListApp = adapter.listApp
            val db = MyDatabase.getInstance(this)

            val addedAppList = ArrayList<FavoriteApp>()
            val deletedAppList = ArrayList<FavoriteApp>()
            currentListApp.filterIndexed { index, appAfterChange ->
                val appBeforeChange = listApp[index]
                if (appAfterChange.isFavorite != appBeforeChange.isFavorite) {
                    if (appAfterChange.isFavorite) {
                        addedAppList.add(FavoriteApp(appAfterChange.applicationInfo.packageName))
                    } else {
                        deletedAppList.add(FavoriteApp(appAfterChange.applicationInfo.packageName))
                    }
                }
                true
            }
            SimpleTask { db.favoriteAppDao().insertAll(addedAppList) }.execute()
            SimpleTask { db.favoriteAppDao().delete(deletedAppList) }.execute()
        }

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_USAGE_PERMISSION) {
            if (isUsageAccessGranted()) {
                val intent = Intent(this, InstallAppsActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Can not doing this action without permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
