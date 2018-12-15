package com.solar.hungnb.demovpn.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.service.FavoriteAppService
import com.solar.hungnb.demovpn.utils.CommonUtils
import de.blinkt.openvpn.core.OpenVPNManagement
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity(), VpnStatus.ByteCountListener, VpnStatus.StateListener {
    private val TAG = MainActivity::class.java.simpleName

    private val RC_START_VPN = 10
    private var isBindService = false
    private var firstData = false
    private var connectionTime: Long = 0


    internal var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            if (binder is OpenVPNService.LocalBinder) {
                mService = binder.service
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }
    }

    internal var threadUpdateConnectionTime: Thread = object : Thread() {
        override fun run() {
            while (VpnStatus.isVPNActive()) {
                runOnUiThread {
                    val timeConnectMillis = System.currentTimeMillis() - connectionTime
                    val sdf = SimpleDateFormat("HH:mm:ss")
                    sdf.timeZone = TimeZone.getTimeZone("GMT")
                    val calConnectTime = GregorianCalendar()
                    calConnectTime.timeInMillis = timeConnectMillis
                    val timeConnect = String.format(getString(R.string.connect_time),
                            sdf.format(calConnectTime.time))
                    tvTimeConnect.text = timeConnect
                }
                SystemClock.sleep(1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        VpnStatus.addByteCountListener(this)
        VpnStatus.addStateListener(this)

        checkMyService()
    }

    private fun checkMyService() {
        if (!CommonUtils.isMyServiceRunning(this, FavoriteAppService::class.java)) {
            startService(Intent(this, FavoriteAppService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(this, OpenVPNService::class.java)
        intent.action = OpenVPNService.START_SERVICE
        isBindService = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        if (isBindService) {
            isBindService = false
            unbindService(serviceConnection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        unregisterReceiver(trafficReceiver);
    }

    private fun preprapeVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, RC_START_VPN)
        } else {
            startVpn()
        }
    }

    private fun startVpn() {
        CommonUtils.startDefaultVpn(this)
    }

    private fun stopVpn() {
        ProfileManager.setConntectedVpnProfileDisconnected(this)
        if (mService != null && mService!!.management != null) {
            mService!!.management.stopVPN(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_START_VPN) {
            if (resultCode == Activity.RESULT_OK) {
                startVpn()
            } else {
                Toast.makeText(this, "Can not open vpn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun click(view: View) {
        when (view.id) {
            R.id.btnStartVpn -> {
                firstData = false
                preprapeVpn()
            }
            R.id.btnStopVpn -> {
                firstData = true
                stopVpn()
            }
            R.id.btnSelectApp -> selectApp()
            R.id.btnTestNetworkSpeed -> openNetworkTestActivity()
            else -> {
            }
        }
    }

    private fun openNetworkTestActivity() {
        val intent = Intent(this, NetworkSpeedTestingActivity::class.java)
        startActivity(intent)
    }

    private fun selectApp() {
        val intent = Intent(this, InstallAppsActivity::class.java)
        startActivity(intent)
    }

    override fun updateByteCount(inp: Long, out: Long, diffIn: Long, diffOut: Long) {
        if (firstData) {
            firstData = false
        } else if (connectionTime > 0) {
            val downloadSession = String.format(getString(R.string.traffic_in),
                    OpenVPNService.humanReadableByteCount(inp, false))
            val uploadSession = String.format(getString(R.string.traffic_out),
                    OpenVPNService.humanReadableByteCount(out, false))
            val downloadSpeed = String.format(getString(R.string.download_speed),
                    OpenVPNService.humanReadableByteCount(diffIn / OpenVPNManagement.mBytecountInterval, true))
            val uploadSpeed = String.format(getString(R.string.upload_speed),
                    OpenVPNService.humanReadableByteCount(diffOut / OpenVPNManagement.mBytecountInterval, true))

            runOnUiThread { tvStatus.text = String.format("%s\n%s\n%s\n%s", downloadSpeed, downloadSession, uploadSpeed, uploadSession) }
        }
    }

    override fun updateState(state: String, logmessage: String, localizedResId: Int, level: VpnStatus.ConnectionStatus) {
        if (level == VpnStatus.ConnectionStatus.LEVEL_CONNECTED) {
            tvTimeConnect!!.postDelayed({
                if (OpenVPNService.mConnecttime > 0)
                    connectionTime = OpenVPNService.mConnecttime
                else
                    connectionTime = System.currentTimeMillis()

                threadUpdateConnectionTime.start()
            }, 50)

            firstData = false
        } else if (level == VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED) {
            firstData = true

            runOnUiThread { tvStatus!!.text = logmessage }
        }
    }

    companion object {

        private var mService: OpenVPNService? = null
    }
}