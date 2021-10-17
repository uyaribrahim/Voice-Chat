package com.ibrhmuyar.voicechat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.ibrhmuyar.voicechat.notification.NotificationData
import com.ibrhmuyar.voicechat.notification.PushNotification
import com.ibrhmuyar.voicechat.notification.RetrofitInstance
import com.ibrhmuyar.voicechat.model.Contact
import com.ibrhmuyar.voicechat.model.Message
import com.ibrhmuyar.voicechat.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FirebaseRepository {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val db = FirebaseDatabase.getInstance()


    private var myRef = db.reference

    private var currentUserName: String? = null
    private var senderToken: String? = null
    var senderID: String? = null
    var senderImgUrl: String? = null

    private var userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private var emailExist: MutableLiveData<Boolean> = MutableLiveData()
    private var invalidUser: MutableLiveData<Boolean> = MutableLiveData()
    private var loading: MutableLiveData<Boolean> = MutableLiveData()
    private var networkError: MutableLiveData<Boolean> = MutableLiveData()
    private var badlyFormat: MutableLiveData<Boolean> = MutableLiveData()
    private var userList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private var messageList: MutableLiveData<ArrayList<Message>> = MutableLiveData()
    private var contactList: MutableLiveData<ArrayList<Contact>> = MutableLiveData()

    fun getCurrentUserData() {

        var currentUser: User?
        myRef.child("users").child(auth.uid.toString()).get()
            .addOnSuccessListener {
                currentUser = it.getValue(User::class.java)

                currentUserName = currentUser?.userName
                senderToken = currentUser?.userToken
                senderID = currentUser?.userId
                senderImgUrl = currentUser?.profileImgUrl
            }
    }
    fun addContactToDatabase(receiverID: String,imageURL: String,receiverName: String, receiverToken:String){

                var contact = Contact(receiverID,imageURL,receiverName,receiverToken)
                myRef.child("contact").child(senderID.toString()).child("contacts")
                    .child(receiverID)
                    .setValue(contact).addOnSuccessListener {

                        var contact = Contact(senderID,senderImgUrl,currentUserName,senderToken)
                        myRef.child("contact").child(receiverID).child("contacts")
                            .child(senderID.toString())
                            .setValue(contact).addOnSuccessListener {
                            }
                    }
    }

    fun addMessageToDatabase(message: String, senderRoom: String,receiverRoom:String, senderID: String,receiverID:String, imageURL: String, receiverName: String,receiverToken:String){
        loading.value = true
        val msg = Message(message,senderID)

        myRef.child("rooms").child(senderRoom).child("messages").push()
            .setValue(msg).addOnSuccessListener {
                myRef.child("rooms").child(receiverRoom).child("messages").push()
                    .setValue(msg).addOnSuccessListener {
                        loading.value = false
                        myRef.child("contact").child(senderRoom).child("contacts")
                            .child(receiverID)
                            .get()
                            .addOnSuccessListener {
                                if(!it.exists()){
                                    addContactToDatabase(receiverID,imageURL,receiverName,receiverToken)
                                }
                            }
                        PushNotification(
                            NotificationData("Yeni bir mesajın var!", currentUserName.toString()+" sana bir mesaj gönderdi."),
                            receiverToken.toString()
                        ).also {
                            sendNotification(it)
                        }
                    }

            }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch{

        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){

            }else{
                // Log.e("%%%", response.errorBody().toString())
            }

        }catch (e: Exception){
            //Log.e("%%%", e.toString())
        }

    }
    fun getMessagesFromDatabase(senderRoom: String){
        var list: ArrayList<Message> = ArrayList()
        myRef.child("rooms").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (messageSnapshot in snapshot.children){
                        val messages = messageSnapshot.getValue(Message::class.java)
                        list.add(messages!!)
                    }
                    messageList.postValue(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun getUsersContacts() {
        var list: ArrayList<Contact> = ArrayList()
        myRef.child("contact").child(auth.uid.toString()).child("contacts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (contactSnapshot in snapshot.children) {
                        val contacts = contactSnapshot.getValue(Contact::class.java)
                        if (contacts != null) {
                            list.add(contacts)
                        }
                    }
                    contactList.postValue(list)
                    Log.e("----",contactList.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    //Veritabanından kullanıcıları al
    fun getUsers(searchKey: String){

        loading.value = true
        var list: ArrayList<User> = ArrayList()
            myRef.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for(userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(User::class.java)
                    if(auth.uid != user?.userId && user?.userName?.contains(searchKey)!!){
                        list.add(user)
                    }
                }
                userList.postValue(list)
                loading.value = false
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //Yeni kayıt olan kullanıcı bilgilerini veritabanına kaydet
    fun saveUserDataToDatabase(email: String,username: String, userToken: String){
        val defaultImageUrl = "null"
        val user = User(auth.uid.toString() ,username,email,defaultImageUrl,userToken)
        myRef = FirebaseDatabase.getInstance().reference
        myRef.child("users").child(auth.uid.toString()).setValue(user)
        loading.value = false
    }

    fun signup(email: String, pass: String, username: String, userToken:String){

        loading.value = true
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    saveUserDataToDatabase(email,username,userToken)
                    userMutableLiveData.postValue(auth.currentUser)

                } else{
                    try {
                        throw it.exception!!
                    }
                    catch (existEmail: FirebaseAuthUserCollisionException){
                        emailExist.value = true
                        loading.value = false
                    }
                    catch (badlyFormatEmail: FirebaseAuthInvalidCredentialsException){
                        badlyFormat.value = true
                        loading.value = false
                    }
                }
            }
    }

    fun login(email: String, pass: String){
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    userMutableLiveData.postValue( auth.currentUser)
                }else{
                    try {
                        throw it.exception!!
                    }
                    catch (invalidUserException: FirebaseAuthInvalidUserException){
                        invalidUser.value = true
                    }
                    catch (invalidCredentialsException: FirebaseAuthInvalidCredentialsException){
                        invalidUser.value = true
                    }
                    catch (networkException: FirebaseNetworkException){
                        networkError.value = true
                    }
                }
            }
    }

    fun logout() = auth.signOut()

    fun updateProfilImage(url: String){

        myRef = FirebaseDatabase.getInstance().reference
        myRef.child("users").child(auth.uid.toString()).child("profileImgUrl").setValue(url)

    }

    fun getMessagesMutableLiveData(): MutableLiveData<ArrayList<Message>>{
        return messageList
    }
    fun getUsersMutableLiveData(): MutableLiveData<ArrayList<User>> {
        return userList
    }
    fun currentUser() = auth.currentUser
    fun getUserLiveData(): MutableLiveData<FirebaseUser>{
        return userMutableLiveData
    }
    fun getContactMutableLiveData(): MutableLiveData<ArrayList<Contact>>{
        return contactList
    }
    fun getEmailExistLiveData(): MutableLiveData<Boolean> {
        return emailExist
    }
    fun getInvalidUser(): MutableLiveData<Boolean> {
        return invalidUser
    }
    fun getLoading(): MutableLiveData<Boolean> {
        return loading
    }
    fun getNetwork(): MutableLiveData<Boolean>{
        return networkError
    }
    fun getBadlyFormat(): MutableLiveData<Boolean>{
        return badlyFormat
    }

}