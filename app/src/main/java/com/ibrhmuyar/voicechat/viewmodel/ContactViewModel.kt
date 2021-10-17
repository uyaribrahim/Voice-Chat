package com.ibrhmuyar.voicechat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibrhmuyar.voicechat.model.Contact
import com.ibrhmuyar.voicechat.model.Message
import com.ibrhmuyar.voicechat.model.User
import com.ibrhmuyar.voicechat.repository.FirebaseRepository

class ContactViewModel : ViewModel() {

    private var repository: FirebaseRepository = FirebaseRepository()
    var userList: MutableLiveData<ArrayList<User>> = repository.getUsersMutableLiveData()
    var contactList: MutableLiveData<ArrayList<Contact>> = repository.getContactMutableLiveData()
    var messageList: MutableLiveData<ArrayList<Message>> = repository.getMessagesMutableLiveData()
    var loading: MutableLiveData<Boolean> = repository.getLoading()

    fun getMessages(senderRoom: String){
        repository.getMessagesFromDatabase(senderRoom)
    }
    fun getCurrentUserData(){
        repository.getCurrentUserData()
    }

    fun getUsers(searchKey: String){
        repository.getUsers(searchKey)
    }
    fun addMessageToDatabase(message: String, senderRoom: String, receiverRoom: String,
                             senderID: String, receiverID: String, imageURL: String,
                             receiverName:String,receiverToken:String){
        repository.addMessageToDatabase(message,senderRoom,receiverRoom,senderID,
            receiverID,imageURL,receiverName,receiverToken)
    }
    fun getUsersContacts(){
        repository.getUsersContacts()
    }
}