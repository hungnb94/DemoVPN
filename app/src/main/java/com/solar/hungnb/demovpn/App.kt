package com.solar.hungnb.demovpn

import android.app.Application
import me.logg.Logg
import me.logg.config.LoggConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val configuration = LoggConfiguration.Buidler()
                .setDebug(true)
                .build()
        Logg.init(configuration)
    }
}
