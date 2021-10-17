package com.ibrhmuyar.voicechat.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ibrhmuyar.voicechat.view.adapter.MessageAdapter
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.model.Message
import com.ibrhmuyar.voicechat.view.adapter.MessageAdapter.OnItemClickListener
import kotlinx.android.synthetic.main.activity_chat.*

import com.ibrhmuyar.voicechat.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.activity_chat.btnRecord
import java.lang.Exception
import kotlinx.coroutines.Runnable
import android.view.View.OnTouchListener
import android.widget.SeekBar
import com.google.firebase.database.FirebaseDatabase
import java.io.*


private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class ChatActivity : AppCompatActivity() {

    private lateinit var contactViewModel: ContactViewModel

    private var storeReference: StorageReference = FirebaseStorage.getInstance().getReference()

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var myHandler: Handler = Handler()

    private var fileName: String = ""
    private var audioName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isRecording: Boolean = false
    private var isPlaying: Boolean = false

    private var starttime: Long = 0
    private var timerHandler = Handler()

    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var imageURL: String
    private lateinit var receiverName: String

    private val messageAdapter = MessageAdapter(arrayListOf())

    var senderRoom: String? = null
    var receiverRoom: String? = null

    private var receiverToken: String? = null

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val db = FirebaseDatabase.getInstance()

    private var myRef = db.reference



    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        contactViewModel.getCurrentUserData()

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
        var userToken = intent.getStringExtra("userToken")

        senderId = senderID
        receiverId = receiverID.toString()
        imageURL = imageUrl.toString()
        receiverName = userName.toString()
        receiverToken = userToken

        userNameChat.text = userName
        Glide.with(this)
            .load(imageUrl)
            .circleCrop()
            .override(200,200)
            .into(userChatImage)

        receiverRoom = senderID + receiverID
        senderRoom = receiverID + senderID

        contactViewModel.getMessages(senderRoom!!)


        btnRecord.setOnTouchListener(OnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                isRecording = true
                starttime = System.currentTimeMillis()
                timerHandler.postDelayed(timer,0)
                onRecord(isRecording)
                true
            }
            if(event.action == MotionEvent.ACTION_UP){
                isRecording = false
                timerHandler.removeCallbacks(timer);
                onRecord(isRecording)
                txtRecordingTime.text = "0:00"
                true
            }
            else true
            // consume the event
        })

        messageAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, button: Button, seekBar: SeekBar,
                                     loadAudioProgress: ProgressBar,
                                     msgAudio: Message, position: Int) {

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser){
                            player!!.seekTo(progress)
                            seekBar?.progress = progress
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }


                })

                if(button.tag == "STOP"){
                    isPlaying = false
                    player!!.stop()
                    player!!.reset()
                    player!!.release()
                    player = null
                    button.tag = "PLAY"
                    button.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                }else{

                    button.visibility = View.GONE
                    loadAudioProgress.visibility = View.VISIBLE

                    var runnable = Runnable {
                        run {
                            try {
                                player = MediaPlayer()
                                player!!.setDataSource(msgAudio.message)
                                player!!.prepareAsync()
                                player!!.setOnPreparedListener {
                                    loadAudioProgress.visibility = View.GONE
                                    button.visibility = View.VISIBLE
                                    it.start()
                                    isPlaying = true
                                    button.tag = "STOP"
                                    button.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                                    updateSeekBar(seekBar)
                                    Log.d("Prog", "run: " + player!!.getDuration())
                                    seekBar.max = player!!.duration
                                    updateSeekBar(seekBar)

                                }
                                player!!.setOnCompletionListener {
                                    isPlaying = false
                                    it.reset()
                                    it.release()
                                    button.tag = "PLAY"
                                    button.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                                    Log.d("Progg", "finish: " + "bitti");
                                }

                            }catch (e: Exception){}
                        }
                    }
                    myHandler.postDelayed(runnable,400)
                }

            }
        })
        //************ Mesajı database e gönder
           /* btnRecord.setOnClickListener {
                if(inputMessage.text.isNotEmpty()){
                    var message = inputMessage.text.toString()
                    contactViewModel.addMessageToDatabase(message,senderRoom!!,receiverRoom!!,senderID)
                    inputMessage.text.clear()
                }
            }*/

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

    var timer: Runnable = Runnable {
        run {
            val millis = System.currentTimeMillis() - starttime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            txtRecordingTime.text = String.format("%d:%02d", minutes, seconds)

            timerHandler.postDelayed(timer, 500)
        }
    }



    fun updateSeekBar(seekBar: SeekBar){

         var runnable = Runnable {
            run {
                    Log.e("GGGG","Running")
                if (isPlaying){
                    seekBar.progress = player!!.currentPosition
                    updateSeekBar(seekBar)
                }else{
                    seekBar.progress = 0
                }
            }
        }
            myHandler.postDelayed(runnable,200)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        Log.e("####", "kayıt başladı")
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        /*Log.e("####", "oynatma başladı")
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }*/
        val audioUrl = "https://firebasestorage.googleapis.com/v0/b/voice-chat-eff0c.appspot.com/" +
                "o/audio%2F1634238813494chat.mp3?alt=media&token=d0c43b03-a7af-42f2-9aca-88f41b6e3486"

        player = MediaPlayer()

        player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            player!!.setDataSource(audioUrl)

            player!!.prepare()
            player!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()

    }

    private fun stopPlaying() {
        Log.e("####", "oynatma durdu")
        player?.release()
        player = null
    }

    private fun startRecording() {
        audioName = "${System.currentTimeMillis()}chat.mp3"
        fileName = "${externalCacheDir?.absolutePath}/"+audioName
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        Log.e("####", "stopRecording() kayıt durdu")
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        uploadToFirebase()
    }

    fun uploadToFirebase() {
        var uri: Uri = Uri.fromFile(File(fileName))
        var fileRef: StorageReference = storeReference.child("audio")
            .child(audioName)
        fileRef.putFile(uri).addOnSuccessListener(OnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                var audioUrl = it.toString()
                contactViewModel.addMessageToDatabase(audioUrl,senderRoom!!,receiverRoom!!,
                    senderId,receiverId,imageURL,receiverName,receiverToken.toString())
                //saveDataToFirebase()
                //uploadingProgressBar.visibility = View.GONE
            }
        })
            .addOnFailureListener {
                Toast.makeText(this, "Resim ekleme işlemi başarısız", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStop() {
        super.onStop()
        isPlaying = false
        recorder?.release()
        recorder = null
        player?.release()
        player = null
        finish()
    }

}