package com.ibrhmuyar.voicechat.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.ibrhmuyar.voicechat.view.adapter.MessageAdapter
import com.ibrhmuyar.voicechat.R
import kotlinx.android.synthetic.main.activity_chat.*

import com.ibrhmuyar.voicechat.viewmodel.ContactViewModel


class ChatActivity : AppCompatActivity() {

    private lateinit var contactViewModel: ContactViewModel

    private val messageAdapter = MessageAdapter(arrayListOf())

    var senderRoom: String? = null
    var receiverRoom: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        layoutManager.stackFromEnd = false
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.adapter = messageAdapter

        var intent = intent
        var userName = intent.getStringExtra("userName")
        var receiverID = intent.getStringExtra("uId")
        var imageUrl = intent.getStringExtra("imageUrl")
        var senderID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        userNameChat.text = userName
        Glide.with(this)
            .load(imageUrl)
            .circleCrop()
            .override(200,200)
            .into(userChatImage)

        receiverRoom = senderID + receiverID
        senderRoom = receiverID + senderID

        contactViewModel.getMessages(senderRoom!!)

        //************ Mesajı database e gönder
            recorder.setOnClickListener {
                if(inputMessage.text.isNotEmpty()){
                    var message = inputMessage.text.toString()
                    contactViewModel.addMessageToDatabase(message,senderRoom!!,receiverRoom!!,senderID)
                    inputMessage.text.clear()
                }
            }

        contactViewModel.messageList.observe(this, Observer {
            chatRecyclerView.visibility = View.VISIBLE
            this?.let { it1 -> messageAdapter.updateList(it, it1) }
            //setRecyclerSmoot()
            if(it.size == messageAdapter.itemCount){
                if(messageAdapter.itemCount != 0) {
                    chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        })
        contactViewModel.loading.observe(this, Observer {
            if(!it){
                if(messageAdapter.itemCount != 0){
                    chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1 )
                }
            }
        })
    }

}