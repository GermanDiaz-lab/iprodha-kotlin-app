package com.example.test2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.databinding.ActivityConvenioBinding
import com.example.test2.util.LoadingDialog
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.random.Random

class ConvenioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConvenioBinding

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var obraAdapter: ObraAdapter

    private lateinit var edObra: EditText
    private lateinit var edDescripcion: EditText
    private lateinit var edUpdateObra: EditText
    private lateinit var btnCrearObra: Button
    private lateinit var btnUpdateObra: Button






    private var oList: ObraModel? = null



    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvenioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        sqliteHelper = SQLiteHelper(this)
        btnCrearObra.setOnClickListener { addObra() }
        btnUpdateObra.setOnClickListener { updateObra() }



        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val header : View = navView.getHeaderView(0)


        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_home -> startActivity(Intent(this, DashboardActivity::class.java))
                R.id.borrar_obras -> changeActivityBorrarObra()
                R.id.borrar_convenios -> startActivity(Intent(this, BorrarConveniosActivity::class.java))

            }

            true

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
        LocationServices.getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)

        checkPermission()
        setupObraRecyclerView()
        loadObraFromDbIntoRecyclerView()
        setUserNameAndEmail()


    }

    private fun addObra() {
        val obra = edObra.text.toString()
        val descripcion = edDescripcion.text.toString()
        val usuario = sqliteHelper.getUsuario()[0].usuarionombre
        val syncobra = 0

        if (obra.isEmpty()) {
            Toast.makeText(this, "Ingresar Numero De Convenio", Toast.LENGTH_SHORT).show()
        }else{
            val std = ObraModel(obra = obra, descripcion = descripcion, items = 0, usuario = usuario, syncobra = syncobra)
            val status = sqliteHelper.insertConv(std)
            if (status > -1){
                Toast.makeText(this, "Convenio Creado", Toast.LENGTH_SHORT).show()
                loadObraFromDbIntoRecyclerView()
                clearEditText()
            } else{
                Toast.makeText(this, "ERROR: Convenio no Cargado", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun updateObra(){
        val obra = edObra.text.toString()
        val descripcion = edDescripcion.text.toString()
        val newobra = edUpdateObra.text.toString()

        if (obra.isEmpty()){
            Toast.makeText(this, "Ingresar Numero De Obra a Modificar", Toast.LENGTH_SHORT).show()
        } else if (obra == newobra) {
            Toast.makeText(this, "Numeros son iguales", Toast.LENGTH_SHORT).show()
            return
        }

        if (oList == null) return

        val oList = ObraModel(id = oList!!.id, obra = obra, descripcion = descripcion, items = oList!!.items, usuario = oList!!.usuario)
        val status = sqliteHelper.updateConvNumero(oList, newobra)
        if (status > -1){
            clearEditText()
            loadObraFromDbIntoRecyclerView()
        } else {
            Toast.makeText(this, "ERROR: Conv no Actualizada", Toast.LENGTH_SHORT).show()
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

    private fun clearEditText(){
        edObra.setText("")
        edDescripcion.setText("")
        edUpdateObra.setText("")
        edObra.requestFocus()
    }

    private fun setupObraRecyclerView() = binding.rvObras.apply {
        adapter = obraAdapter
        layoutManager = LinearLayoutManager(this@ConvenioActivity, LinearLayoutManager.VERTICAL, false)

        obraAdapter.setOnClickItem { obra ->
            Toast.makeText(applicationContext, obra.obra, Toast.LENGTH_SHORT).show()
            edObra.setText(obra.obra)
            edDescripcion.setText(obra.descripcion)
            oList = obra
        }

        obraAdapter.setOnClickFotoItem { obraFoto ->
            startNewActivityTomarFoto(obraFoto)
        }

    }

    private fun startNewActivityTomarFoto (oListintent: ObraModel){
        val intent = Intent(this@ConvenioActivity, ConvenioFoto::class.java)
        val id = oListintent.id
        val obra = oListintent.obra
        val items = oListintent.items
        val usuario = oListintent.usuario
        intent.putExtra("Id", id)
        intent.putExtra("Obra", obra)
        intent.putExtra("Items", items)
        intent.putExtra("Usuario", usuario)
        finish()
        startActivity(intent)
    }

    private fun loadObraFromDbIntoRecyclerView() {
        lifecycleScope.launch{
            val obra = loadObraFromDb()
            Log.d("TAG", obra.size.toString())
            obraAdapter.submitList(obra)
        }
    }

    private fun loadObraFromDb(): List<ObraModel> {
        return sqliteHelper.getAllConv()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initView(){
        edObra = findViewById(R.id.edObra)
        edDescripcion = findViewById(R.id.edDescripcion)
        edUpdateObra = findViewById(R.id.edUpdateObra)
        btnCrearObra = findViewById(R.id.btnCrearObra)
        btnUpdateObra = findViewById(R.id.btnUpdateObra)
        obraAdapter = ObraAdapter()

    }



    private fun changeActivityBorrarObra(){
        finish()
        val intent = Intent(this, BorrarObrasActivity::class.java)
        startActivity(intent)

    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

}