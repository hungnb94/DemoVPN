package com.solar.hungnb.demovpn.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

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