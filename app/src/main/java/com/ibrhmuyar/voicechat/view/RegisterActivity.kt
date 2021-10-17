package com.ibrhmuyar.voicechat.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ibrhmuyar.voicechat.notification.FirebaseService
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_register.*



const val TOPIC = "/topics/myTopic2"
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var viewmodel : AuthViewModel
    private lateinit var userToken : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            FirebaseService.token = it.result
            userToken = it.result.toString()
            Log.e("6666",it.result.toString())
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        auth = FirebaseAuth.getInstance()

        viewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        observeLiveData()

        btnRegister.setOnClickListener {

            var username: String = inputUserName.text.toString()
            var email: String = inputEmail.text.toString()
            var pass: String = inputPassword.text.toString()
            var verifyPass: String = inputVerifyPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && username.isNotEmpty()){
                if(pass.length > 5){
                    if(pass == verifyPass){
                        progressLoading.visibility = View.VISIBLE
                        viewmodel.register(email,pass,username,userToken)
                    }else{
                        showToastMessage("Girdiğiniz şifreler birbiri ile eşleşmiyor")
                    }
                }else{
                    showToastMessage("Şifre en az 6 karakter olmalı")
                }
            }else{
                showToastMessage("Bütün bilgileri girdiğinizden emin olun")
            }
        }
        txtLogin.setOnClickListener {
            viewmodel.goToLogin(this)
            finish()
        }
    }

    fun observeLiveData(){
        viewmodel.getUserLiveData().observe(this, Observer{
            if(it != null){
                showToastMessage("Kayıt Başarılı")
                val intent = Intent(this, ProfilePicture::class.java).apply {
                }
                startActivity(intent)
                finish()
                /*viewmodel.goToMainActivity(this)
                finish()*/
            }else{

            }
        })

        viewmodel.existEmail.observe(this, Observer {
            if(it){
                showToastMessage("Bu e-posta zaten kullanılıyor")
            }
        })
        viewmodel.badlyFormat.observe(this, Observer {
            if(it){
                showToastMessage("Geçerli bir e-posta adresi girin")
            }
        })
        viewmodel.loading.observe(this, Observer {
            if(it){
                progressLoading.visibility = View.VISIBLE
            }else{
                progressLoading.visibility = View.GONE
            }
        })

    }

    fun showToastMessage(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser?.uid != null){
           viewmodel.goToMainActivity(this)
            finish()
        }
        //viewmodel.currentUser(this)
    }
}