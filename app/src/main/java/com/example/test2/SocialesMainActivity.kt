package com.example.test2

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.databinding.ActivitySocialesMainBinding
import com.example.test2.util.LoadingDialog
import com.google.android.gms.location.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class SocialesMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySocialesMainBinding

    private lateinit var informeAdapter: InformeAdapter
    private lateinit var sqliteHelper: SQLiteHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialesMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sqliteHelper = SQLiteHelper(this)
        informeAdapter = InformeAdapter()

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
        LocationServices.getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        checkPermission()
        loadInformeFromDbIntoRecyclerView()
        setupObraRecyclerView()

        binding.fab.setOnClickListener {
            startEncuesta()
        }

    }

    private fun loadInformeFromDbIntoRecyclerView() {
        lifecycleScope.launch{
            val informe = loadInformeFromDb()
            informeAdapter.submitList(informe)
        }
    }

    private fun loadInformeFromDb(): List<InformeModel> {
        return sqliteHelper.getAllInformesForRV()
    }

    private fun setupObraRecyclerView() = binding.rvInformes.apply {
        adapter = informeAdapter
        layoutManager = LinearLayoutManager(this@SocialesMainActivity, LinearLayoutManager.VERTICAL, false)


        informeAdapter.setOnClickFotoItem { informe ->
            lifecycleScope.launch {
                uploadSignatureToImgur(informe)
            }
        }

    }

    private suspend fun loadPhoto(uri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri).use { inputStream ->
                val btm = BitmapFactory.decodeStream(inputStream)
                return@use btm
            }
        }
    }

    private val CLIENT_ID = "a640e02866c8b62"

    private suspend fun uploadSignatureToImgur(informe: InformeModel) {
        val loading = LoadingDialog(this).apply { startLoading() }

        val firmaList = sqliteHelper.getFirma(informe.titulardni)
        val image = loadPhoto(firmaList[0].firmauri)

        val base64Image = getBase64Image(image)
        val response = sendImageToImgur(base64Image)

        val data = processResponse(response)

        loading.isDismiss()
        uploadInformeToFirebase(data.getString("link"), informe)

        Log.d("TAG", "Link is : ${data.getString("link")}")
    }

    private fun getBase64Image(image: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private suspend fun sendImageToImgur(base64Image: String): String {
        val url = URL("https://api.imgur.com/3/image")
        val boundary = "Boundary-${System.currentTimeMillis()}"

        return withContext(Dispatchers.IO) {
            (url.openConnection() as HttpsURLConnection).apply {
                setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                requestMethod = "POST"
                doInput = true
                doOutput = true
            }.let { conn ->
                conn.outputStream.use { outputStream ->
                    val body = buildRequestBody(boundary, base64Image)
                    OutputStreamWriter(outputStream).use { it.write(body) }
                }

                conn.inputStream.bufferedReader().use { it.readText() }.also { conn.disconnect() }
            }
        }
    }

    private fun buildRequestBody(boundary: String, base64Image: String): String {
        return StringBuilder().apply {
            append("--$boundary\r\n")
            append("Content-Disposition:form-data; name=\"image\"\r\n\r\n")
            append("$base64Image\r\n")
            append("--$boundary--\r\n")
        }.toString()
    }

    private fun processResponse(response: String): JSONObject {
        val jsonObject = JSONTokener(response).nextValue() as JSONObject
        return jsonObject.getJSONObject("data")
    }

    private fun searchString(link: String): String {
        val fileTypes = listOf(".jpg", ".png", ".jpeg")
        for (type in fileTypes) {
            val index = link.indexOf(type)
            if (index != -1) return link.addCharAtIndex('m', index)
        }
        return ""
    }

    private fun String.addCharAtIndex(char: Char, index: Int) =
        StringBuilder(this).apply { insert(index, char) }.toString()

    private fun uploadInformeToFirebase(link: String, informe: InformeModel){
        val loading = LoadingDialog(this)
        loading.startLoading()
        val informeModel = sqliteHelper.getInforme(informe.titulardni)
        val infToSync = hashMapOf<String, Any>()
        val linkm = searchString(link)

        val db = Firebase.firestore

        infToSync["usuario"] = informeModel[0].usuario
        infToSync["sitjur"] = informeModel[0].sitjur
        infToSync["exp"] = informeModel[0].exp
        infToSync["estadodeud"] = informeModel[0].estadodeud
        infToSync["notdeud"] = informeModel[0].notdeud
        infToSync["titular"] = informeModel[0].titular
        infToSync["nota"] = informeModel[0].nota
        infToSync["fecha"] = informeModel[0].fecha
        infToSync["localidad"] = informeModel[0].localidad
        infToSync["legajo"] = informeModel[0].legajo
        infToSync["ocupante"] = informeModel[0].ocupante
        infToSync["ocupantedni"] = informeModel[0].ocupantedni
        infToSync["residedesde"] = informeModel[0].residedesde
        infToSync["tipologia"] = informeModel[0].tipologia
        infToSync["estado"] = informeModel[0].estado
        infToSync["titulo"] = informeModel[0].titulo
        infToSync["sithab"] = informeModel[0].sithab
        infToSync["identviv"] = informeModel[0].identviv
        infToSync["ampliacion"] = informeModel[0].ampliacion
        infToSync["observacion"] = informeModel[0].observacion
        infToSync["antecedenteiprodha"] = informeModel[0].antecedenteiprodha
        infToSync["antecedenteviv"] = informeModel[0].antecedenteviv
        infToSync["antecedentearreglo"] = informeModel[0].antecedentearreglo
        infToSync["antecedentelote"] = informeModel[0].antecedentelote
        infToSync["antecedentemv"] = informeModel[0].antecedentemv
        infToSync["antecedentevr"] = informeModel[0].antecedentevr
        infToSync["comoaccede"] = informeModel[0].comoaccede
        infToSync["otrosinicio"] = informeModel[0].otrosinicio
        infToSync["validogrupo"] = informeModel[0].validogrupo
        infToSync["titulardni"] = informeModel[0].titulardni
        infToSync["estadociviltitular"] = informeModel[0].estadociviltitular
        infToSync["estadocivilconyuge"] = informeModel[0].estadocivilconyuge
        infToSync["dniconyuge"] = informeModel[0].dniconyuge
        infToSync["dnihijos"] = informeModel[0].dnihijos
        infToSync["actadematrimonio"] = informeModel[0].actadematrimonio
        infToSync["unionconvivencial"] = informeModel[0].unionconvivencial
        infToSync["partidasnac"] = informeModel[0].partidasnac
        infToSync["contactotelefonico"] = informeModel[0].contactotelefonico
        infToSync["correoelectronico"] = informeModel[0].correoelectronico
        infToSync["observaciongrupo"] = informeModel[0].observaciongrupo
        infToSync["escolaridad"] = informeModel[0].escolaridad
        infToSync["obrasocial"] = informeModel[0].obrasocial
        infToSync["tienecud"] = informeModel[0].tienecud
        infToSync["vigenciacud"] = informeModel[0].vigenciacud
        infToSync["discapacidad"] = informeModel[0].discapacidad
        infToSync["diagnostico"] = informeModel[0].diagnostico
        infToSync["silladeruedas"] = informeModel[0].silladeruedas
        infToSync["muletas"] = informeModel[0].muletas
        infToSync["andador"] = informeModel[0].andador
        infToSync["otrosmovilidad"] = informeModel[0].otrosmovilidad
        infToSync["observacionsalud"] = informeModel[0].observacionsalud
        infToSync["trasnsplantado"] = informeModel[0].trasnsplantado
        infToSync["carnet"] = informeModel[0].carnet
        infToSync["listadeespera"] = informeModel[0].listadeespera
        infToSync["observaciontrasplante"] = informeModel[0].observaciontrasplante
        infToSync["ingreso1"] = informeModel[0].ingreso1
        infToSync["ingreso2"] = informeModel[0].ingreso2
        infToSync["ingreso3"] = informeModel[0].ingreso3
        infToSync["ingreso4"] = informeModel[0].ingreso4
        infToSync["nombreingreso1"] = informeModel[0].nombreingreso1
        infToSync["nombreingreso2"] = informeModel[0].nombreingreso2
        infToSync["nombreingreso3"] = informeModel[0].nombreingreso3
        infToSync["nombreingreso4"] = informeModel[0].nombreingreso4
        infToSync["categorialaboral1"] = informeModel[0].categorialaboral1
        infToSync["categorialaboral2"] = informeModel[0].categorialaboral2
        infToSync["categorialaboral3"] = informeModel[0].categorialaboral3
        infToSync["categorialaboral4"] = informeModel[0].categorialaboral4
        infToSync["ocupacion1"] = informeModel[0].ocupacion1
        infToSync["ocupacion2"] = informeModel[0].ocupacion2
        infToSync["ocupacion3"] = informeModel[0].ocupacion3
        infToSync["ocupacion4"] = informeModel[0].ocupacion4
        infToSync["telefono1"] = informeModel[0].telefono1
        infToSync["telefono2"] = informeModel[0].telefono2
        infToSync["telefono3"] = informeModel[0].telefono3
        infToSync["telefono4"] = informeModel[0].telefono4
        infToSync["domicilio1"] = informeModel[0].domicilio1
        infToSync["domicilio2"] = informeModel[0].domicilio2
        infToSync["domicilio3"] = informeModel[0].domicilio3
        infToSync["domicilio4"] = informeModel[0].domicilio4
        infToSync["lugar1"] = informeModel[0].lugar1
        infToSync["lugar2"] = informeModel[0].lugar2
        infToSync["lugar3"] = informeModel[0].lugar3
        infToSync["lugar4"] = informeModel[0].lugar4
        infToSync["fechadelrecibo1"] = informeModel[0].fechadelrecibo1
        infToSync["fechadelrecibo2"] = informeModel[0].fechadelrecibo2
        infToSync["fechadelrecibo3"] = informeModel[0].fechadelrecibo3
        infToSync["fechadelrecibo4"] = informeModel[0].fechadelrecibo4
        infToSync["principalpagador"] = informeModel[0].principalpagador
        infToSync["dnipagador"] = informeModel[0].dnipagador
        infToSync["ingresospagador"] = informeModel[0].ingresospagador
        infToSync["catlaboralpagador"] = informeModel[0].catlaboralpagador
        infToSync["garante"] = informeModel[0].garante
        infToSync["dnigarante"] = informeModel[0].dnigarante
        infToSync["ingresosgarante"] = informeModel[0].ingresosgarante
        infToSync["catlaboralgarante"] = informeModel[0].catlaboralgarante
        infToSync["declaracionjurada"] = informeModel[0].declaracionjurada
        infToSync["titularprestamos"] = informeModel[0].titularprestamos
        infToSync["prestamomonto"] = informeModel[0].prestamomonto
        infToSync["plazo"] = informeModel[0].plazo
        infToSync["consumomensual"] = informeModel[0].consumomensual
        infToSync["tarjetas"] = informeModel[0].tarjetas
        infToSync["tipodetarjeta"] = informeModel[0].tipodetarjeta
        infToSync["observacionessiteco"] = informeModel[0].observacionessiteco
        infToSync["luz"] = informeModel[0].luz
        infToSync["montoluz"] = informeModel[0].montoluz
        infToSync["luzformal"] = informeModel[0].luzformal
        infToSync["agua"] = informeModel[0].agua
        infToSync["aguamonto"] = informeModel[0].aguamonto
        infToSync["aguaformal"] = informeModel[0].aguaformal
        infToSync["cable"] = informeModel[0].cable
        infToSync["cablemonto"] = informeModel[0].cablemonto
        infToSync["cableformal"] = informeModel[0].cableformal
        infToSync["internet"] = informeModel[0].internet
        infToSync["internetmonto"] = informeModel[0].internetmonto
        infToSync["internetformal"] = informeModel[0].internetformal
        infToSync["observacionesservicios"] = informeModel[0].observacionesservicios
        infToSync["cuotasocial"] = informeModel[0].cuotasocial
        infToSync["cuotaprovisoria"] = informeModel[0].cuotaprovisoria
        infToSync["cuotaestandar"] = informeModel[0].cuotaestandar
        infToSync["planventa"] = informeModel[0].planventa
        infToSync["planalquiler"] = informeModel[0].planalquiler
        infToSync["otrosmodalidad"] = informeModel[0].otrosmodalidad
        infToSync["plazopresentacion"] = informeModel[0].plazopresentacion
        infToSync["observacionesplazo"] = informeModel[0].observacionesplazo
        infToSync["informelatitud"] = informeModel[0].informelatitud
        infToSync["informelongitud"] = informeModel[0].informelongitud
        infToSync["link"] = link
        infToSync["linkm"] = linkm
        infToSync["conclusionfin"] = ""
        infToSync["serverdate"] = FieldValue.serverTimestamp()

        db.collection("informes")
            .add(infToSync)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                sqliteHelper.borrarInforme(informe.titulardni)
                Toast.makeText(this, "INFORME GUARDADO", Toast.LENGTH_SHORT).show()
                loadInformeFromDbIntoRecyclerView()
                loading.isDismiss()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }


    }

    private fun startEncuesta(){
        startActivity(Intent(this, EncuestaActivity::class.java))
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

}