package com.example.finalexam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopicActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var topicListView: ListView
    private lateinit var folderName: String
    private lateinit var topicNameEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var topicList: MutableList<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        toolbar = findViewById(R.id.toolbar)
        topicListView = findViewById(R.id.topicListView)

        // Lấy tên của Folder từ Intent
        folderName = intent.getStringExtra("folderName") ?: ""
        setupToolbar()

        topicNameEditText = findViewById(R.id.topicNameEditText)
        submitButton = findViewById(R.id.submitButton)

        // Xử lý sự kiện khi người dùng nhấn nút "Submit"
        submitButton.setOnClickListener {
            val topicName = topicNameEditText.text.toString().trim()

            if (topicName.isNotEmpty()) {
                // Thêm topic mới vào Firebase Realtime Database
                val newTopic = Topic(name = topicName, folderName = folderName)
                addTopicToFirebase(newTopic)
            } else {
                // Xử lý trường hợp tên topic trống
                Toast.makeText(this, "Topic name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Hiển thị danh sách Topic
        displayTopicListByFolder()

    }

    private fun setupToolbar() {
        toolbar.title = folderName
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun displayTopicListByFolder() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("topics")
        val query = databaseReference.orderByChild("folderName").equalTo(folderName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Cập nhật giá trị của topicList
                topicList = mutableListOf()

                for (topicSnapshot in dataSnapshot.children) {
                    val topic = topicSnapshot.getValue(Topic::class.java)
                    topic?.let {
                        it.id = topicSnapshot.key
                        topicList.add(it)
                    }
                }

                // Hiển thị danh sách Topic trong ListView
                val adapter = ArrayAdapter(this@TopicActivity, android.R.layout.simple_list_item_1, topicList.map { "${it.name} - ${it.status}" })
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




    private fun addTopicToFirebase(newTopic: Topic) {
        // Thêm topic mới vào Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("topics")
        val newTopicReference = databaseReference.push()

        // Set a unique ID for the new topic
        val topicId = newTopicReference.key ?: ""

        // Set the ID for the new topic
        newTopicReference.child("id").setValue(topicId)

        // Set the status for the new topic
        val statusRadioGroup = findViewById<RadioGroup>(R.id.statusRadioGroup)
        val selectedStatus = when (statusRadioGroup.checkedRadioButtonId) {
            R.id.radioPrivate -> "Private"
            R.id.radioPublic -> "Public"
            else -> "Unknown"
        }
        newTopic.status = selectedStatus

        // Lưu trữ topic vào database
        newTopicReference.setValue(newTopic)

        // Xóa nội dung của EditText sau khi thêm topic
        topicNameEditText.text.clear()

        // Cập nhật ListView
        displayTopicListByFolder()
    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_topic, menu)
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val selectedTopic = topicList[position]

        when (item.itemId) {
            R.id.menuUpdateTopic -> {
                // Handle the Update option
                showUpdateDialog(selectedTopic)
                return true
            }
            R.id.menuDeleteTopic -> {
                // Handle the Delete option
                deleteTopic(selectedTopic)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun deleteTopic(selectedTopic: Topic) {
        // Remove the topic from Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("topics")
        selectedTopic.id?.let { databaseReference.child(it).removeValue() }

        // Update the topic list and refresh the ListView
        displayTopicListByFolder()

        Toast.makeText(this, "Topic deleted successfully", Toast.LENGTH_SHORT).show()
    }
    private fun showUpdateDialog(selectedTopic: Topic) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Update Topic")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Add an EditText for the name
        val nameInput = EditText(this)
        nameInput.setText(selectedTopic.name)
        layout.addView(nameInput)

        // Add an EditText for the status
        val statusInput = EditText(this)
        statusInput.setText(selectedTopic.status)
        layout.addView(statusInput)

        alertDialogBuilder.setView(layout)

        alertDialogBuilder.setPositiveButton("Update") { _, _ ->
            val updatedName = nameInput.text.toString().trim()
            val updatedStatus = statusInput.text.toString().trim()

            if (updatedName.isNotEmpty()) {
                // Update the topic name and status in Firebase Realtime Database
                val databaseReference = FirebaseDatabase.getInstance().getReference("topics")
                val topicId = selectedTopic.id

                if (topicId != null) {
                    // Update the name and status fields of the selected topic
                    selectedTopic.name = updatedName
                    selectedTopic.status = updatedStatus

                    // Update the topic in Firebase Realtime Database
                    databaseReference.child(topicId).setValue(selectedTopic)

                    // Update the topic list and refresh the ListView
                    displayTopicListByFolder()

                    Toast.makeText(this, "Topic updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Topic ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Topic name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun openVocabularyPage(topicName: String) {
        val intent = Intent(this, VocabularyActivity::class.java)
        intent.putExtra("topicName", topicName)
        startActivity(intent)
    }

}