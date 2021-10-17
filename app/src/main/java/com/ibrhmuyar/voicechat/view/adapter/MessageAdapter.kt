package com.ibrhmuyar.voicechat.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.model.Message
import java.util.logging.Handler
import kotlin.collections.ArrayList


class MessageAdapter(val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private var audioPlayer: PlayAudio = PlayAudio()
    private lateinit var mPlayer: MediaPlayer
    private var msgAudio: ArrayList<Message> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var duration: Int = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    lateinit var context: Context

    val MSG_LEFT = 0
    val MSG_RIGHT = 1


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    var currentUser = auth.currentUser
    var uId = currentUser?.uid.toString()


    interface OnItemClickListener{
        fun onItemClick(view: View, button: Button, seekBar: SeekBar, loadAudioProgressBar: ProgressBar,
                        msgAudio: Message, position: Int)
    }
    fun setOnItemClickListener(itemClickListener: OnItemClickListener){
        this.onItemClickListener = itemClickListener
    }

    class MessageViewHolder(var view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == MSG_RIGHT){
            var view = LayoutInflater.from(context).inflate(R.layout.message_item_right,parent,false)
            return SenderViewHolder(view)
        }else{
            var view = LayoutInflater.from(context).inflate(R.layout.message_item_left,parent,false)
            return ReceiveViewHolder(view)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var msg: Message = messageList[position]
        if(holder.javaClass == SenderViewHolder::class.java){
            val viewHolder = holder as SenderViewHolder
            viewHolder.senderPlayButton.setOnClickListener {
                if(onItemClickListener != null){
                    onItemClickListener!!.onItemClick(it,viewHolder.senderPlayButton,
                        viewHolder.seekBarSender,
                        viewHolder.loadAudioProgress,msg,position)
                }
            }
            viewHolder.seekBarSender.setOnTouchListener(object : View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return true
                }
            })

        }else{
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiverPlayButton.setOnClickListener{
                if(onItemClickListener != null){
                    onItemClickListener!!.onItemClick(it,viewHolder.receiverPlayButton,
                        viewHolder.seekBarReceiver,
                        viewHolder.loadAudioProgressReceiver,msg,position)
                }
            }
            viewHolder.seekBarReceiver.setOnTouchListener(object : View.OnTouchListener{
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return true
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class SenderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val senderPlayButton = itemView.findViewById<Button>(R.id.senderPlayButton)
        val loadAudioProgress = itemView.findViewById<ProgressBar>(R.id.loadAudioProgress)
        val seekBarSender = itemView.findViewById<AppCompatSeekBar>(R.id.seekBarSender)
    }
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiverPlayButton = itemView.findViewById<Button>(R.id.receiverPlayButton)
        val loadAudioProgressReceiver = itemView.findViewById<ProgressBar>(R.id.loadAudioProgressReceiver)
        val seekBarReceiver = itemView.findViewById<AppCompatSeekBar>(R.id.seekBarReceiver)
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