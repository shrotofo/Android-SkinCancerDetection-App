package com.example.skin_ai

import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity


import android.graphics.Bitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream


class MainActivity5 : FragmentActivity() {
    private val class_names = arrayOf("Acne", "Pale_skintone", "Pigmentation", "Pore_Quality", "Wrinkled", "dark_skintone", "light_skintone", "medium_skintone")
    var bitmapFromURI: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        // Find the ImageView and TextView by their IDs
        val imageView: ImageView = findViewById(R.id.imageView)
        val myImageView: ImageView = findViewById(R.id.myImageView)
        val resultTextView: TextView = findViewById(R.id.resultTextView)

        // Retrieve the captured image path from the intent
        val imagePath = intent.getStringExtra("image_path")
        val imageURI = intent.getStringExtra("image_uri")
// Load your PyTorch model
        val module = Module.load(assetFilePath("skinclassifier_oncpu.pt"))

        // Load the image as a Bitmap
        val imageBitmap: Bitmap = if (imagePath != null) {
            BitmapFactory.decodeFile(imagePath)
        } else {
            MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imageURI))
        }

        // Update the ImageView with the selected image
        imageView.setImageBitmap(imageBitmap)

        // Preprocess the image and convert to Tensor
        val inputTensor = preprocessImage(imageBitmap)

        // Perform inference and get the result
        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray
        val maxScoreIdx = scores.indices.maxByOrNull { scores[it] } ?: -1

        // Assuming you have the class names array as in your Python script
        val predictedClass = class_names[maxScoreIdx]
        val confidence = scores[maxScoreIdx]

        // Display the prediction result in the TextView
        resultTextView.text = "Predicted Class: $predictedClass, Confidence: $confidence"

        // Don't forget to close the module to free up resources
        module.destroy()
    }

    private fun preprocessImage(bitmap: Bitmap): Tensor {
        // Preprocess the image to a 224x224 RGB image and normalize as required by your model
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        return TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
    }

    private fun assetFilePath(assetName: String): String {
        val file = File(filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        this.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }
}