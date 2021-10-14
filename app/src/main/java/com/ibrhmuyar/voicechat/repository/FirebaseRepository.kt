package com.ibrhmuyar.voicechat.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.ibrhmuyar.voicechat.model.Message
import com.ibrhmuyar.voicechat.model.User

class FirebaseRepository {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val db = FirebaseDatabase.getInstance()


    private var myRef = db.reference

    private var userMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private var emailExist: MutableLiveData<Boolean> = MutableLiveData()
    private var invalidUser: MutableLiveData<Boolean> = MutableLiveData()
    private var loading: MutableLiveData<Boolean> = MutableLiveData()
    private var networkError: MutableLiveData<Boolean> = MutableLiveData()
    private var badlyFormat: MutableLiveData<Boolean> = MutableLiveData()
    private var userList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private var messageList: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    //mesajı gönder
    fun addMessageToDatabase(message: String, senderRoom: String,receiverRoom:String, senderID: String){
        loading.value = true
        val msg = Message(message,senderID)
        myRef.child("rooms").child(senderRoom).child("messages").push()
            .setValue(msg).addOnSuccessListener {
                myRef.child("rooms").child(receiverRoom).child("messages").push()
                    .setValue(msg).addOnSuccessListener {
                        loading.value = false
                    }
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

    //Veritabanından kullanıcıları al
    fun getUsers(){

        var list: ArrayList<User> = ArrayList()
            myRef.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(User::class.java)
                    if(auth.uid != user?.userId){
                        list.add(user!!)
                    }
                }
                userList.postValue(list)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //Yeni kayıt olan kullanıcı bilgilerini veritabanına kaydet
    fun saveUserDataToDatabase(email: String,username: String){
        val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/voice-chat-eff0c.appspot.com/o/20211005_012704.jpg?alt=media&token=af8d40b5-a3cb-43d9-8663-eaf171aba250"
        val user = User(auth.uid.toString() ,username,email,defaultImageUrl)
        myRef = FirebaseDatabase.getInstance().reference
        myRef.child("users").child(auth.uid.toString()).setValue(user)
        loading.value = false
    }

    fun signup(email: String, pass: String, username: String){
        loading.value = true
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    saveUserDataToDatabase(email,username)
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