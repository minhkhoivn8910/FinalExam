package com.example.finalexam

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordDialog : DialogFragment() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_change_password, null)

        currentPasswordEditText = view.findViewById(R.id.editTextCurrentPassword)
        newPasswordEditText = view.findViewById(R.id.editTextNewPassword)

        builder.setView(view)
            .setTitle("Change Password")
            .setPositiveButton("Change") { _, _ ->
                handleChangePassword()
            }
            .setNegativeButton("Cancel", null)

        return builder.create()
    }

    private fun handleChangePassword() {
        val currentPassword = currentPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updatePasswordTask ->
                                if (updatePasswordTask.isSuccessful) {
                                    dismiss()
                                    showToast("Password updated successfully")
                                } else {
                                    showToast("Failed to update password")
                                }
                            }
                    } else {
                        showToast("Authentication failed. Please check your current password.")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
