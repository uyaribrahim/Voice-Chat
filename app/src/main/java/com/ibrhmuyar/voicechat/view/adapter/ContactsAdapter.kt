package com.ibrhmuyar.voicechat.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.model.Contact
import com.ibrhmuyar.voicechat.model.User
import com.ibrhmuyar.voicechat.view.ChatActivity
import kotlinx.android.synthetic.main.item_users.view.*

class ContactsAdapter  (val contactList: ArrayList<Contact>): RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {


    lateinit var context: Context

    class ContactsViewHolder(var view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_users,parent,false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {

        Glide.with(context)
            .load(contactList[position].userImgUrl)
            .circleCrop()
            .placeholder(R.drawable.place_holder)
            .override(200,200)
            .into(holder.view.userImage)

        if(position % 2 == 0){
            holder.view.relativeLayout.setBackgroundColor(Color.parseColor("#C9D0CA"))
        }else{
            holder.view.relativeLayout.setBackgroundColor(Color.parseColor("#E8EFE9"))
        }

        holder.view.userName.text = contactList[position].userName
        holder.view.setOnClickListener {
            context.startActivity(
                Intent(this.context, ChatActivity::class.java)
                .putExtra("userName", contactList[position].userName)
                .putExtra("uId", contactList[position].userID)
                .putExtra("imageUrl", contactList[position].userImgUrl)
                .putExtra("userToken", contactList[position].userToken))
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
    fun updateList(newContactList: List<Contact>, context: Context){
        this.context = context
        contactList.clear()
        contactList.addAll(newContactList)
        notifyDataSetChanged()
    }
}