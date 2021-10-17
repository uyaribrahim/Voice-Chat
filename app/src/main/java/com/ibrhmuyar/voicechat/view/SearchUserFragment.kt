package com.ibrhmuyar.voicechat.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibrhmuyar.voicechat.R
import kotlinx.android.synthetic.main.fragment_search_user.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView

import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrhmuyar.voicechat.model.User
import com.ibrhmuyar.voicechat.view.adapter.UserAdapter
import com.ibrhmuyar.voicechat.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_chats.*


class SearchUserFragment : Fragment() {


    private val useradapter = UserAdapter(arrayListOf())
    private var contactViewModel: ContactViewModel = ContactViewModel()
    private val emptyList: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        inputSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isNotEmpty()){
                    contactViewModel.getUsers(inputSearch.text.toString())
                }else{
                    context?.let { useradapter.updateList(emptyList, it) };
                    searchImage.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        usersRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        usersRecyclerView.adapter = useradapter


        contactViewModel.userList.observe(viewLifecycleOwner, Observer {
            usersRecyclerView.visibility = View.VISIBLE
            searchImage.visibility = View.GONE
            context?.let { it1 -> useradapter.updateList(it, it1) }
        })
        contactViewModel.loading.observe(viewLifecycleOwner, Observer {
            if(it){
                usersLoadingProgress.visibility = View.VISIBLE
            }
            else{
                usersLoadingProgress.visibility = View.GONE
            }
        })

        /*inputSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return@OnEditorActionListener true
            }
            false
        })*/


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }


}