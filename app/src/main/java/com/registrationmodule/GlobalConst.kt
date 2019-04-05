package com.registrationmodule

import android.provider.BaseColumns

object GlobalConst{

    val DATABASE_NAME: String = "User.db"
    val USER_TABLE: String = "User"
    val COL_1: String = BaseColumns._ID
    val COL_2: String = "username"
    val COL_3: String = "password"

    val SQLITE:String = "SQLite DB"
    val STORIO :String = "Storio DB"
    val ROOM :String = "Room DB"
}
