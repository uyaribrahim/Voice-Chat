package com.ibrhmuyar.voicechat.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.model.Message
import kotlinx.android.synthetic.main.message_item_left.view.*


class MessageAdapter(val messageList: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    lateinit var context: Context

    val MSG_LEFT = 0
    val MSG_RIGHT = 1


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var currentUser = auth.currentUser
    var uId = currentUser?.uid.toString()


    class MessageViewHolder(var view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        if(viewType == MSG_RIGHT){
            var view = LayoutInflater.from(context).inflate(R.layout.message_item_right,parent,false)
            return MessageViewHolder(view)
        }else{
            var view = LayoutInflater.from(context).inflate(R.layout.message_item_left,parent,false)
            return MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {


        //Log.e("88888", position.toString())
        //Log.e("7777", messageList.size.toString())
        //Log.e("8888", messageList?.get(position)?.message.toString())

        holder.view.txtMessage.text = messageList[position].message.toString()
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList[position].senderID.equals(uId)){
            return MSG_RIGHT
        }else{
            return MSG_LEFT
        }
    }

    fun updateList(newMessageList: ArrayList<Message>, context: Context){
        this.context = context
        messageList.clear()
        messageList.addAll(newMessageList)
        notifyDataSetChanged()
    }
}