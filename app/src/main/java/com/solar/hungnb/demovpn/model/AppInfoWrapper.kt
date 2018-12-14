package com.solar.hungnb.demovpn.model

import android.content.pm.ApplicationInfo

data class AppInfoWrapper(
        val applicationInfo: ApplicationInfo,
        var isFavorite: Boolean
)