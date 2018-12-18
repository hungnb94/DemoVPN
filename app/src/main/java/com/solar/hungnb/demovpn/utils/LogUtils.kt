package com.solar.hungnb.demovpn.utils

import android.util.Log

object LogUtils {
    private val debug = true

    @JvmStatic
    fun e(tag: String?, msg: String?) {
        if (debug) Log.e(tag, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: String?) {
        if (debug) Log.i(tag, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: String?) {
        if (debug) Log.d(tag, msg)
    }
}