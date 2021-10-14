package com.ibrhmuyar.voicechat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibrhmuyar.voicechat.R
import kotlinx.android.synthetic.main.fragment_search_user.*
import android.view.inputmethod.EditorInfo

import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrhmuyar.voicechat.view.adapter.UserAdapter


class SearchUserFragment : Fragment() {


      private val useradapter = UserAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        usersRecyclerView.adapter = useradapter

        inputSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }

}