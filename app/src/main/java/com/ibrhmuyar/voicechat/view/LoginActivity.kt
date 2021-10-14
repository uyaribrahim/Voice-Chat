package com.ibrhmuyar.voicechat.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var viewmodel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        viewmodel.invalidUser.observe(this, Observer {
            if(it){
                Toast.makeText(this,"Geçersiz e-posta veya şifre", Toast.LENGTH_SHORT).show()
            }
        })
        viewmodel.getUserLiveData().observe(this, Observer{
            if(it != null){
                Toast.makeText(this,"Giriş yapıldı",Toast.LENGTH_SHORT).show()
                viewmodel.goToMainActivity(this)
                finish()
            }else{

            }
        })
        viewmodel.network.observe(this, Observer {
            if(it){
                Toast.makeText(this,"İnternet bağlantısı bulunamadı!", Toast.LENGTH_SHORT).show()
            }
        })

        btnLogin.setOnClickListener(View.OnClickListener {
            var email: String = inputEmail.text.toString()
            var pass: String = inputPassword.text.toString()
            viewmodel.login(email,pass)
        })

        txtSignUp.setOnClickListener(View.OnClickListener {
            viewmodel.goToRegister(txtSignUp)
            finish()
        })
    }
}