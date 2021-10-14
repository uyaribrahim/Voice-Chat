package com.ibrhmuyar.voicechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_register.*




class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var viewmodel : AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                        viewmodel.register(email,pass,username)
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