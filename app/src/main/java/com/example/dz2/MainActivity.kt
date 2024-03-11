package com.example.dz2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.example.dz34.utils.hideKeyboardAndClearFocus

class MainActivity : AppCompatActivity(R.layout.activity_main){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        hideKeyboardAndClearFocus()
    }
}

/**
 * Process SSL handshake (trust all certificates)
 */