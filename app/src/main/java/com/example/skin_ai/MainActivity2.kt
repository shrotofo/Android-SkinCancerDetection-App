package com.example.skin_ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview



@Suppress("DEPRECATION")
class MainActivity2 : FragmentActivity() {

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var surfaceView: SurfaceView
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var previewView: PreviewView
    private lateinit var gallery_iv: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        previewView = findViewById(R.id.previewView)
        val btnNextScreen: Button = findViewById(R.id.btnNextScreen)
        val btn_gallery: Button = findViewById(R.id.btn_gallery)
        gallery_iv = findViewById(R.id.gallery_iv)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera(previewView)  // Use previewView.surfaceProvider instead of surfaceView.holder
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        outputDirectory = getOutputDirectory()

        // Set a click listener for the Button
        btnNextScreen.setOnClickListener {
            // Capture the image
            captureImage()
        }
        btn_gallery.setOnClickListener {
            // Pick image from Gallery
            if(galleryPermissionsGranted()){
                selectImageInAlbum()
            }else{
                ActivityCompat.requestPermissions(
                    this@MainActivity2,
                    GALLERY_PERMISSION,
                    REQUEST_SELECT_IMAGE_IN_ALBUM
                )
            }

        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun startCamera(previewView: PreviewView) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, previewView)
        }, ContextCompat.getMainExecutor(this))
    }


    private fun bindPreview(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val imageAnalysis = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                    // Handle image analysis if needed
                    image.close()
                })
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch (exc: Exception) {
            Toast.makeText(this, "Error: ${exc.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image: ${exc.message}", exc)
                    Toast.makeText(
                        this@MainActivity2,
                        "Error capturing image: ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Image capture is successful, navigate to the next screen
                    val intent = Intent(this@MainActivity2, MainActivity3::class.java)
                    intent.putExtra("image_path", photoFile.absolutePath)
                    startActivity(intent)
                }
            }
        )
    }



    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                this@MainActivity2, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun galleryPermissionsGranted() =
        GALLERY_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                this@MainActivity2, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK){


            //gallery_iv.setImageURI()

            val intent = Intent(this@MainActivity2, MainActivity3::class.java)
            intent.putExtra("image_uri", data?.data.toString())
            startActivity(intent)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(previewView)  // Use previewView.surfaceProvider instead of surfaceView.holder
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }else if(requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM){
            selectImageInAlbum()

        }
//        else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "MainActivity2"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val GALLERY_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //
    }
}
