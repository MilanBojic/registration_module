package com.registrationmodule;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(UserRoom userRoom);

    @Query("SELECT * FROM User")
    List<UserRoom> getAllUserRoom();



}
