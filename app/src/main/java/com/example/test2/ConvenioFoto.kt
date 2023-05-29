
package com.example.test2

import addWatermark
import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.test2.databinding.ActivityConvenioFotoBinding
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.plcoding.androidstorage.SharedStoragePhoto
import com.plcoding.androidstorage.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ConvenioFoto : AppCompatActivity() {


    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        lifecycleScope.launch {
            whenStarted {
                if (writePermissionGranted) {

                    if ((latestTmpUri != null) && (latestTmpUri!! != Uri.EMPTY)) {
                        latestTmpUri?.let { uri ->
                            Log.d("TAG", uri.toString())
                            checkFile(uri)
                        }
                    } else {

                        val uriparced = Uri.parse(sqliteHelper.getTemp()[0].tempuri)
                        uriparced?.let { uri ->
                            Log.d("TAG", uri.toString())
                            checkFile(uri)
                        }
                    }
                } else {
                    Toast.makeText(this@ConvenioFoto, "ERROR NO PERMISO", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("TAG", "ERROR NO PERMISO")
                }
            }
        }

    }

    private var latestTmpUri: Uri? = null
    private var latestFotoGeo: String = ""
    private var latestLatitud: String = ""
    private var latestLongitud: String = ""

    private lateinit var contentObserver: ContentObserver
    private  lateinit var binding: ActivityConvenioFotoBinding
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var deletedImageUri: Uri? = null



    private lateinit var fotoAdapter: FotoAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var readPermissionGranted = false
    private var writePermissionGranted = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>



    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvenioFotoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sqliteHelper = SQLiteHelper(this)

        fotoAdapter = FotoAdapter {
            lifecycleScope.launch {
                changeActivity(it.contentUri, it.fotoid)
            }
        }

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
                R.id.borrar_obras -> startActivity(Intent(this, BorrarObrasActivity::class.java))
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
                    val intent = Intent(this@ConvenioFoto, Settings.ACTION_APPLICATION_SETTINGS::class.java)
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



        binding.btnTakePhoto.setOnClickListener {
            getLocation()
            takeImage()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ConvenioActivity::class.java))
        }

        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)

        setupInternalStorageRecyclerView()
        loadPhotosFromExternalStorageIntoRecyclerView()
        setUserNameAndEmail()
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


    private fun takeImage() {
        Log.d("TAG", "takeImage")
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                Log.d("TAG", uri.toString())
                latestTmpUri = uri
                takePhoto.launch(uri)
            }
        }
    }


    private fun getTmpFileUri(): Uri? {
        Log.d("TAG", "getTmpFileUri")
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "temp_file" + UUID.randomUUID().toString() + ".jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        }
        return contentResolver.insert(imageCollection, contentValues)
    }



    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it == null) {
                Toast.makeText(applicationContext, "NO GPS", Toast.LENGTH_SHORT).show()
            } else it.apply {
                latestFotoGeo = "Latitud: " + it.latitude.toString() + " Longitud: " + it.longitude.toString()
                latestLatitud = it.latitude.toString()
                latestLongitud = it.longitude.toString()

                sqliteHelper.insertTemp(latestTmpUri.toString(), latestFotoGeo, latestLatitud, latestLongitud)

                Toast.makeText(applicationContext, "GEO SET", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun savePhotoToExternalStorage(displayName: String, numeroObra: String, bmp: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            val bmpW = addWatermark(bmp, sqliteHelper.getTemp()[0].tempgeo)

            val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "convenio$numeroObra"
            bmp.recycle()

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Iprodha_app_$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmpW.width)
                put(MediaStore.Images.Media.HEIGHT, bmpW.height)
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
                        if (!bmpW.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        } else {
                            addFoto(uri)
                            loadPhotosFromExternalStorageIntoRecyclerView()
                            latestTmpUri?.let { contentResolver.delete(it, null, null) }
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




    private suspend fun loadPhotosFromExternalStorage(obraid: String): List<SharedStoragePhoto> {

        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI


            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )

            val photosList = sqliteHelper.getAllFotosConv(obraid)
            val photosUri = arrayOfNulls<String>(photosList.size)
            val idMap = mutableMapOf<Long, Int>()
            var selection = MediaStore.Video.Media._ID + " IN (?"
            for (i in photosList.indices) {
                val photoUriParseId = ContentUris.parseId(photosList[i].fotouri)
                photosUri[i] = photoUriParseId.toString()
                idMap[photoUriParseId] = photosList[i].fotoid // Associate the parsed ID with the fotoid

                if (i > 0) {
                    selection += ", ?"
                }
            }
            selection += ")"




            val photos = mutableListOf<SharedStoragePhoto>()
            contentResolver.query(
                collection,
                projection,
                selection,
                photosUri,
                "${MediaStore.Images.Media.DATE_ADDED} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while(cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn) // Get the parsed ID
                    val fotoid = idMap[id] ?: error("No fotoid found for parsed ID $id") // Look up the corresponding fotoid
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val btm = BitmapFactory.Options().run{
                        inJustDecodeBounds = true
                        BitmapFactory.decodeStream(contentResolver.openInputStream(contentUri), null, this)
                        // Calculate inSampleSize
                        inSampleSize = calculateInSampleSize(this, 100, 100)

                        // Decode bitmap with inSampleSize set
                        inJustDecodeBounds = false

                        BitmapFactory.decodeStream(contentResolver.openInputStream(contentUri), null, this)
                    }


                    photos.add(SharedStoragePhoto(id, fotoid, displayName, width, height, contentUri, compressBitmap = btm ))
                }
                photos.toList()
            } ?: listOf()
        }
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    private suspend fun loadPhoto(uri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri).use { inputStream ->
                val btm = BitmapFactory.decodeStream(inputStream)
                return@use btm
            }
        }
    }


    //private suspend fun watermarkFotoBitmap(uri: Uri): Bitmap{
    //val bmp = loadPhoto(uri)
    //  Toast.makeText(applicationContext, bmp.byteCount.toString(), Toast.LENGTH_SHORT).show()
    //    return addWatermark(bmp, "Latitud:" + fusedLocationClient.lastLocation.result.latitude.toString() + " " + "Longitud:" + fusedLocationClient.lastLocation.result.latitude.toString())
    // }

    private suspend fun checkFile(uri: Uri) {
        Log.d("TAG", "checkFile")
        Log.d("TAG", uri.toString())
        return withContext(Dispatchers.IO) {

            val projection = arrayOf(
                MediaStore.Images.Media.SIZE,
            )

            val photos = mutableListOf<Int>()
            contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (cursor.moveToNext()) {
                    val size = cursor.getInt(sizeColumn)
                    photos.add(size)
                }
                photos.toList()
            }
            if (photos[0] == 0){
                latestTmpUri?.let { contentResolver.delete(it, null, null) }
            }else{
                saveWatermarkFoto(uri)
            }
        }
    }

    private suspend fun saveWatermarkFoto(uri: Uri){
        Log.d("TAG", "saveWatermarkFoto")
        val bmp = loadPhoto(uri)
        val items = sqliteHelper.getConv(intent.getIntExtra("Id", 0).toString())
        val name = intent.getStringExtra("Obra") + items[0].items.toString()
        val isSavedSuccessfully =
            savePhotoToExternalStorage(name, intent.getStringExtra("Obra").toString(), bmp)
        if (!isSavedSuccessfully) {
            Toast.makeText(
                applicationContext,
                "ERROR watermark",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }



    private fun loadPhotosFromExternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = intent.getIntExtra("Id", 0)?.let { loadPhotosFromExternalStorage(it.toString()) }
            fotoAdapter.submitList(photos)
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
                val intent = Intent(this@ConvenioFoto, Settings.ACTION_APPLICATION_SETTINGS::class.java)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
        }
    }

    private  fun changeActivity(photoUri: Uri, id: Int) {
        val obraintent = intent.getStringExtra("Obra")
        val intent = Intent(this@ConvenioFoto, ImagenConvenio::class.java)
        intent.putExtra("Uri", photoUri.toString())
        intent.putExtra("Id", id.toString())
        intent.putExtra("Obra", obraintent)
        startActivity(intent)

        //  val oList = ObraModel(id = obraid, obra = obranumero!!, items = obraitems, usuario = obrausuario!!)
        //  val stat = sqliteHelper.updateObraItems(oList)
        //  if (stat > -1){
        //      Toast.makeText(applicationContext, "Item actualizado", Toast.LENGTH_SHORT).show()
        //  } else {
        //      Toast.makeText(applicationContext, "ERROR: Item no actualizado", Toast.LENGTH_SHORT).show()
        //  }

    }


    private fun addFoto(uri: Uri){
        Log.d("TAG", "addFoto")

        val geo = sqliteHelper.getTemp()
        val id = sqliteHelper.getConv(intent.getIntExtra("Id", 0).toString())
        val fotouri = uri
        val obraid = intent.getIntExtra("Id", 0)
        val obranumero = intent.getStringExtra("Obra")
        val obraitems = id[0].items + 1
        val obrausuario = intent.getStringExtra("Usuario")
        val fotonombre = obranumero + obraitems
        val syncfoto = 0
        val latitud = geo[0].templat
        val longitud = geo[0].templong
        Log.d("TAG", geo[0].templong)


        val oList = ObraModel(id = obraid, obra = obranumero!!, items = obraitems, usuario = obrausuario!!)
        val stat = sqliteHelper.updateConvItems(oList)
        val fechafoto = getCurrentDate()



        val fml = FotoModel(nombrefoto = fotonombre, fotouri = fotouri, obraid = 0, convid = obraid, syncfoto = syncfoto, fotolatitud = latitud, fotolongitud = longitud, fechafoto = fechafoto)
        val status = sqliteHelper.insertFoto(fml)
        if (status > -1){
            loadPhotosFromExternalStorageIntoRecyclerView()
        }

        sqliteHelper.borrarTemp()



    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun setUserNameAndEmail() {
        val usrList =  sqliteHelper.getUsuario()

        val navView : NavigationView = findViewById(R.id.nav_view)
        val header : View = navView.getHeaderView(0)
        val navUsername : TextView = header.findViewById(R.id.user_name)
        val navUserEmail : TextView = header.findViewById(R.id.user_email)
        val fotoObraNumero: TextView = findViewById(R.id.fotoObraNumero)

        fotoObraNumero.text = intent.getStringExtra("Obra")

        navUsername.text = usrList[0].usuarionombre
        navUserEmail.text = usrList[0].usuarioemail
    }

    private fun setupInternalStorageRecyclerView() = binding.rvPrivatePhotos.apply {
        adapter = fotoAdapter
        layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}