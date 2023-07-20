// LoginActivity.kt
package com.example.swipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : Activity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var errorMessageTextView: TextView
    private lateinit var successMessageTextView: TextView
    private lateinit var usernameget: String
    private lateinit var uniqueUserKey: String

    private val client = OkHttpClient()
    private val gson = Gson()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)
        successMessageTextView = findViewById(R.id.successMessageTextView)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val json = gson.toJson(mapOf("username" to username, "password" to password))
            val requestBody = json.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("http://192.168.0.103:8080/login")
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        showErrorMessage("Network error")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        // Обработка успешного ответа
                        runOnUiThread {
                            showSuccessMessage("Login successful")
                            val jsonResponse = JSONObject(responseBody)
                            usernameget = jsonResponse.getString("username") // Initialize the lateinit variable
                            uniqueUserKey = jsonResponse.getString("unique_user_key")
                            openAnotherActivityAfterDelay() // Call the function here
                        }
                    } else {
                        // Обработка ошибки
                        val jsonObject = JSONObject(responseBody)
                        val errorMessage = jsonObject.getString("error")
                        runOnUiThread {
                            showErrorMessage(errorMessage)
                        }
                    }
                }
            })
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val json = gson.toJson(mapOf("username" to username, "password" to password))
            val requestBody = json.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("http://192.168.0.103:8080/register")
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        showErrorMessage("Network error")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        // Обработка успешного ответа
                        runOnUiThread {
                            showSuccessMessage("Registration successful")
                        }
                    } else {
                        // Обработка ошибки
                        val jsonObject = JSONObject(responseBody)
                        val errorMessage = jsonObject.getString("error")
                        runOnUiThread {
                            showErrorMessage(errorMessage)
                        }
                    }
                }
            })
        }
    }

    private fun showErrorMessage(message: String) {
        runOnUiThread {
            errorMessageTextView.text = message
            errorMessageTextView.visibility = View.VISIBLE
            successMessageTextView.visibility = View.GONE
        }
    }

    private fun showSuccessMessage(message: String) {
        runOnUiThread {
            successMessageTextView.text = message
            successMessageTextView.visibility = View.VISIBLE
            errorMessageTextView.visibility = View.GONE
        }
    }



    private fun openAnotherActivityAfterDelay() {
        Handler().postDelayed({
            val intent = Intent(this, SwipeActivity::class.java)
            intent.putExtra("username", usernameget)
            intent.putExtra("uniqueUserKey", uniqueUserKey)
            startActivity(intent)
            finish()
        }, 3000)
    }

}
