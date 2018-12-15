package com.solar.hungnb.demovpn.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.task.SimpleTask
import com.solar.hungnb.demovpn.utils.LogUtils
import kotlinx.android.synthetic.main.activity_network_speed_testing.*


class NetworkSpeedTestingActivity : AppCompatActivity() {
    private val TAG = NetworkSpeedTestingActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_speed_testing)

        testSpeed("https://www.google.com.vn")
    }

    private fun testSpeed(mUrl: String) {
        SimpleTask {
            val runtime = Runtime.getRuntime()
            try {
                val startTime = System.currentTimeMillis()
                val ipProcess = runtime.exec(arrayOf("ping", "-c 4", mUrl))
                ipProcess.waitFor()
                val timeOfPing = System.currentTimeMillis() - startTime

                // update the ping result - we need to call this on the UI thread
                // because it updates UI elements (TextView)
                LogUtils.e(TAG, "Ping speed command line $timeOfPing")
                tvPingSpeed.post { tvPingSpeed.text = String.format("Ping %d ms", timeOfPing) }
            } catch (ex: Exception) {
                ex.printStackTrace(); }
        }.execute()
    }
}
