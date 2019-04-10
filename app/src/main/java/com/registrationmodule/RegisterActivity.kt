package com.registrationmodule

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.registrationmodule.room.RoomApi
import com.registrationmodule.room.UserRoom
import com.registrationmodule.storio.StorioApi
import com.registrationmodule.storio.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mGroupBox: RadioGroup
    private lateinit var roomApi: RoomApi
    private lateinit var mRegisterButton: Button
    private lateinit var mLoginButton: Button
    private val EMPTY_SELECT = -1
    private lateinit var sqlLiteDataBase: SQLiteDB
    private lateinit var storioApi: StorioApi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        initViews()
        mRegisterButton.setOnClickListener {
            storeToDataBase()
        }
        mLoginButton.setOnClickListener {
            goToLoginActivity()
        }

    }

    private fun initViews() {
        mUserName = findViewById(R.id.register_username_field)
        mPassword = findViewById(R.id.register_password_field)
        mRegisterButton = findViewById(R.id.register_button)
        mLoginButton = findViewById(R.id.login_button)
        mGroupBox = findViewById(R.id.group_box_id)
        sqlLiteDataBase = SQLiteDB(this)
        storioApi = StorioApi(this)
        roomApi = RoomApi(this)
    }

    private fun storeToDataBase() {
        val userName = mUserName.text.toString()
        val password = mPassword.text.toString()
        if (userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.userEmpty), Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show()
            return
        }
        if (mGroupBox.checkedRadioButtonId == EMPTY_SELECT) {
            Toast.makeText(this, getString(R.string.tErrorSelect), Toast.LENGTH_LONG).show()
            return
        }

        val radioButton = findViewById<RadioButton>(mGroupBox.checkedRadioButtonId)

        if (isValidationOk(userName, radioButton.text.toString())) {
            store(userName, password, radioButton.text.toString())
        }

    }

    private fun isValidationOk(userName: String, index: String): Boolean {
        var usersList = ArrayList<User>()
        var userRoomList = ArrayList<UserRoom>()
        when (index) {
            GlobalConst.SQLITE -> usersList = sqlLiteDataBase.getAllUsers()
            GlobalConst.STORIO -> {
                usersList = storioApi.getAllUsers()
            }
            GlobalConst.ROOM -> {
                userRoomList = roomApi.getAllUsers()
            }
        }

        usersList.forEach { t: User? ->
            run {
                if (t!!.userName.equals(userName)) {
                    Toast.makeText(
                        this,
                        getString(R.string.tDifferentUser),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            }
        }
        userRoomList.forEach { t: UserRoom? ->
            if (t!!.userName.equals(userName)) {
                Toast.makeText(
                    this,
                    getString(R.string.tDifferentUser),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    private fun store(userName: String, password: String, index: String) {
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

    }

    private fun goToLoginActivity() {
        val intentToLoginActivity: Intent
        intentToLoginActivity = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intentToLoginActivity)
    }

    override fun onResume() {
        super.onResume()
        mUserName.setText("")
        mPassword.setText("")
    }

}
