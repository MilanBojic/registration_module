package com.registrationmodule

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase



@Database(entities = [UserRoom::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract val userDAO: UserDao
}
