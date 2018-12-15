package com.solar.hungnb.demovpn.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.solar.hungnb.demovpn.R
import kotlinx.android.synthetic.main.activity_network_speed_testing.*
import okhttp3.OkHttpClient
import okhttp3.Request


class NetworkSpeedTestingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_speed_testing)

        testSpeed("http://cachefly.cachefly.net/100mb.test")
    }

    private fun testSpeed(url: String) {
        val client =  OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()

        val sendRequestTime = response.sentRequestAtMillis()
        val startReceiveRspTime = response.receivedResponseAtMillis()

        val pingSpeed = startReceiveRspTime - sendRequestTime
        tvPingSpeed.text = String.format("Ping %d ms", pingSpeed)
    }
}
