package com.ibrhmuyar.voicechat.viewmodel

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.ibrhmuyar.voicechat.view.LoginActivity
import com.ibrhmuyar.voicechat.view.MainActivity
import com.ibrhmuyar.voicechat.view.RegisterActivity
import com.ibrhmuyar.voicechat.repository.FirebaseRepository

class AuthViewModel(): ViewModel() {

    private var repository: FirebaseRepository = FirebaseRepository()
    private var userMutableLiveData: MutableLiveData<FirebaseUser> = repository.getUserLiveData()
    var existEmail: MutableLiveData<Boolean>  = repository.getEmailExistLiveData()
    var invalidUser: MutableLiveData<Boolean> = repository.getInvalidUser()
    var loading: MutableLiveData<Boolean> = repository.getLoading()
    var network: MutableLiveData<Boolean> = repository.getNetwork()
    var badlyFormat: MutableLiveData<Boolean> = repository.getBadlyFormat()



    fun register(email: String, pass: String, username: String, userToken: String){

        repository.signup(email,pass,username,userToken)

    }

    fun login(email: String, pass: String){
        if(email.isNotEmpty() && pass.isNotEmpty()){
            repository.login(email,pass)
        }else{
            invalidUser.value = true
        }
    }

    fun logout(){
        repository.logout()
    }

    fun updateProfilePictures(url: String){
        repository.updateProfilImage(url)
    }

    fun getUserLiveData(): MutableLiveData<FirebaseUser>{
        return userMutableLiveData
    }

    fun goToLogin(context: Context) {
        Intent(context, LoginActivity::class.java).also {
            context.startActivity(it)
        }
    }

    fun goToRegister(view: View){
        Intent(view.context, RegisterActivity::class.java).also{
            view.context.startActivity(it)
        }
    }
    fun goToMainActivity(context: Context) {
        Intent(context, MainActivity::class.java).also {
            context.startActivity(it)
        }
    }
}