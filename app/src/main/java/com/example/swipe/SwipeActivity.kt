// SwipeActivity.kt
package com.example.swipe

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class SwipeActivity : Activity() {
    private lateinit var tvUsername: TextView
    private lateinit var tvAdress: TextView
    private lateinit var tvBalance: TextView
    private lateinit var requestQueue: RequestQueue

    private val handler = Handler()
    private val delay: Long = 60 // Интервал обновления в миллисекундах (например, 1 минута)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        tvUsername = findViewById(R.id.tvUsername)
        tvAdress = findViewById(R.id.tvAdress)
        tvBalance = findViewById(R.id.tvBalance)

        val username = intent.getStringExtra("username")
        val uniqueUserKey = intent.getStringExtra("uniqueUserKey")

        tvUsername.text = "$username"

        // Инициализация объекта RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        val btnNext = findViewById<Button>(R.id.btnNext)
        btnNext.setOnClickListener {
            val intent = Intent(this, VOENcoinActivity::class.java)
            intent.putExtra("adress", tvAdress.text.toString())
            intent.putExtra("uniqueUserKey", uniqueUserKey)
            startActivity(intent)
        }
        val copyAddress = findViewById<Button>(R.id.copyAddress)
        copyAddress.setOnClickListener {
            val address = tvAdress.text.toString()
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Address", address)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
        }


        // Вызов функции checkBalance с параметром uniqueUserKey
        checkBalance(uniqueUserKey)
        checkAdress(uniqueUserKey)
    }

    override fun onResume() {
        super.onResume()
        startBalanceUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopBalanceUpdate()
    }

    private fun startBalanceUpdate() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Вызов функции checkBalance для обновления баланса
                val uniqueUserKey = intent.getStringExtra("uniqueUserKey")
                checkBalance(uniqueUserKey)

                // Повторный вызов функции через заданный интервал
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

    private fun stopBalanceUpdate() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun checkBalance(uniqueUserKey: String?) {
        // Отправка GET запроса на сервер для получения баланса кошелька
        val url = "http://192.168.0.103:8081/wallets/$uniqueUserKey/myBalance"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val balance = response.getInt("balance")
                tvBalance.text = "Balance: $balance coins"
            },
            Response.ErrorListener { error ->
                Log.e("RequestError", error.message ?: "Unknown error occurred")
            })
        requestQueue.add(request)
    }

    private fun checkAdress(uniqueUserKey: String?) {
        // Отправка GET запроса на сервер для получения баланса кошелька
        val url = "http://192.168.0.103:8081/wallets/$uniqueUserKey/myAdress"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val adress = response.getString("adress")
                tvAdress.text = "$adress"
            },
            Response.ErrorListener { error ->
                Log.e("RequestError", error.message ?: "Unknown error occurred")
            })
        requestQueue.add(request)
    }
}
