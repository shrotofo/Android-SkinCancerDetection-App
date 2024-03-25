package com.example.skin_ai

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity3 : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // Find the Button by its ID
        val btnNextScreen: Button = findViewById(R.id.btnNextScreen3)

        // Find the ImageView by its ID
        val imageView: ImageView = findViewById(R.id.imageView)

        // Retrieve the captured image path from the intent
        val imagePath = intent.getStringExtra("image_path")
        val imageURI = intent.getStringExtra("image_uri")



        // Check if imagePath is not null
        if (imagePath != null) {
            // Load and display the image in the second ImageView
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageView.setImageBitmap(bitmap)
        }else if (imageURI != null){
            //val bitmap = BitmapFactory.decodeFile(imageURI)
            imageView.setImageURI(Uri.parse(imageURI))
        }

        // Set a click listener for the Button
        btnNextScreen.setOnClickListener {
            // Create an Intent to navigate to the next screen
            if(imagePath!=null) {
                val intent = Intent(this, MainActivity4::class.java)
                intent.putExtra("image_path", imagePath)
                startActivity(intent)
            }else{
                val intent = Intent(this, MainActivity4::class.java)
                intent.putExtra("image_uri", imageURI)
                startActivity(intent)
            }
        }
    }
}
