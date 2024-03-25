package com.example.skin_ai

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.View
import android.widget.Button

class MainActivity4 : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        // Existing code for loading MainFragment

        // Find the Button by its ID
        val btnNextScreen: Button = findViewById(R.id.btnNextScreen4)
        // Retrieve the captured image path from the intent
        val imagePath = intent.getStringExtra("image_path")

        val imageURI = intent.getStringExtra("image_uri")

        // Set a click listener for the Button
        btnNextScreen.setOnClickListener {
            // Create an Intent to navigate to the next screen
            if(imagePath!=null){
                val intent = Intent(this, MainActivity5::class.java)
                intent.putExtra("image_path", imagePath)
                startActivity(intent)
            }else{
                val intent = Intent(this, MainActivity5::class.java)
                intent.putExtra("image_uri", imageURI)
                startActivity(intent)
            }

        }
    }
}