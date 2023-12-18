package com.example.finalexam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private val firebaseHelper = FirebaseHelper()
    private lateinit var folderAdapter: ArrayAdapter<String>
    private lateinit var selectedFolderId: String
    val folderList: MutableList<String> = mutableListOf()
    private var snapshot: DataSnapshot? = null
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = intent.getStringExtra("UserId") ?: ""
        val folderListView: ListView = findViewById(R.id.folderListView)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        folderAdapter = ArrayAdapter(this, R.layout.list_item_folder, R.id.folderNameTextView, folderList)
        folderListView.adapter = folderAdapter

        val folderNameEditText: EditText = findViewById(R.id.folderNameEditText)
        val submitButton: Button = findViewById(R.id.submitButton)

        // Hiển thị danh sách Folder
        firebaseHelper.getFolders(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                snapshot = dataSnapshot  // Lưu trữ snapshot tại đây
                folderList.clear()
                for (folderSnapshot in dataSnapshot.children) {
                    val folder = folderSnapshot.getValue(Folder::class.java)
                    folder?.let { folderList.add(it.name.orEmpty()) }
                }

                // Cập nhật ListView khi có dữ liệu mới
                folderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })

        // Xử lý sự kiện khi bấm nút "Submit"
        submitButton.setOnClickListener {
            val folderName = folderNameEditText.text.toString().trim()

            if (folderName.isNotEmpty()) {
                // Thêm Folder mới
                val newFolder = Folder(name = folderName)
                firebaseHelper.addFolder(newFolder)

                // Xóa nội dung của EditText sau khi thêm Folder
                folderNameEditText.text.clear()

                updateListView()
            }
        }

        // Đăng ký ContextMenu cho ListView
        registerForContextMenu(folderListView)

        // Xử lý sự kiện khi người dùng chọn một mục trong ListView
        folderListView.setOnItemLongClickListener { _, _, position, _ ->
            val folderSnapshot = folderListView.getItemAtPosition(position) as? String
            if (folderSnapshot != null) {
                firebaseHelper.getFolderIdByName(folderSnapshot) { folderId ->
                    Log.d("FolderId", "Folder ID from getFolderIdByName: $folderId")
                    if (folderId != null) {
                        selectedFolderId = folderId
                    } else {
                    }
                }
            }

            false
        }
        folderListView.setOnItemClickListener { _, _, position, _ ->
            // Lấy tên của Folder được chọn
            val selectedFolderName = folderList[position]

            // Chuyển tới trang topic của Folder bằng Intent hoặc một phương pháp khác tùy thuộc vào cách bạn tổ chức Activity của ứng dụng
            // Ví dụ sử dụng Intent:
            val intent = Intent(this, TopicActivity::class.java)
            intent.putExtra("folderName", selectedFolderName)
            startActivity(intent)
        }


    }

    // Tạo ContextMenu
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    // Xử lý sự kiện khi người dùng chọn một mục trong ContextMenu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuUpdate -> {
                if (::selectedFolderId.isInitialized && selectedFolderId != null) {
                    // Gọi hàm showUpdateDialog với selectedFolderId được truyền vào
                    showUpdateDialog(selectedFolderId)
                } else {
                    // Handle the case where selectedFolderId is not initialized or is null
                }
                return true
            }
            R.id.menuDelete -> {
                // Xử lý khi chọn Delete
                // Gọi hàm xóa Folder theo ID (selectedFolderId)
                if (::selectedFolderId.isInitialized && selectedFolderId != null) {
                    firebaseHelper.deleteFolder(selectedFolderId)
                    updateListView()
                } else {
                    // Handle the case where selectedFolderId is not initialized or is null
                }
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }


    fun updateListView() {
        // Hiển thị danh sách Folder
        firebaseHelper.getFolders(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                snapshot = dataSnapshot  // Lưu trữ snapshot tại đây
                folderList.clear()
                for (folderSnapshot in dataSnapshot.children) {
                    val folder = folderSnapshot.getValue(Folder::class.java)
                    folder?.let { folderList.add(it.name.orEmpty()) }
                }

                // Cập nhật ListView khi có dữ liệu mới
                folderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }
    fun showUpdateDialog(selectedFolderId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Folder Name")

        // Định nghĩa layout cho dialog
        val view = layoutInflater.inflate(R.layout.dialog_update_folder, null)
        builder.setView(view)

        val folderNameEditText = view.findViewById<EditText>(R.id.updateFolderNameEditText)

        // Kiểm tra xem selectedFolderId có tồn tại trong folderList hay không
        val folderIndex = folderList.indexOf(selectedFolderId)
        if (folderIndex != -1) {
            // Nếu tồn tại, sử dụng nó để thiết lập giá trị cho EditText
            folderNameEditText.setText(folderList[folderIndex])
        } else {
            // Xử lý trường hợp không tìm thấy selectedFolderId trong folderList
            Log.e("UpdateDialog", "Selected folder not found in folderList")
        }

        builder.setPositiveButton("Submit") { _, _ ->
            // Lấy tên mới từ EditText
            val updatedFolderName = folderNameEditText.text.toString().trim()

            // Thực hiện cập nhật folder
            if (updatedFolderName.isNotEmpty()) {
                firebaseHelper.updateFolder(Folder(id = selectedFolderId, name = updatedFolderName))
                updateListView()
            } else {
                // Xử lý trường hợp tên folder mới trống
                Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                openSettingsPage()
                return true
            }
            R.id.action_public_topics -> {
                openPublicTopicsPage()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun openSettingsPage() {
        val intent = Intent(this, Setting::class.java)
        intent.putExtra("UserId", userId)
        startActivity(intent)
    }
    private fun openPublicTopicsPage() {
        val intent = Intent(this, PublicTopicsActivity::class.java)
        startActivity(intent)
    }
}



