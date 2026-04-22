package com.leo.zamtrivia.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.leo.zamtrivia.R

class NameInputDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name_input, null)
        val editName = view.findViewById<TextInputEditText>(R.id.editPlayerName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enter_name_title))
            .setView(view)
            .setPositiveButton(getString(R.string.continue_text), null)
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> dismiss() }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.lt_primary)
            )
            negativeButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.lt_secondary)
            )

            positiveButton.setOnClickListener {
                val name = editName.text?.toString()?.trim().orEmpty()
                if (name.isBlank()) {
                    editName.error = getString(R.string.name_required)
                    return@setOnClickListener
                }

                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(BUNDLE_NAME to name)
                )
                dismiss()
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "NameInputDialog"
        const val REQUEST_KEY = "name_input_request"
        const val BUNDLE_NAME = "player_name"
    }
}