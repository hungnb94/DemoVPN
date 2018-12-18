package com.solar.hungnb.demovpn.screen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.qiniu.android.netdiag.Ping
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
        ping()
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

    private fun ping() {
        val TAG = "PingTest"
        Ping.start("google.com", 5, {
            line -> LogUtils.d(TAG + "line", line)
        }, {
            result -> LogUtils.e(TAG, "Avg: ${result.avg} \nMin: ${result.min} \nMax: ${result.max}" +
                "\nCount: ${result.count} \nDropped: ${result.dropped} \nInterval: ${result.interval}" +
                "\nIp: ${result.ip} \nSent: ${result.sent} \nResult: ${result.result}" +
                "\nCount: ${result.count} \nSide: ${result.size} \nStddev: ${result.stddev}")
        })
    }
}
