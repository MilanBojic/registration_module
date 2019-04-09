package com.registrationmodule.room

import android.arch.persistence.room.Room
import android.content.Context

class RoomApi(val context: Context) {


    private var roomDB: RoomDB

    init {
        roomDB = Room.databaseBuilder(context, RoomDB::class.java, "User")
            .allowMainThreadQueries()
            .build()
    }


    fun insertUserRoom(userRoom: UserRoom){
        val userDao = roomDB.userDAO
        userDao.insert(userRoom)
    }

    fun getAllUsers():ArrayList<UserRoom>{
        val list = ArrayList<UserRoom>()
        roomDB.userDAO.allUserRoom.forEach { list.add(it) }
        return list
    }
}
