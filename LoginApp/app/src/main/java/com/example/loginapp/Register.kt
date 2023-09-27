package com.example.loginapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class Register: AppCompatActivity() {
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var nombre: EditText
    private lateinit var btnregister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        correo = findViewById(R.id.editTextTextEmailAddress2)
        contrasena = findViewById(R.id.editTextTextPassword)
        nombre = findViewById(R.id.editTextTextEmailAddress)
        btnregister = findViewById(R.id.btnRegister)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val textview = findViewById<TextView>(R.id.textView2)

        // Configurar un clic de botón
        btnBack.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }

        // Configurar un clic de botón
        btnregister.setOnClickListener {
            val username = correo.text.toString()
            val password = contrasena.text.toString()
            val name = nombre.text.toString()
            // Llamar a la función loginAndStoreJwt en un contexto de GlobalScope para realizar la operación de forma asincrónica
            GlobalScope.launch(Dispatchers.Main) {
                val jwt = reg(applicationContext, username, password,name)
                if (jwt != null) {
                    if(jwt.equals("User_exists")){
                        textview.text="El usuario ya está registrado"
                    }
                    else if(jwt.equals("not_registered")){

                    }
                    else if(jwt.equals("user_registered")){
                        textview.text="Usuario registrado corectamente!"
                    }
                    println("done")

                } else {
                    val toast = Toast.makeText(applicationContext, "Credenciales invalidas", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }

        }

    }

    suspend fun reg(context: Context, username: String, password: String, name: String): String? {
        val json = JSONObject()
        json.put("username", username)
        json.put("password", password)
        json.put("name", name)

        val mediaType = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        println(username)
        println(password)
        println(json.toString())

        val request = Request.Builder()
            .url("http://10.0.2.2/basic-php-jwt-auth-example/public/register.php") // Reemplaza con la URL de tu servidor
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            val response = OkHttpClient().newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null ) {
                //val jwt = JSONObject(responseBody).getString("jwt")
                val jwt = responseBody
                println(jwt)
                return@withContext jwt
            }
            else {

                    return@withContext null

            }
        }
    }
}