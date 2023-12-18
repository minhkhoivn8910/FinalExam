package com.example.finalexam

import com.google.firebase.database.*

class FirebaseHelper {

    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("folders")
    }

    fun addFolder(folder: Folder): String {
        val key = databaseReference.push().key
        key?.let {
            databaseReference.child(it).setValue(folder)
        }
        return key.orEmpty()
    }

    fun getFolders(listener: ValueEventListener) {
        databaseReference.addListenerForSingleValueEvent(listener)
    }

    fun updateFolder(folder: Folder) {
        folder.id?.let {
            databaseReference.child(it).setValue(folder)
        }
    }

    fun deleteFolder(folderId: String) {
        databaseReference.child(folderId).removeValue()
    }

    fun getFolderIdByName(folderName: String, callback: (String?) -> Unit) {
        databaseReference.orderByChild("name").equalTo(folderName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val folderId = snapshot.children.firstOrNull()?.key
                callback.invoke(folderId)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.invoke(null)
            }
        })
    }

    fun getUserIdByEmail(email: String, callback: (String?) -> Unit) {
        val usersReference = FirebaseDatabase.getInstance().reference.child("Users")

        val query: Query = usersReference.orderByChild("uemail").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var uId: String? = null

                for (userSnapshot in dataSnapshot.children) {
                    uId = userSnapshot.key
                    break
                }

                callback(uId)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                callback(null)
            }
        })
    }


}
