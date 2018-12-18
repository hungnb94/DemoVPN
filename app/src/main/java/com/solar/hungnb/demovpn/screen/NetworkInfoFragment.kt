package com.solar.hungnb.demovpn.screen

import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.location.Geocoder
import android.location.LocationManager
import android.net.NetworkInfo.DetailedState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.os.ConfigurationCompat
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.databinding.FragmentNetworkInfoBinding
import com.solar.hungnb.demovpn.utils.LogUtils
import java.net.NetworkInterface
import java.util.*


class NetworkInfoFragment : Fragment() {
    private val TAG = NetworkInfoFragment::class.java.simpleName
    private var wifiName: String? = null
    private var ipAddress: String? = null
    private var wifiState: String? = null
    private var countryName: String? = null
    private var macAddress: String? = null
    private var phoneName: String? = null
    private var phoneVersion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiName = "Wifi name: " + getWifiName(manager)
        ipAddress = "IPv4: " + getIpAddress(true) + "\nIPv6: " + getIpAddress(false)
        wifiState = "Wifi encrypted: " + getWifiState(manager)
        countryName = "Country name: " + getUserCountry()
        macAddress = "Mac address: " + getMacAddress(manager)
        phoneName = "Phone name: " + android.os.Build.MODEL
        phoneVersion = "Phone version: " + android.os.Build.VERSION.RELEASE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentNetworkInfoBinding>(inflater, R.layout.fragment_network_info, container, false)
        binding.setLifecycleOwner(this)

        binding.wifiName = wifiName
        binding.ipAdress = ipAddress
        binding.wifiState = wifiState
        binding.countryName = countryName
        binding.macAddress = macAddress
        binding.phoneName = phoneName
        binding.phoneVersion = phoneVersion

        return binding.root
    }

    private fun getWifiName(manager: WifiManager): String? {
        if (manager.isWifiEnabled) {
            val wifiInfo = manager.connectionInfo
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                if (state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
                    var wifiName = wifiInfo.ssid
                    if (wifiName.indexOf("\"") == 0 && wifiName.lastIndexOf("\"") == wifiName.length - 1) {
                        wifiName = wifiName.substring(1, wifiName.length - 1)
                    }
                    return wifiName
                }
            }
        }
        return null
    }

    fun getIpAdress(manager: WifiManager): String? {
        val wifiInfo = manager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        return String.format("%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)
    }

    private fun getIpAddress(useIPv4: Boolean): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(TAG, ex.message)
            ex.printStackTrace()
        }
        return null
    }

    private fun getWifiState(manager: WifiManager): Boolean? {
        val networks = manager.configuredNetworks
        val wifiInfo = manager.connectionInfo
        val configurations = networks.filter { wifiConfiguration -> (wifiConfiguration.SSID == wifiInfo.ssid) }
        if (configurations.isEmpty()) return null

        return getSecurity(configurations[0])
    }

    private fun getSecurity(config: WifiConfiguration): Boolean {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return true //SECURITY_PSK
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return true //SECURITY_EAP
        }
        if (config.wepKeys[0] != null) {
            return true //SECURITY_WEP
        }
        return false //SECURITY_NONE
    }

    private fun getCountryNameDeprecated(): String? {
        val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        return locale.displayCountry
    }

    private fun getMacAddress(manager: WifiManager): String? {
        val info = manager.connectionInfo
        return info.macAddress
    }

    private fun getUserCountry(): String? {
        try {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                val locale = Locale("", simCountry)
                LogUtils.d(TAG, "Get country name by sim country code")
                return locale.displayCountry
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    val locale = Locale("", networkCountry)
                    LogUtils.d(TAG, "Get country name by network country code")
                    return locale.displayCountry
                }
            }
        } catch (ex: Exception) {
            LogUtils.e(TAG, ex.message)
            ex.printStackTrace()
        }
        //Can not get country name from sim or network
        return getCountryName()
    }

    private fun getCountryName(): String? {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                return addresses[0].countryName
            }
        } catch (ex: Exception) {
            LogUtils.e(TAG, ex.message)
            ex.printStackTrace()
        }

        return null
    }

}
