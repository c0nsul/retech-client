package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess
import java.io.*


class Main : AppCompatActivity() {

    companion object {
        //global
        val defaultIP = "192.168.0.100"
    }

    private lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serverIP.setText(defaultIP)
        progressBar.visibility = View.GONE
        status.text = ""

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

    }

    /**
     *  login
     */
    fun login(view: View) {
        progressBar.visibility = View.VISIBLE

        //get data
        val serverIp = findViewById<EditText>(R.id.serverIP)
        val Login = findViewById<EditText>(R.id.Login)
        val Password = findViewById<EditText>(R.id.Password)

        if (serverIP.text.isNotEmpty() && Login.text.isNotEmpty() && Password.text.isNotEmpty()) {
            //POST
            PostAsyncTask().execute(
                serverIP.text.toString(),
                Login.text.toString(),
                Password.text.toString()
            )
        } else {
            val warn =
                Toast.makeText(
                    this,
                    "Credentials is not set. Please check data!",
                    Toast.LENGTH_LONG
                )
            warn.show()
            progressBar.visibility = View.GONE
        }

        //redirect to setup
        //val setup = Intent(this, Setup::class.java)
        //startActivity(setup)
    }


    //--------------------------------------------------------------------
    //POST
    inner class PostAsyncTask : AsyncTask<String, String, String>() {

        private var result: String = ""

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String {

            var conn: HttpURLConnection? = null

            try {
                //POST
                result = sendData(params[0], params[1], params[2])


            } catch (ex: Exception) {
                Log.d("", "Error in doInBackground " + ex.message)
            } finally {
                conn?.disconnect()
            }
            return result
        }

        override fun onProgressUpdate(vararg values: String?) {

        }

        override fun onPostExecute(result: String?) {
            // Done
            //Debug.text = result
            super.onPostExecute(result)
            parseResult(result.toString())
        }

        /**
         * POST method
         */
        private fun sendData(urlRow: String, login: String, password: String): String {

            try {

                val urlRow = "http://$urlRow:8080/"
                val urlParameters = "login=$login"
                val urlParameters2 = "&password=$password"
                val url = URL(urlRow)

                val conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 4000
                conn.connectTimeout = 4000
                conn.doOutput = true

                try {
                    //write
                    val writer = OutputStreamWriter(conn.outputStream)
                    writer.write(urlParameters)
                    writer.write(urlParameters2)
                    writer.flush()

                    //read
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    result = reader.readLine()


                    reader.close()
                } catch (exception: Exception) {

                }


                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    try {

                        val reader = BufferedReader(InputStreamReader(conn.inputStream))
                        val output: String = reader.readLine()

                        println("There was error while connecting the chat $output")
                        System.exit(0)

                    } catch (exception: Exception) {
                        throw Exception("Exception while push the notification  $exception.message")
                    }
                }



            } catch (e: Exception) {
                Debug.text = "Post error!"
                e.printStackTrace()
            }
            return result

        }
    }
//--------------------------------------------------------------------




    //---------------------------------------------------------------------------------
    //GET
    // AsyncTask inner class
    /*
    inner class GetAsyncTask: AsyncTask<String, String, String>() {

        private var result: String = ""

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String {

            var connect: HttpURLConnection? = null
            try {

                val urlRow = params[0]
                val url = "http://$urlRow:8080/"

                connect = url as HttpURLConnection
                connect.readTimeout = 4000
                connect.connectTimeout = 4000
                connect.requestMethod = "GET"
                connect.connect()

                val responseCode: Int = connect.responseCode
                if (responseCode == 200) {
                    result = streamToString(connect.inputStream)
                }

            } catch (ex: Exception) {
                Log.d("", "Error in doInBackground " + ex.message)
            } finally {
                if (connect != null) {
                    connect.disconnect()
                }
            }
            return result
        }

    }
    */

    fun streamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }
        return result
    }

    /**
     * EXIT BUTTON
     */
    fun exitApp(view: View) {
        moveTaskToBack(true)
        exitProcess(-1)
    }

    /**
     * parsing results
     */
    fun parseResult(data: String) {

        if (!data.isNullOrEmpty()) {

            try {
                var jsonData = JSONObject(data)
            } catch (e: NumberFormatException) {
                status.text = "Bad server response!"
            }

            var jsonData = JSONObject(data)

            //check server response
            if (jsonData.length() > 0) {
                //Debug.text = jsonData.toString()

                if (jsonData.has("error")) {
                    status.text = "Credentalis error"
                } else {
                    //go to setup

                    status.text = "Server online"

                }

            } else {
                status.text = "Bad server response!"
            }


        } else {

            val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifi.isWifiEnabled) {
                //wifi is enabled
                status.text = "Server offline!"

            } else {
                status.text = "WiFI is OFF!"
            }
        }

        progressBar.visibility = View.GONE
    }


}
