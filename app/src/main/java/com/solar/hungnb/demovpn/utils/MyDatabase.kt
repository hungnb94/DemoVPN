package com.solar.hungnb.demovpn.utils

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.solar.hungnb.demovpn.model.AppDao
import com.solar.hungnb.demovpn.model.FavoriteApp



@Database(entities = arrayOf(FavoriteApp::class), version = 1)
abstract class MyDatabase : RoomDatabase() {

    abstract fun favoriteAppDao(): AppDao

    companion object {
        var instance: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, MyDatabase::class.java, "favorite_app_vpn")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return instance!!
        }
    }
}