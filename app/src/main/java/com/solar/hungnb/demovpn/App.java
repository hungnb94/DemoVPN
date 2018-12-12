package com.solar.hungnb.demovpn;

import android.app.Application;

import me.logg.Logg;
import me.logg.config.LoggConfiguration;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoggConfiguration configuration = new LoggConfiguration.Buidler()
                .setDebug(true)
//                .setTag("test")// 自定义全局Tag
                .build();
        Logg.init(configuration);
    }
}
