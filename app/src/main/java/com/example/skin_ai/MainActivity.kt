package com.example.skin_ai

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNextScreen: Button = findViewById(R.id.myButton)

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.CAMERA) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
//        // Check if the permission is already granted
//        if (ContextCompat.checkSelfPermission(
//                this@MainActivity,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is already granted.
//            Toast.makeText(this, "already ok", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "prompting", Toast.LENGTH_SHORT).show()
//            // Permission is not granted.
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA),
//                REQUEST_CODE_PERMISSIONS
//            )
//        }


        btnNextScreen.setOnClickListener {
            if (allPermissionsGranted()) {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                this@MainActivity, it
            ) == PackageManager.PERMISSION_GRANTED
        }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted.
//                val intent = Intent(this, MainActivity2::class.java)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "not ok", Toast.LENGTH_SHORT).show()
//                // Permission denied by the user.
//            }
//
//        }
//
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity2"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val GALLERY_PERMISSION = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //
    }
}
