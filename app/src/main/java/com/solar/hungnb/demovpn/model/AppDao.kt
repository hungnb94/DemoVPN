package com.solar.hungnb.demovpn.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface AppDao {
    @Query("SELECT * FROM favoriteapp")
    fun getAll(): List<FavoriteApp>

    @Query("SELECT * FROM favoriteapp WHERE package_name IN (:packageName)")
    fun findByPackageName(packageName : String): List<FavoriteApp>

    @Insert
    fun insertAll(vararg app: FavoriteApp)

    @Delete
    fun delete(app: FavoriteApp)
}