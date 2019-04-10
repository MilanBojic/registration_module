package com.registrationmodule.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull

@Entity(tableName = "User")
class UserRoom {

    @PrimaryKey
    @NonNull
    var id: Long? = null

    var userName: String = ""

    var password: String = ""
}
