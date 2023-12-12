package com.example.ecommercefurniture.dialog

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ecommercefurniture.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomSheetDialog(
    onSendClick: (String) -> Unit
)  {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val btnSend = view.findViewById<Button>(R.id.btnSendResetPw)
    val btnCancel = view.findViewById<Button>(R.id.btnCancelResetPw)


    btnSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        if (email.isEmpty())
        {
            return@setOnClickListener Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()

        }
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}