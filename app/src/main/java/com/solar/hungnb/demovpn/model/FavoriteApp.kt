package com.solar.hungnb.demovpn.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class FavoriteApp(
        @ColumnInfo(name = "package_name") var packageName: String
)