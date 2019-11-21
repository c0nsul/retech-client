package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*


class Setup : AppCompatActivity() {

    private lateinit var pref: SharedPreferences

    private val APP_PREFERENCES = "settings"
    private val APP_PREFERENCES_IP = "ip"
    private val APP_PREFERENCES_PASS = "pass"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        if (pref.contains(APP_PREFERENCES_IP)) {
            // Получаем число из настроек
            var ip = pref.getString(APP_PREFERENCES_IP, null)
            // Выводим на экран данные из настроек
            editText.setText(ip)
        }

        if (pref.contains(APP_PREFERENCES_PASS)) {
            var pass = pref.getString(APP_PREFERENCES_PASS, null)
            Password.setText(pass)
        }
    }

    fun backToPrevious (view: View) {
        //redirect to settings
        val backData = Intent(this, Main::class.java)
        startActivity(backData)
    }

    /**
     * save IP to storage
     */
    fun saveSettings(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val Password = findViewById<EditText>(R.id.Password)

        //save data
        //NOT empty
        if (editText.text != null && !editText.text.isEmpty()) {

            //TODO IP validation

            val serverIp = pref.edit()
            serverIp.putString(APP_PREFERENCES_IP, editText.text.toString())
            serverIp.apply()
            val UpdateButton = Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT)
            UpdateButton.show()
        } else {
            val UpdateButton = Toast.makeText(this, "IP address required!", Toast.LENGTH_SHORT)
            UpdateButton.show()
        }

        if (Password.text != null && !Password.text.isEmpty()) {

            val serverPassword = pref.edit()
            serverPassword.putString(APP_PREFERENCES_PASS, Password.text.toString())
            serverPassword.apply()
            val UpdateButton = Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT)
            UpdateButton.show()
        } else {
            val UpdateButton = Toast.makeText(this, "Password is empty!", Toast.LENGTH_SHORT)
            UpdateButton.show()
        }
    }


}
