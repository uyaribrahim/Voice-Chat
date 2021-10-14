package com.ibrhmuyar.voicechat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.view.adapter.UserAdapter
import com.ibrhmuyar.voicechat.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment() {

    private lateinit var viewmodel : ContactViewModel
    private val userAdapter = UserAdapter(arrayListOf())

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


        viewmodel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        viewmodel.getUsers()
        contactsRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        contactsRecyclerView.adapter = userAdapter

        viewmodel.userList.observe(viewLifecycleOwner, Observer {
            contactsRecyclerView.visibility = View.VISIBLE
            context?.let { it1 -> userAdapter.updateList(it, it1) }
        })
        viewmodel.loading.observe(viewLifecycleOwner, Observer {
            if(it){
                contactLoadingProgressBar.visibility = View.VISIBLE
            }
            else{
                contactLoadingProgressBar.visibility = View.GONE
            }
        })

        /*viewmodel.getRooms()

        viewmodel.contacts.observe(viewLifecycleOwner, Observer{
            contactsRecyclerView.visibility = View.VISIBLE
            context?.let { it1 -> contactsAdapter.updateList(it, it1) }
        })
        viewmodel.userLoading.observe(viewLifecycleOwner, Observer {
            if(it){

            }else{
                //usersLoadingProgress.visibility = View.GONE
                contactsRecyclerView.visibility = View.VISIBLE
            }
        })*/
    }


}