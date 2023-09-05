package com.example.web3mall.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.web3mall.R

class ContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(com.google.android.material.R.id.container, FirstFragment())
                .commit()
        }
    }
}