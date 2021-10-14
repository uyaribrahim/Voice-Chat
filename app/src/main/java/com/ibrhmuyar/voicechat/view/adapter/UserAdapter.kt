package com.ibrhmuyar.voicechat.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.model.User
import com.ibrhmuyar.voicechat.view.ChatActivity
import kotlinx.android.synthetic.main.item_users.view.*

class UserAdapter (val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    lateinit var context: Context

    class UserViewHolder(var view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_users,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        Glide.with(context)
            .load(userList[position].profileImgUrl)
            .placeholder(R.drawable.place_holder)
            .circleCrop()
            .override(200,200)
            .into(holder.view.userImage)

        if(position % 2 == 0){
            holder.view.relativeLayout.setBackgroundColor(Color.parseColor("#C9D0CA"))
        }else{
            holder.view.relativeLayout.setBackgroundColor(Color.parseColor("#E8EFE9"))
        }

        holder.view.userName.text = userList[position].userName
        holder.view.setOnClickListener {
            context.startActivity(Intent(this.context, ChatActivity::class.java)
                .putExtra("userName", userList[position].userName)
                .putExtra("uId", userList[position].userId)
                .putExtra("imageUrl", userList[position].profileImgUrl))
        }
        /*holder.view.setOnClickListener(View.OnClickListener {
            context.startActivity(Intent(this.context,ChatActivity::class.java).putExtra("userName", userList[position].userName)
                .putExtra("userId", userList[position].userId))
        })*/
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    fun updateList(newUserList: List<User>, context: Context){
        this.context = context
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }
}