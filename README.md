# ROOM - STORIO - SQLITE

## What is this?

Through one simple example(registration module), i want to shows how to persist data using a ***Room***, ***Storio*** and ***SQLite***.
Our simple application stored only username and password, of course the real registration module is much more complex.

## Requirements

Android 5.0 or later (Minimum SDK level 21)

Android Studio 3.0 (to compile and use)

Eclipse is not supported

## Download
apply plugin: 'kotlin-kapt'

implementation 'com.pushtorefresh.storio3:sqlite:3.0.0'
implementation 'com.pushtorefresh.storio3:sqlite-annotations:3.0.0'
kapt 'com.pushtorefresh.storio3:sqlite-annotations-processor:3.0.0'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'

// Extensions = ViewModel + LiveData
implementation "android.arch.lifecycle:extensions:1.1.1"
kapt "android.arch.lifecycle:compiler:1.1.1"

// Room
implementation "android.arch.persistence.room:runtime:1.1.1"
kapt "android.arch.persistence.room:compiler:1.1.1"



## Example insert operations for each API

        when (index) {
            GlobalConst.SQLITE -> {
                val contentValues = ContentValues()
                contentValues.put(GlobalConst.COL_2, userName)
                contentValues.put(GlobalConst.COL_3, password)
                sqlLiteDataBase.writableDatabase.insert(GlobalConst.USER_TABLE, null, contentValues)
            }
            GlobalConst.STORIO -> {
                val user = User()
                user.userName = userName
                user.password = password
                storioApi.addUser(user)
            }
            GlobalConst.ROOM -> {
                val userRoom = UserRoom()
                userRoom.userName = userName
                userRoom.password = password
                roomApi.insertUserRoom(userRoom)
            }
        }


## What does it look like?

![test image size](https://github.com/MilanBojic/registration_module/blob/master/image1.png)

![test image size](https://github.com/MilanBojic/registration_module/blob/master/image2.png)



**Support**

If you've found an error while using the library, please file an issue. All patches are encouraged, and may be submitted by forking this project and submitting a pull request through GitHub.
