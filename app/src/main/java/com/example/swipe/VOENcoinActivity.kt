package com.example.swipe

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream


class VOENcoinActivity : AppCompatActivity() {

    private lateinit var outputTextView: TextView
    private lateinit var inputEditText: EditText
    private lateinit var viewBlockchainButton: Button
    private lateinit var sendCoinsButton: Button
    private lateinit var mineButton: Button
    private lateinit var amountEditText: EditText

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voen_coin_activity)
        val adress = intent.getStringExtra("adress")
        val uniqueUserKey = intent.getStringExtra("uniqueUserKey")
        // Инициализация компонентов интерфейса
        outputTextView = findViewById(R.id.outputTextView)
        inputEditText = findViewById(R.id.inputEditText)
        viewBlockchainButton = findViewById(R.id.viewBlockchainButton)
        sendCoinsButton = findViewById(R.id.sendCoinsButton)
        amountEditText = findViewById(R.id.amountEditText)
        mineButton = findViewById(R.id.mineButton)

        // Инициализация очереди запросов
        requestQueue = Volley.newRequestQueue(this)

        // Установка слушателей для кнопок

        viewBlockchainButton.setOnClickListener {
            viewBlockchain()
        }

        sendCoinsButton.setOnClickListener {
            val recipientAddress = inputEditText.text.toString()
            val amount = amountEditText.text.toString().toIntOrNull() ?: 0
            sendCoins(recipientAddress, amount)
        }


        mineButton.setOnClickListener {
            mine()
        }


    }



    private fun viewBlockchain() {
        // Отправка GET запроса на сервер для получения информации о блокчейне
        val url = "http://192.168.0.103:8081/blockchain"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val blockchainInfo = response.getJSONArray("blockchain")
                outputTextView.text = blockchainInfo.toString()
            },
            Response.ErrorListener { error ->
                Log.e("RequestError", error.message ?: "Unknown error occurred")
            })
        requestQueue.add(request)
    }


    private fun sendCoins(recipientAddress: String, amount: Int) {
        // Получение значения из строки ввода
        val walletID = intent.getStringExtra("uniqueUserKey")
        // Отправка POST запроса на сервер для отправки монет на указанный адрес
        val url = "http://192.168.0.103:8081/wallets/$walletID/send-coins"
        val requestBody = JSONObject().apply {
            put("address", recipientAddress)
            put("amount", amount)
        }
        val request = JsonObjectRequest(Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                val message = response.getString("message")
                outputTextView.text = message
            },
            Response.ErrorListener { error ->
                Log.e("RequestError", error.message ?: "Unknown error occurred")
            })
        requestQueue.add(request)
    }



    private fun mine() {
        val walletID = intent.getStringExtra("uniqueUserKey")
        val url = "http://192.168.0.103:8081/wallets/$walletID/mine"
        val request = JsonObjectRequest(Request.Method.POST, url, null,
            Response.Listener { response ->
                val message = response.getString("message")
                val reward = response.getInt("reward")
                outputTextView.text = "$message\nReward: $reward coins"
            },
            Response.ErrorListener { error ->
                Log.e("RequestError", error.message ?: "Unknown error occurred")
            })
        requestQueue.add(request)
    }
}