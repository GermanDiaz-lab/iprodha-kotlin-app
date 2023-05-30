
package com.example.test2

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.test2.databinding.ActivityImagenConvenioBinding
import com.example.test2.util.LoadingDialog
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.plcoding.androidstorage.SharedStoragePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class ImagenConvenio : AppCompatActivity() {


    private  lateinit var binding: ActivityImagenConvenioBinding

    private lateinit var imageView2: ImageView
    private lateinit var buttonDelete: Button
    private lateinit var buttonSave: Button
    private lateinit var textObs: EditText


    private var isFotoDeleted: Boolean = true

    private lateinit var contentObserver: ContentObserver
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>




    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var readPermissionGranted = false
    private var writePermissionGranted = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>



    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = ActivityImagenConvenioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sqliteHelper = SQLiteHelper(this)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)




        initContentObserver()


        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_home -> startActivity(Intent(this, DashboardActivity::class.java))
                R.id.borrar_obras -> Toast.makeText(applicationContext, "Click", Toast.LENGTH_SHORT).show()
                R.id.borrar_convenios -> startActivity(Intent(this, BorrarConveniosActivity::class.java))

            }

            true

        }

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if(readPermissionGranted) {
                loadPhotosFromExternalStorageIntoRecyclerView()
            } else {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("La aplicacion necesita que de todos los permisos para funcionar,\n" +
                        "hacer click en ok para ir a la pantalla de configuracion")
                alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    val intent = Intent(this@ImagenConvenio, Settings.ACTION_APPLICATION_SETTINGS::class.java)
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                alertDialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }

            }
        }
        initView()
        updateOrRequestPermissions()


        buttonDelete.setOnClickListener { lifecycleScope.launch {
            deletePhotoFromExternalStorage(Uri.parse(intent.getStringExtra("Uri")))
        } }

        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                intent.getStringExtra("Id")?.let { id ->
                    intent.getStringExtra("Uri")?.let { uri ->
                        startUpload(Uri.parse(uri), id)
                    }
                }
            }
        }





        loadPhotosFromExternalStorageIntoRecyclerView()
        setUserNameAndEmail()
    }


    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if(readPermissionGranted) {
                    loadPhotosFromExternalStorageIntoRecyclerView()
                }
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }









    private suspend fun loadPhotosFromExternalStorage(uri: Uri): List<SharedStoragePhoto> {
        return withContext(Dispatchers.IO) {

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )




            val photos = mutableListOf<SharedStoragePhoto>()
            contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while(cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedStoragePhoto(id = id, fotoid = 0, name = displayName, width = width, height = height, contentUri = contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
    }




    private fun loadPhotosFromExternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photo = loadPhotosFromExternalStorage(Uri.parse(intent.getStringExtra("Uri")))
            if (photo.isEmpty()) {
                imageView2.setImageResource(R.drawable.ic_baseline_done_24)
            } else {
                imageView2.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        imageView2.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val bitmap = decodeSampledBitmapFromUri(photo[0].contentUri, imageView2.width, imageView2.height)
                        imageView2.setImageBitmap(bitmap)
                    }
                })
            }
        }
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            val fileDescriptor = parcelFileDescriptor.fileDescriptor

            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, this)

                val height = outHeight
                val width = outWidth
                var inSampleSize = 1

                if (height > reqHeight || width > reqWidth) {
                    val halfHeight: Int = height / 2
                    val halfWidth: Int = width / 2

                    while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                        inSampleSize *= 2
                    }
                }

                inJustDecodeBounds = false
                this.inSampleSize = inSampleSize
            }

            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        }
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
                val intent = Intent(this@ImagenConvenio, Settings.ACTION_APPLICATION_SETTINGS::class.java)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
        }
    }

    private suspend fun deletePhotoFromExternalStorage(photoUri: Uri) {
        if (isFotoDeleted) {
            withContext(Dispatchers.IO) {
                try {
                    contentResolver.delete(photoUri, null, null)
                } catch (e: SecurityException) {
                    val intentSender = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                            MediaStore.createDeleteRequest(contentResolver, listOf(photoUri)).intentSender
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                            val recoverableSecurityException = e as? RecoverableSecurityException
                            recoverableSecurityException?.userAction?.actionIntent?.intentSender
                        }
                        else -> null
                    }
                    intentSender?.let { sender ->
                        intentSenderLauncher.launch(
                            IntentSenderRequest.Builder(sender).build()
                        )
                    }
                }
                isFotoDeleted = false
            }
        } else {
            Toast.makeText(applicationContext, "La foto ya se borro", Toast.LENGTH_SHORT).show()
        }


    }




    private fun setUserNameAndEmail() {
        val usrList =  sqliteHelper.getUsuario()

        val navView : NavigationView = findViewById(R.id.nav_view)
        val header : View = navView.getHeaderView(0)
        val navUsername : TextView = header.findViewById(R.id.user_name)
        val navUserEmail : TextView = header.findViewById(R.id.user_email)

        navUsername.text = usrList[0].usuarionombre
        navUserEmail.text = usrList[0].usuarioemail
    }



    private val CLIENT_ID = "***************"

    private suspend fun startUpload(uri: Uri, id: String) {
        val fotoList = sqliteHelper.getFoto(id)
        if (fotoList[0].syncfoto == 0) {
            val image = loadPhoto(uri, 1024, 1024)
            image?.let { uploadImageToImgur(it) } ?: showToast("Image loading failed")
        }
    }

    private suspend fun loadPhoto(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return withContext(Dispatchers.IO) {
            val options = getBitmapOptions(uri)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            decodeBitmap(uri, options)
        }
    }

    private fun getBitmapOptions(uri: Uri): BitmapFactory.Options {
        return contentResolver.openInputStream(uri).use { inputStream ->
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, this)
            }
        }
    }

    private fun decodeBitmap(uri: Uri, options: BitmapFactory.Options): Bitmap? {
        options.inJustDecodeBounds = false
        return contentResolver.openInputStream(uri).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private suspend fun uploadImageToImgur(image: Bitmap) {
        val loading = LoadingDialog(this)
        loading.startLoading()

        try {
            if (isFotoDeleted) {
                getBase64Image(image)?.let { base64Image ->
                    val response = uploadToImgur(base64Image)
                    val data = parseResponse(response)
                    showToast(data.getString("link"))
                    uploadToFirebase(data.getString("link"))
                } ?: showToast("Failed to convert image to Base64")
            } else {
                showToast("La foto fue borrada")
            }
        } catch (e: Exception) {
            showToast("An error occurred: ${e.localizedMessage}")
        } finally {
            loading.isDismiss()
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun getBase64Image(bitmap: Bitmap): String? {
        return withContext(Dispatchers.Default) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val b = outputStream.toByteArray()
            Base64.encodeToString(b, Base64.DEFAULT)
        }
    }

    private suspend fun uploadToImgur(base64Image: String): String {
        val url = URL("https://api.imgur.com/3/image")
        val boundary = "Boundary-${System.currentTimeMillis()}"

        val response = withContext(Dispatchers.IO) {
            createHttpUrlConnection(url, boundary).let { conn ->
                writeDataToOutputStream(conn, base64Image, boundary)
                readResponse(conn)
            }
        }
        return response
    }

    private fun createHttpUrlConnection(url: URL, boundary: String): HttpsURLConnection {
        return (url.openConnection() as HttpsURLConnection).apply {
            setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            requestMethod = "POST"
            doInput = true
            doOutput = true
        }
    }

    private fun writeDataToOutputStream(conn: HttpsURLConnection, base64Image: String, boundary: String) {
        conn.outputStream.use { outputStream ->
            val body = buildRequestBody(base64Image, boundary)
            OutputStreamWriter(outputStream).use { outputStreamWriter ->
                outputStreamWriter.write(body)
                outputStreamWriter.flush()
            }
        }
    }

    private fun buildRequestBody(base64Image: String, boundary: String): String {
        return StringBuilder().apply {
            append("--$boundary\r\n")
            append("Content-Disposition:form-data; name=\"image\"\r\n\r\n")
            append("$base64Image\r\n")
            append("--$boundary--\r\n")
        }.toString()
    }

    private fun readResponse(conn: HttpsURLConnection): String {
        return conn.inputStream.bufferedReader().use { reader -> reader.readText() }.also {
            conn.disconnect()
        }
    }

    private fun parseResponse(response: String): JSONObject {
        val jsonObject = JSONTokener(response).nextValue() as JSONObject
        return jsonObject.getJSONObject("data")
    }

    private suspend fun uploadToFirebase(link: String) {
        val fotoList = intent.getStringExtra("Id")?.let { sqliteHelper.getFoto(it) }
        val obrtosyncmap = buildDataMap(fotoList, link)
        addDataToFirestore(obrtosyncmap)
    }

    private fun buildDataMap(fotoList: List<FotoModel>?, link: String): HashMap<String, Any> {
        val obrtosyncmap = hashMapOf<String, Any>()
        val usr = sqliteHelper.getUsuario()
        val linkm = searchString(link)

        fotoList?.filter { it.syncfoto == 0 }?.forEach { foto ->
            obrtosyncmap.apply {
                put("fotoid", foto.fotoid)
                put("nombrefoto", foto.nombrefoto)
                put("fotolatitud", foto.fotolatitud)
                put("fotolongitud", foto.fotolongitud)
                put("linkm", linkm)
                put("link", link)
                put("obra", intent.getStringExtra("Obra").toString())
                put("usuarionombre", usr[0].usuarionombre)
                put("observacion", textObs.text.toString())
                put("fechafoto", foto.fechafoto)
                put("serverdate", FieldValue.serverTimestamp())
            }
            sqliteHelper.updateSyncFoto(foto.fotoid.toString())
        }
        return obrtosyncmap
    }

    private suspend fun addDataToFirestore(obrtosyncmap: HashMap<String, Any>): DocumentReference =
        suspendCoroutine { continuation ->
            val db = Firebase.firestore
            db.collection("obras")
                .add(obrtosyncmap)
                .addOnSuccessListener { documentReference ->
                    continuation.resume(documentReference)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    private fun searchString(link: String): String {
        val formats = listOf(".jpg", ".png", ".jpeg")
        var result = ""
        for (format in formats) {
            val index = link.indexOf(format, 0)
            if (index != -1) {
                result = link.addCharAtIndex('m', index)
                break
            }
        }
        return result
    }

    private fun String.addCharAtIndex(char: Char, index: Int) =
        StringBuilder(this).apply { insert(index, char) }.toString()




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initView(){

        imageView2 = findViewById(R.id.imageView2)
        buttonDelete = findViewById(R.id.buttonDelete)
        buttonSave = findViewById(R.id.buttonSave)
        textObs = findViewById(R.id.obs)



    }

}
