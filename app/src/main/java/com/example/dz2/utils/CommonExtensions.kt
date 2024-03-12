package com.example.dz2.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

fun <T : Fragment> T.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
