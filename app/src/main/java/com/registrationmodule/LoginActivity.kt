package com.registrationmodule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.registrationmodule.room.RoomApi
import com.registrationmodule.room.UserRoom
import com.registrationmodule.storio.StorioApi
import com.registrationmodule.storio.User

class LoginActivity : AppCompatActivity() {
    private lateinit var mUserName: EditText
    private lateinit var mPassword: EditText
    private lateinit var mLoginButton: Button
    private lateinit var storioApi: StorioApi
    private lateinit var roomApi: RoomApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        initViews()

        mLoginButton.setOnClickListener {
            if (isValidationOk()) {
                login()
            } else {
                Toast.makeText(this, getString(R.string.tErrorLogin), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidationOk(): Boolean {
        val userName = mUserName.text.toString()
        val password = mPassword.text.toString()
        if (userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.userEmpty), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.passwordEmpty), Toast.LENGTH_SHORT).show()
            return false
        }
        val usersList = storioApi.getAllUsers()
        val usersRoomList = roomApi.getAllUsers()
        usersList.forEach { t: User? ->
            if (t!!.userName.equals(userName) && t.password.equals(password)) {
                return true
            }
        }

        usersRoomList.forEach { t: UserRoom? ->
            if (t!!.userName.equals(userName) && t.password.equals(password)) {
                return true
            }
        }

        return false
    }


    private fun initViews() {
        mUserName = this.findViewById(R.id.login_username_field)
        mPassword = this.findViewById(R.id.login_password_field)
        mLoginButton = this.findViewById(R.id.login_button)
        storioApi = StorioApi(this)
        roomApi = RoomApi(this)
    }

    fun login() {
        Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show()
    }


}
