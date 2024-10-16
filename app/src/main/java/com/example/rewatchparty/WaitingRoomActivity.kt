package com.example.rewatchparty

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.LinearLayout

class WaitRoomActivity : AppCompatActivity() {

    private lateinit var searchInput: SearchView
    private lateinit var usersListView: ListView
    private lateinit var userContainer: LinearLayout
    private lateinit var adapter: ArrayAdapter<String>

    private val addedUsers = mutableSetOf<String>()

    private val usersList = listOf("John Doe", "Jane Smith", "Alice Johnson", "Bob Brown", "Chris Evans")

    private val emptyList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waiting_room_activity)
        searchInput = findViewById(R.id.searchInput)
        usersListView = findViewById(R.id.usersListView)
        userContainer = findViewById(R.id.userContainer)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usersList)
        usersListView.adapter = adapter
        searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.clear()
                } else {
                    adapter = ArrayAdapter(this@WaitRoomActivity, android.R.layout.simple_list_item_1, usersList)
                    usersListView.adapter = adapter
                    adapter.filter.filter(newText)
                }
                return false
            }
        })

        usersListView.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = adapter.getItem(position)
            if (selectedUser != null) {
                if (!addedUsers.contains(selectedUser)) {
                    addUserToContainer(selectedUser)
                    addedUsers.add(selectedUser)
                } else {
                    Toast.makeText(this, "$selectedUser is already in the waiting room", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addUserToContainer(userName: String) {
        val inflater = LayoutInflater.from(this)
        val userCardView = inflater.inflate(R.layout.user_card_view, userContainer, false) as CardView

        val userNameText = userCardView.findViewById<TextView>(R.id.userName)
        val userImageView = userCardView.findViewById<ImageView>(R.id.userImage)
        userNameText.text = userName
        userImageView.setImageResource(R.drawable.ic_user_placeholder)

        userContainer.addView(userCardView)
    }
}
