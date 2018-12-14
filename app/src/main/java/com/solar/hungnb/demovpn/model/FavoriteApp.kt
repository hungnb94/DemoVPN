package com.solar.hungnb.demovpn.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
data class FavoriteApp(
//        @PrimaryKey(autoGenerate = true) var uid: Int,
//        @ColumnInfo(name = "package_name") var packageName: String
        @PrimaryKey(autoGenerate = false) var packageName: String
)