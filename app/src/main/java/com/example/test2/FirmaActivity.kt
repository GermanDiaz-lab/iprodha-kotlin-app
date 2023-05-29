package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.test2.util.DrawingView
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.graphics.Color
import android.os.Environment
import android.view.Surface
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.plcoding.androidstorage.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

import java.io.IOException

class FirmaActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var saveButton: Button

    private lateinit var contentObserver: ContentObserver


    private var readPermissionGranted = false
    private var writePermissionGranted = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var sqliteHelper: SQLiteHelper




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firma)

        setRequestedOrientationBasedOnDevice()

        drawingView = findViewById(R.id.drawingView)
        drawingView.keepScreenOn = true


        drawingView.setBackgroundColor(Color.WHITE)

        saveButton = findViewById(R.id.guardarFirma)

        saveButton.setOnClickListener {
            saveDrawing()
        }

        sqliteHelper = SQLiteHelper(this)


        initContentObserver()

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if(readPermissionGranted) {

            } else {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("La aplicacion necesita que de todos los permisos para funcionar,\n" +
                        "hacer click en ok para ir a la pantalla de configuracion")
                alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    val intent = Intent(this@FirmaActivity, Settings.ACTION_APPLICATION_SETTINGS::class.java)
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                alertDialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }

            }
        }
        updateOrRequestPermissions()


    }

    private fun setRequestedOrientationBasedOnDevice() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val rotation = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        }

        val orientation: Int = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT ->
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                }
            Configuration.ORIENTATION_LANDSCAPE ->
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        requestedOrientation = orientation
    }


    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
        if (!(readPermissionGranted || readPermissionGranted || permissionsToRequest.isNotEmpty())){
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("La aplicacion necesita que de todos los permisos para funcionar,\n" +
                    "hacer click en ok para ir a la pantalla de configuracion")
            alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
                val intent = Intent(this@FirmaActivity, Settings.ACTION_APPLICATION_SETTINGS::class.java)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
        }
    }

    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if(readPermissionGranted) {

                }
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    private fun saveDrawing() {
        Log.d("TAG", "takeImage")
        lifecycleScope.launchWhenStarted {
            drawingView.saveDrawing().let { bmp ->
                if (bmp != null) {
                    savePhotoToExternalStorage(bmp)
                }
            }
        }
    }

    private suspend fun savePhotoToExternalStorage(bmp: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            val informedni = intent.getStringExtra("Dni")

            val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "firmas"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Iprodha_app_$informedni.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            try {
                contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        } else {
                            if (informedni != null) {
                                addFirmatoDB(uri, informedni)
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, contentValues, null, null)
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }


    private fun addFirmatoDB(uri: Uri, dni:String){
        val firma = FirmaModel(nombrefirma = "firma_${dni}.jpg", uri, informedni = dni)
        val stat = sqliteHelper.inserFirma(firma)
        if (stat > -1) {
            finish()
            startActivity(Intent(this, SocialesMainActivity::class.java))
        } else {
            //error
        }
    }


}