package com.ibrhmuyar.voicechat.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.view.adapter.ContactsAdapter
import com.ibrhmuyar.voicechat.view.adapter.UserAdapter
import com.ibrhmuyar.voicechat.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment() {

    private lateinit var viewmodel : ContactViewModel
    private val contactsAdapter = ContactsAdapter(arrayListOf())

    private var CHANNEL_ID = "new_messagee"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*createNotificationChannel()
        sendNotification()
*/

        viewmodel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        contactsRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        contactsRecyclerView.adapter = contactsAdapter

        viewmodel.getUsersContacts()

        viewmodel.contactList.observe(viewLifecycleOwner, Observer {
            contactsRecyclerView.visibility = View.VISIBLE
            if(it.isEmpty()){
                chatLinearLayout.visibility = View.VISIBLE
            }else{
                chatLinearLayout.visibility = View.GONE
            }
            context?.let { it1 -> contactsAdapter.updateList(it, it1) }
        })
        viewmodel.loading.observe(viewLifecycleOwner, Observer {
            if(it){
                contactLoadingProgressBar.visibility = View.VISIBLE
            }
            else{
                contactLoadingProgressBar.visibility = View.GONE
            }
        })
    }



}