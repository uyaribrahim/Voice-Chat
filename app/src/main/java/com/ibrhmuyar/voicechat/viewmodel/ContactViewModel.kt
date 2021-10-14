package com.ibrhmuyar.voicechat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibrhmuyar.voicechat.model.Message
import com.ibrhmuyar.voicechat.model.User
import com.ibrhmuyar.voicechat.repository.FirebaseRepository

class ContactViewModel : ViewModel() {

    private var repository: FirebaseRepository = FirebaseRepository()
    var userList: MutableLiveData<ArrayList<User>> = repository.getUsersMutableLiveData()
    var messageList: MutableLiveData<ArrayList<Message>> = repository.getMessagesMutableLiveData()
    var loading: MutableLiveData<Boolean> = repository.getLoading()

    fun getMessages(senderRoom: String){
        repository.getMessagesFromDatabase(senderRoom)
    }

    fun getUsers(){
        repository.getUsers()
    }
    fun addMessageToDatabase(message: String, senderRoom: String, receiverRoom: String, senderID: String){
        repository.addMessageToDatabase(message,senderRoom,receiverRoom,senderID)
    }
}