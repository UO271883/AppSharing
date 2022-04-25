package com.example.appsharing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.appsharing.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.annotations.Contract

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.shareMessage.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, binding.message.text.toString())
                type = "text/plain"
            }
            val activities= packageManager.queryIntentActivities(sendIntent, 0)
            val isIntentSafe = activities.size > 0
            if(isIntentSafe) {
                startActivity(sendIntent)
            }
        }
        binding.sendMail.setOnClickListener {
            val correo = arrayOf("UO271883@uniovi.es")
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, correo)
                putExtra(Intent.EXTRA_SUBJECT, "Prueba desde AppSharing")
                putExtra(Intent.EXTRA_TEXT, binding.message.text.toString())
            }
            val activities = packageManager.queryIntentActivities(emailIntent, 0)
            val isIntentSafe = activities.size > 0
            startActivity(emailIntent)
        }
        binding.getImage.setOnClickListener {
            requestCameraPermission()
        }
    }

    private val reqCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        cameraDoWork()
    }
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            image ->
        binding.imageView.setImageBitmap(image)
    }

    fun requestCameraPermission() {
        // Comprueba si el permiso está concedido
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permiso no concedido
            // ¿Dar una explicación?
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Explicación de la necesidad del permiso en un SnackBar
                // ¡No es la mejor opción!
                Snackbar.make(binding.root, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {
                    }.show()
            }
            else {
                // No se mecesita explicación: solicitamos el permiso
                reqCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
        else {
            //Permiso concedido. Realizamos el trabajo
            cameraDoWork()
        }
    }
    private fun cameraDoWork(){
        takePictureLauncher.launch(null)
    }
}