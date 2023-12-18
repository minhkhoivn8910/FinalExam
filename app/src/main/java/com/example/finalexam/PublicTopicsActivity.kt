package com.example.finalexam

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*

class PublicTopicsActivity : AppCompatActivity() {
    private lateinit var topicListView: ListView
    private val publicTopicsList: MutableList<Topic> = mutableListOf()
    private val topicsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("topics")
    private lateinit var topicList: MutableList<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_topics)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Public Topics"

        topicListView = findViewById(R.id.publicTopicsListView)

        displayTopicListByFolder()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayTopicListByFolder() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("topics")
        val query = databaseReference.orderByChild("status").equalTo("Public")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                topicList = mutableListOf()

                for (topicSnapshot in dataSnapshot.children) {
                    val topic = topicSnapshot.getValue(Topic::class.java)
                    topic?.let {
                        it.id = topicSnapshot.key
                        topicList.add(it)
                    }
                }

                val adapter = ArrayAdapter(this@PublicTopicsActivity, android.R.layout.simple_list_item_1, topicList.map { "${it.name} - ${it.status}" })
                topicListView.adapter = adapter

                // Xử lý sự kiện khi một topic được chọn
                topicListView.setOnItemClickListener { _, _, position, _ ->
                    val selectedTopic = topicList[position]
                    openVocabularyPage(selectedTopic.name ?: "")
                }

                registerForContextMenu(topicListView)
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    private fun openVocabularyPage(topicName: String) {
        val intent = Intent(this, VocabularyActivity::class.java)
        intent.putExtra("topicName", topicName)
        startActivity(intent)
    }
}
