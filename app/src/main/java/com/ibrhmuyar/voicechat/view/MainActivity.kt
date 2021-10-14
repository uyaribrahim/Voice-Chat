package com.ibrhmuyar.voicechat.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.databinding.ActivityMainBinding
import com.ibrhmuyar.voicechat.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)


        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.itemLogout -> logout()
            };true
        }

        setUpFragment()
    }
    fun logout(){
        viewModel.logout()
        Toast.makeText(this,"Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
        viewModel.goToLogin(this)
        finish()
    }
    private fun setUpFragment(){

        binding.bottomNavBar.setItemSelected(R.id.nav_chat)
        binding.bottomNavBar.setOnItemSelectedListener {
            val action = ChatsFragmentDirections.actionChatsFragmentToSearchUserFragment()
            val secondAction = SearchUserFragmentDirections.actionSearchUserFragmentToChatsFragment()
            when (it){
                R.id.nav_search -> Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)
                R.id.nav_chat -> Navigation.findNavController(this,R.id.fragmentContainerView).navigate(secondAction)
            }
        }
    }

}
