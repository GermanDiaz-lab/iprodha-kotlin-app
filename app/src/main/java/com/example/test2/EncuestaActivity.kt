package com.example.test2

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Calendar
import java.text.SimpleDateFormat
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import com.example.test2.databinding.ActivityEncuestaBinding
import com.example.test2.util.LoadingDialog
import com.google.android.gms.location.*

class EncuestaActivity : AppCompatActivity() {

    companion object {
        val cities = arrayOf(
            "9 de Julio",
            "25 de Mayo",
            "Alba Posse",
            "Almafuerte",
            "Apóstoles",
            "Aristóbulo del Valle",
            "Arroyo del Medio",
            "Azara",
            "Barra Concepción",
            "Barrio Bernardino Rivadavia",
            "Barrio del Lago",
            "Barrio Rural",
            "Bernardo de Irigoyen",
            "Bonpland",
            "Caá Yarí",
            "Camión Cue",
            "Campo Grande",
            "Campo Ramón",
            "Campo Viera",
            "Candelaria",
            "Capioví",
            "Caraguatay",
            "Cerro Azul",
            "Cerro Corá",
            "Colonia Alberdi",
            "Colonia Alicia",
            "Candelaria",
            "Colonia Aparecida",
            "Colonia Aurora",
            "Colonia Delicia",
            "Colonia Helvecia",
            "Colonia Polana",
            "Colonia Victoria",
            "Comandante Andresito",
            "Concepción de la Sierra",
            "Corpus Christi",
            "Cruce Caballero",
            "Domingo Savio",
            "Dos Arroyos",
            "Dos de Mayo",
            "El Alcázar",
            "Dos Hermanas",
            "El Piñalito",
            "El Salto",
            "El Soberbio",
            "Eldorado",
            "Estación Apóstoles",
            "Fracrán",
            "Fachinal",
            "Florentino Ameghino",
            "Garuhapé",
            "Garuhapé-Mi",
            "General Alvear",
            "General Urquiza",
            "Gobernador Lanusse",
            "Gobernador López",
            "Gobernador Roca",
            "Guaraní",
            "Hipólito Yrigoyen",
            "Integración",
            "Itacaruaré",
            "Jardín América",
            "La Corita",
            "Laharrague",
            "Leandro N. Alem",
            "Loreto",
            "Los Helechos",
            "Mártires",
            "Mbopicuá",
            "Mojón Grande",
            "Montecarlo",
            "Nemesio Parma",
            "Nueve de Julio Kilómetro 20",
            "Oasis",
            "Olegario Víctor Andrade",
            "Panambí",
            "Panambí Kilómetro 8",
            "Paraje Fontana",
            "Peñón del Teyú Cuaré",
            "Pindapoy",
            "Piray Kilómetro 18",
            "Pozo Azul",
            "Primero de Mayo",
            "Profundidad",
            "Pueblo Illia",
            "Puerto Azara",
            "Puerto Esperanza",
            "Puerto Iguazú",
            "Puerto Leoni",
            "Puerto Libertad",
            "Puerto Mado",
            "Puerto Mineral",
            "Puerto Pinares",
            "Puerto Piray",
            "Puerto Rico",
            "Posadas",
            "Garupa",
            "Puerto Santa Ana",
            "Reserva natural de la defensa Puerto Península",
            "Roca Chica",
            "Ruiz de Montoya",
            "Salto Encantado",
            "San Alberto",
            "San Antonio",
            "San Francisco de Asís",
            "San Gotardo",
            "San Ignacio",
            "San Javier",
            "San José",
            "San Martín",
            "San Pedro",
            "San Vicente",
            "Santa Ana",
            "Santa María",
            "Santa Rita",
            "Santiago de Liniers",
            "Santo Pipó",
            "Tarumá",
            "Terciados Paraíso",
            "Tobuna",
            "Torta Quemada",
            "Tres Capones",
            "Valle Hermoso",
            "Villa Akerman",
            "Villa Bonita",
            "Villa Libertad",
            "Villa Parodi",
            "Villa Roulet",
            "Villa Salto Encantado",
            "Villalonga",
            "Wanda"
        )
    }

    private lateinit var binding: ActivityEncuestaBinding

    private lateinit var sqliteHelper: SQLiteHelper

    private var nombreList = ArrayList<String>()
    private var empleoList = ArrayList<String>()
    private var empleoNumero = ArrayList<Int>()
    private var ocupacionList = ArrayList<String>()
    private var ocupacionNumero = ArrayList<Int>()
    private var telefonoList = ArrayList<String>()
    private var ingresosList = ArrayList<String>()
    private var domicilioList = ArrayList<String>()
    private var fechaList = ArrayList<Array<Int>>()
    private var lugarList = ArrayList<String>()
    private var fecha: String = ""
    private var numeroDeIngresos: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latestFotoGeo: String = ""
    private var latestLatitud: String = ""
    private var latestLongitud: String = ""

    private var backButtonCheck: Int = 0

    private var iList: InformeModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEncuestaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRequestedOrientationBasedOnDevice()

        sqliteHelper = SQLiteHelper(this)

        val autoCompleteTextView = binding.a8
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities)
        autoCompleteTextView.setAdapter(adapter)

        setEditTextFilters(binding.a2)
        setEditTextFilters(binding.a4)
        setEditTextFilters(binding.a28)
        setEditTextFilters(binding.a29)
        setEditTextFilters(binding.a30)
        setEditTextFilters(binding.a66)
        setEditTextFilters(binding.a70)
        setEditTextFiltersFloat(binding.a61)
        setEditTextFiltersFloat(binding.a67)
        setEditTextFiltersFloat(binding.a71)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



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

        onBackPressedDispatcher.addCallback(this) {
            // Place your custom logic here, if needed
            if (backButtonCheck == 0){
                Toast.makeText(this@EncuestaActivity, "Boton habilitado apretar otra vez para volver atras", Toast.LENGTH_SHORT).show()
                this.remove()
            } else {
                Toast.makeText(this@EncuestaActivity, "Boton bloqueado para evitar errores", Toast.LENGTH_SHORT).show()
            }
            // To call the original onBackPressed behavior, use the following line:
            // this.remove() // Uncomment this line if you want the original behavior
        }

        binding.siguiente1.setOnClickListener {
            binding.preguntas1.visibility = View.GONE
            binding.preguntas2.visibility = View.VISIBLE
            binding.sView.scrollTo(0, 0)
            backButtonCheck += 1
        }
        binding.siguiente2.setOnClickListener {
            binding.preguntas2.visibility = View.GONE
            binding.preguntas3.visibility = View.VISIBLE
            binding.sView.scrollTo(0, 0)
        }
        binding.siguiente3.setOnClickListener {
            binding.preguntas3.visibility = View.GONE
            binding.preguntas4.visibility = View.VISIBLE
            binding.sView.scrollTo(0, 0)
        }
        binding.siguiente4.setOnClickListener {
            binding.preguntas4.visibility = View.GONE
            binding.preguntas5.visibility = View.VISIBLE
            binding.title5.text = "Situacion Economica - FINANCIERA INGRESOS ${numeroDeIngresos + 1}"
            binding.sView.scrollTo(0, 0)
        }
        binding.siguiente5.setOnClickListener {
            binding.preguntas5.visibility = View.GONE
            binding.preguntas6.visibility = View.VISIBLE
            addIngresoToList(binding.a57, binding.a58, binding.a59, binding.a60, binding.a61, binding.a62, binding.a64,
                nombreList, empleoList, empleoNumero, ocupacionList, ocupacionNumero, telefonoList,
                ingresosList, domicilioList, lugarList)
            binding.sView.scrollTo(0, 0)
        }
        binding.siguiente6.setOnClickListener {
            binding.preguntas6.visibility = View.GONE
            binding.preguntas7.visibility = View.VISIBLE
            binding.sView.scrollTo(0, 0)
        }
        binding.siguiente7.setOnClickListener {
            binding.preguntas7.visibility = View.GONE
            binding.preguntas8.visibility = View.VISIBLE
            binding.sView.scrollTo(0, 0)
        }
        binding.finalizar.setOnClickListener {

            getLocation()
        }
        binding.atras1.setOnClickListener {
            binding.preguntas1.visibility = View.VISIBLE
            binding.preguntas2.visibility = View.GONE
            binding.sView.scrollTo(0, 0)
            backButtonCheck -= 1
        }
        binding.atras2.setOnClickListener {
            binding.preguntas2.visibility = View.VISIBLE
            binding.preguntas3.visibility = View.GONE
            binding.sView.scrollTo(0, 0)
        }
        binding.atras3.setOnClickListener {
            binding.preguntas3.visibility = View.VISIBLE
            binding.preguntas4.visibility = View.GONE
            binding.sView.scrollTo(0, 0)
        }
        binding.atras4.setOnClickListener {
            if (numeroDeIngresos == 0) {
                binding.preguntas4.visibility = View.VISIBLE
                binding.preguntas5.visibility = View.GONE
            } else {
                backFillForm(
                    binding.a57,
                    binding.a58,
                    binding.a59,
                    binding.a60,
                    binding.a61,
                    binding.a62,
                    binding.a64,
                    nombreList,
                    empleoNumero,
                    ocupacionNumero,
                    telefonoList,
                    ingresosList,
                    domicilioList,
                    lugarList
                )
                binding.title5.text = "Situacion Economica - FINANCIERA INGRESOS ${numeroDeIngresos + 1}"
            }
            binding.sView.scrollTo(0, 0)
        }
        binding.atras5.setOnClickListener {
            binding.preguntas5.visibility = View.VISIBLE
            binding.preguntas6.visibility = View.GONE
            backFillForm(binding.a57, binding.a58, binding.a59, binding.a60, binding.a61, binding.a62, binding.a64,
                nombreList, empleoNumero, ocupacionNumero, telefonoList,
                ingresosList, domicilioList, lugarList)
            binding.title5.text = "Situacion Economica - FINANCIERA INGRESOS ${numeroDeIngresos + 1}"
            binding.sView.scrollTo(0, 0)
        }
        binding.atras6.setOnClickListener {
            binding.preguntas6.visibility = View.VISIBLE
            binding.preguntas7.visibility = View.GONE
            binding.sView.scrollTo(0, 0)
        }
        binding.atras7.setOnClickListener {
            binding.preguntas7.visibility = View.VISIBLE
            binding.preguntas8.visibility = View.GONE
            binding.sView.scrollTo(0, 0)
        }
        binding.newImporte.setOnClickListener {
            if (numeroDeIngresos >= 3){
                Toast.makeText(applicationContext, "Maximo numero de Ingresos alcanzado", Toast.LENGTH_SHORT).show()
            } else {
                addIngresoToList(
                    binding.a57,
                    binding.a58,
                    binding.a59,
                    binding.a60,
                    binding.a61,
                    binding.a62,
                    binding.a64,
                    nombreList,
                    empleoList,
                    empleoNumero,
                    ocupacionList,
                    ocupacionNumero,
                    telefonoList,
                    ingresosList,
                    domicilioList,
                    lugarList
                )
                binding.title5.text = "Situacion Economica - FINANCIERA INGRESOS ${numeroDeIngresos + 1}"

            }
        }

        binding.dateButtonA18.setOnClickListener{
            showDatePickerDialog()
        }
        binding.dateButtonA63.setOnClickListener{
            showDatePickerDialogForIngresos()
        }
    }


    private fun getLocation() {
        // Check permissions
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }

        // Fetch location
        val formData1 = getFormData1()
        if (getOrDefault(formData1, "a2").isNullOrEmpty()) {
            Toast.makeText(applicationContext, "INGRESAR DNI TITULAR", Toast.LENGTH_SHORT).show()
        } else {
            fetchLocation()
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        // TODO: Request location permissions here
    }

    private fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                latestFotoGeo = "Latitud: ${it.latitude} Longitud: ${it.longitude}"
                latestLatitud = it.latitude.toString()
                latestLongitud = it.longitude.toString()
                Log.d("GEO", latestFotoGeo)

                Toast.makeText(applicationContext, "GEO SET", Toast.LENGTH_SHORT).show()
                addFormDataToDB()
            } ?: run {
                Toast.makeText(applicationContext, "NO GPS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addIngresoToList(nombre: EditText, empleo: Spinner, ocupacion: Spinner,
                         telefono: EditText, ingresos: EditText, domicilio: EditText,
                         lugar: EditText,
                         nombreList: ArrayList<String>, empleoList: ArrayList<String>, empleoNumero: ArrayList<Int>,
                         ocupacionList: ArrayList<String>, ocupacionNumero: ArrayList<Int>, telefonoList: ArrayList<String>,
                         ingresosList: ArrayList<String>, domicilioList: ArrayList<String>, lugarList: ArrayList<String>) {

            numeroDeIngresos += 1

            val nombreValue = nombre.text.toString()
            val empleoValue = radioCheck(empleo.selectedItem?.toString() ?: "")
            val empleoValueNumero = empleo.selectedItemPosition
            val ocupacionValue = radioCheck(ocupacion.selectedItem?.toString() ?: "")
            val ocupacionValueNumero = ocupacion.selectedItemPosition
            val telefonoValue = telefono.text.toString()
            val ingresosValue = ingresos.text.toString()
            val domicilioValue = domicilio.text.toString()
            val lugarValue = lugar.text.toString()

            nombreList.add(nombreValue)
            empleoList.add(empleoValue)
            empleoNumero.add(empleoValueNumero)
            ocupacionList.add(ocupacionValue)
            ocupacionNumero.add(ocupacionValueNumero)
            telefonoList.add(telefonoValue)
            ingresosList.add(ingresosValue)
            domicilioList.add(domicilioValue)
            lugarList.add(lugarValue)

            // clear the input fields of the form
            nombre.text.clear()
            empleo.setSelection(0)
            ocupacion.setSelection(0)
            telefono.text.clear()
            ingresos.text.clear()
            domicilio.text.clear()
            lugar.text.clear()


            binding.sView.scrollTo(0, 0)

    }

    private fun backFillForm(nombre: EditText, empleo: Spinner, ocupacion: Spinner,
                             telefono: EditText, ingresos: EditText, domicilio: EditText,
                             lugar: EditText, nombreList: ArrayList<String>, empleoNumero: ArrayList<Int>,
                             ocupacionNumero: ArrayList<Int>, telefonoList: ArrayList<String>,
                             ingresosList: ArrayList<String>, domicilioList: ArrayList<String>,
                             lugarList: ArrayList<String>){

        numeroDeIngresos -= 1

        nombre.setText(nombreList[numeroDeIngresos])
        empleo.setSelection(empleoNumero[numeroDeIngresos])
        ocupacion.setSelection(ocupacionNumero[numeroDeIngresos])
        telefono.setText(telefonoList[numeroDeIngresos])
        ingresos.setText(ingresosList[numeroDeIngresos])
        domicilio.setText(domicilioList[numeroDeIngresos])
        lugar.setText(lugarList[numeroDeIngresos])


    }

    private fun checkAndAddAtIndex(list: ArrayList<Array<Int>>, n: Int) {
        if (n >= list.size) {
            // Add new array to the list until it has index n
            while (list.size <= n) {
                list.add(arrayOf())
            }
        }
    }

    private fun showDatePickerDialogForIngresos() {
        // Get Current Date
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                Toast.makeText(this, "$dayOfMonth/${monthOfYear + 1}/$year", Toast.LENGTH_SHORT).show()
                checkAndAddAtIndex(fechaList, numeroDeIngresos)
                val fechaValue = arrayOf(dayOfMonth, monthOfYear + 1, year)
                fechaList[numeroDeIngresos] = fechaValue
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        // Get Current Date
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                Toast.makeText(this, "$dayOfMonth/${monthOfYear + 1}/$year", Toast.LENGTH_SHORT).show()
                fecha = "$dayOfMonth/${monthOfYear + 1}/$year"
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun getFormData1(): Map<String, String> {
        val formData = mutableMapOf<String, String>()

        // Retrieve values from EditText views
        formData["a1"] = binding.a1.text.toString()
        formData["a2"] = binding.a2.text.toString()
        formData["a3"] = binding.a3.text.toString()
        formData["a4"] = binding.a4.text.toString()
        formData["a5"] = binding.a5.text.toString()
        formData["a6"] = binding.a6.text.toString()
        val a7Spinner = findViewById<Spinner>(R.id.a7)
        formData["a7"] = radioCheck(a7Spinner.selectedItem?.toString() ?: "")
        formData["a8"] = binding.a8.text.toString()
        formData["a9"] = binding.a9.text.toString()
        val a10Spinner = findViewById<Spinner>(R.id.a10)
        formData["a10"] = radioCheck(a10Spinner.selectedItem?.toString() ?: "")
        val a11Spinner = findViewById<Spinner>(R.id.a11)
        formData["a11"] = radioCheck(a11Spinner.selectedItem?.toString() ?: "")
        formData["a12"] = binding.a12.text.toString()
        formData["a13"] = binding.a13.text.toString()
        formData["a14"] = binding.a14.text.toString()
        formData["a15"] = binding.a15.text.toString()
        formData["a16"] = binding.a16.text.toString()
        formData["a17"] = binding.a17.text.toString()
        formData["a19"] = binding.a19.text.toString()


        return formData
    }

    private fun getFormData2(): Map<String, String> {
        val formData = mutableMapOf<String, String>()

        formData["a20"] = binding.a20.text.toString()
        formData["a21"] = binding.a21.text.toString()
        formData["a22"] = binding.a22.text.toString()
        formData["a23"] = binding.a23.text.toString()
        formData["a24"] = binding.a24.text.toString()
        formData["a25"] = binding.a25.text.toString()
        formData["a26"] = binding.a26.text.toString()

        val selectedRadioButton = findViewById<RadioButton>(binding.a27.checkedRadioButtonId)
        val selectedRadioButtonText = selectedRadioButton?.text?.toString() ?: ""
        formData["a27"] = selectedRadioButtonText

        return formData
    }

    private fun getFormData3(): Map<String, String> {
        val dataMap = mutableMapOf<String, String>()
        val dni = binding.a28.text.toString()
        val dniConyuge = binding.a29.text.toString()
        val dniHijos = binding.a30.text.toString()
        val actaMatrimonio = binding.a31.text.toString()
        val unionConvivencial = binding.a32.text.toString()
        val partidasNacimiento = binding.a33.text.toString()
        val contactoTelefonico = binding.a34.text.toString()
        val observacion = binding.a35.text.toString()
        val escolaridad = binding.a36.isChecked.toString()
        val escuelaPublica = binding.a37.isChecked.toString()
        val escuelaPrivada = binding.a41.isChecked.toString()
        val gastosEscolares = binding.a42.isChecked.toString()
        val a38Spinner = findViewById<Spinner>(R.id.a38)
        val estadoCivilTitular = radioCheck(a38Spinner.selectedItem?.toString() ?: "")
        val a39Spinner = findViewById<Spinner>(R.id.a39)
        val estadoCivilConyuge = radioCheck(a39Spinner.selectedItem?.toString() ?: "")
        val correoElectronico = binding.a40.text.toString()

        dataMap["a28"] = dni
        dataMap["a29"] = dniConyuge
        dataMap["a30"] = dniHijos
        dataMap["a31"] = actaMatrimonio
        dataMap["a32"] = unionConvivencial
        dataMap["a33"] = partidasNacimiento
        dataMap["a34"] = contactoTelefonico
        dataMap["a35"] = observacion
        dataMap["a36"] = escolaridad
        dataMap["a37"] = escuelaPublica
        dataMap["a41"] = escuelaPrivada
        dataMap["a42"] = gastosEscolares
        dataMap["a38"] = estadoCivilTitular
        dataMap["a39"] = estadoCivilConyuge
        dataMap["a40"] = correoElectronico

        return dataMap
    }

    private fun getFormData4(): Map<String, String> {

        val formData = mutableMapOf<String, String>()
        formData["a43"] = binding.a43.text.toString()
        formData["a49"] = binding.a49.isChecked.toString()
        formData["a50"] = binding.a50.text.toString()
        formData["a45"] = binding.a45.text.toString()
        formData["a46"] = binding.a46.isChecked.toString()
        formData["a47"] = binding.a47.isChecked.toString()
        formData["a48"] = binding.a48.isChecked.toString()
        formData["a51"] = binding.a51.text.toString()
        formData["a52"] = binding.a52.text.toString()
        formData["a53"] = binding.a53.isChecked.toString()
        formData["a55"] = binding.a55.isChecked.toString()
        formData["a56"] = binding.a56.isChecked.toString()
        formData["a54"] = binding.a54.text.toString()

        val selectedRadioButton = findViewById<RadioButton>(binding.a44.checkedRadioButtonId)
        val selectedRadioButtonText = selectedRadioButton?.text?.toString() ?: ""
        formData["a44"] = selectedRadioButtonText
        return formData
    }

    private fun getFormData5(): Map<String, String> {
        val formData = mutableMapOf<String, String>()
        formData["a65"] = binding.a65.text.toString()
        formData["a66"] = binding.a66.text.toString()
        formData["a67"] = binding.a67.text.toString()

        val a68Spinner = findViewById<Spinner>(R.id.a68)
        formData["a68"] = radioCheck(a68Spinner.selectedItem?.toString() ?: "")

        formData["a69"] = binding.a69.text.toString()
        formData["a70"] = binding.a70.text.toString()
        formData["a71"] = binding.a71.text.toString()

        val a72Spinner = findViewById<Spinner>(R.id.a72)
        formData["a72"] = radioCheck(a72Spinner.selectedItem?.toString() ?: "")

        formData["a73"] = binding.a73.text.toString()
        formData["a74"] = binding.a74.text.toString()
        formData["a75"] = binding.a75.text.toString()
        formData["a76"] = binding.a76.text.toString()
        formData["a77"] = binding.a77.text.toString()
        formData["a78"] = binding.a78.text.toString()

        val a79Spinner = findViewById<Spinner>(R.id.a79)
        formData["a79"] = radioCheck(a79Spinner.selectedItem?.toString() ?: "")

        formData["a80"] = binding.a80.text.toString()

        return formData
    }

    private fun getFormData6(): Map<String, String> {
        val formData = mutableMapOf<String, String>()

        formData["a81"] = binding.a81.isChecked.toString()

        formData["a82"] = binding.a82.text.toString()

        val selectedRadioButton83 = findViewById<RadioButton>(binding.a83.checkedRadioButtonId)
        val selectedRadioButtonText83 = selectedRadioButton83?.text?.toString() ?: ""
        formData["a83"] = selectedRadioButtonText83

        formData["a84"] = binding.a84.isChecked.toString()

        formData["a85"] = binding.a85.text.toString()

        val selectedRadioButton86 = findViewById<RadioButton>(binding.a86.checkedRadioButtonId)
        val selectedRadioButtonText86 = selectedRadioButton86?.text?.toString() ?: ""
        formData["a86"] = selectedRadioButtonText86

        formData["a87"] = binding.a87.isChecked.toString()

        formData["a88"] = binding.a88.text.toString()

        val selectedRadioButton89 = findViewById<RadioButton>(binding.a89.checkedRadioButtonId)
        val selectedRadioButtonText89 = selectedRadioButton89?.text?.toString() ?: ""
        formData["a89"] = selectedRadioButtonText89

        formData["a90"] = binding.a90.isChecked.toString()

        formData["a91"] = binding.a91.text.toString()

        val selectedRadioButton92 = findViewById<RadioButton>(binding.a92.checkedRadioButtonId)
        val selectedRadioButtonText92 = selectedRadioButton92?.text?.toString() ?: ""
        formData["a92"] = selectedRadioButtonText92

        formData["a93"] = binding.a93.text.toString()

        return formData
    }

    private fun getFormData7(): Map<String, String> {
        val formData = mutableMapOf<String, String>()

        formData["a94"] = binding.a94.text.toString()
        formData["a95"] = binding.a95.text.toString()
        formData["a96"] = binding.a96.text.toString()
        formData["a97"] = binding.a97.text.toString()

        val a98SelectedItem = binding.a98.selectedItem?.toString() ?: ""
        formData["a98"] = radioCheck(a98SelectedItem)

        formData["a99"] = binding.a99.text.toString()
        formData["a100"] = binding.a100.text.toString()
        formData["a101"] = binding.a101.text.toString()

        return formData
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

    private fun addFormDataToDB(){

        val loading = LoadingDialog(this)
        loading.startLoading()
        val formData1 = getFormData1()
        val formData2 = getFormData2()
        val formData3 = getFormData3()
        val formData4 = getFormData4()
        val formData5 = getFormData5()
        val formData6 = getFormData6()
        val formData7 = getFormData7()
        val usrList =  sqliteHelper.getUsuario()
        val fechainforme = getCurrentDate()
        var escolaridad = ""
        if (formData3["a36"] == "true"){
            escolaridad = "SI"
            if (formData3["a37"] == "true"){
                escolaridad += " ESCUELA PUBLICA"
                if (formData3["a37"] == "true"){
                    escolaridad += " ESCUELA PRIVADA"
                    if (formData3["a37"] == "true"){
                        escolaridad += " GASTOS ESCOLARES"
                    }
                }
            }
        }

        var fechadia1 = checkValueExists(0, 0)
        var fechames1 = checkValueExists(0, 1)
        var fechano1 = checkValueExists(0, 2)
        var fechadia2 = checkValueExists(1, 0)
        var fechames2 = checkValueExists(1, 1)
        var fechano2 = checkValueExists(1, 2)
        var fechadia3 = checkValueExists(2, 0)
        var fechames3 = checkValueExists(2, 1)
        var fechano3 = checkValueExists(2, 2)
        var fechadia4 = checkValueExists(3, 0)
        var fechames4 = checkValueExists(3, 1)
        var fechano4 = checkValueExists(3, 2)

        iList = InformeModel(
            usuario = usrList[0].usuarionombre,
            sitjur = getOrDefault(formData1, "a7"),
            exp = getOrDefault(formData1, "a5"),
            estadodeud = getOrDefault(formData1, "a14"),
            notdeud = getOrDefault(formData1, "a15"),
            titular = getOrDefault(formData1, "a1"),
            nota = getOrDefault(formData1, "a6"),
            fecha = fechainforme,
            localidad = getOrDefault(formData1, "a8"),
            legajo = getOrDefault(formData1, "a9"),
            ocupante = getOrDefault(formData1, "a3"),
            ocupantedni = getOrDefault(formData1, "a4"),
            residedesde = fecha,
            tipologia = getOrDefault(formData1, "a16"),
            estado = getOrDefault(formData1, "a17"),
            titulo = getOrDefault(formData1, "a10"),
            sithab = getOrDefault(formData1, "a11"),
            identviv = getOrDefault(formData1, "a12"),
            ampliacion = getOrDefault(formData1, "a13"),
            observacion = getOrDefault(formData1, "a19"),
            antecedenteiprodha = getOrDefault(formData2, "a20"),
            antecedenteviv = getOrDefault(formData2, "a21"),
            antecedentearreglo = getOrDefault(formData2, "a25"),
            antecedentelote = getOrDefault(formData2, "a26"),
            antecedentemv = getOrDefault(formData2, "a22"),
            antecedentevr = getOrDefault(formData2, "a23"),
            comoaccede = getOrDefault(formData2, "a27"),
            otrosinicio = getOrDefault(formData2, "a24"),
            validogrupo = getOrDefault(formData2, "a25"),
            titulardni = getOrDefault(formData1, "a2"),
            estadociviltitular = getOrDefault(formData3, "a38"),
            estadocivilconyuge = getOrDefault(formData3, "a39"),
            dniconyuge = getOrDefault(formData3, "a29"),
            dnihijos = getOrDefault(formData3, "a30"),
            actadematrimonio = getOrDefault(formData3, "a31"),
            unionconvivencial = getOrDefault(formData3, "a32"),
            partidasnac = getOrDefault(formData3, "a33"),
            contactotelefonico = getOrDefault(formData3, "a34"),
            correoelectronico = getOrDefault(formData3, "a40"),
            observaciongrupo = getOrDefault(formData3, "a35"),
            escolaridad = escolaridad,
            obrasocial = getOrDefault(formData4, "a43"),
            tienecud = getOrDefault(formData4, "a49"),
            vigenciacud = getOrDefault(formData4, "a50"),
            discapacidad = getOrDefault(formData4, "a44"),
            diagnostico = getOrDefault(formData4, "a45"),
            silladeruedas = getOrDefault(formData4, "a46"),
            muletas = getOrDefault(formData4, "a47"),
            andador = getOrDefault(formData4, "a48"),
            otrosmovilidad = getOrDefault(formData4, "a51"),
            observacionsalud = getOrDefault(formData4, "a52"),
            trasnsplantado = getOrDefault(formData4, "a53"),
            carnet = getOrDefault(formData4, "a55"),
            listadeespera = getOrDefault(formData4, "a56"),
            observaciontrasplante = getOrDefault(formData4, "a54"),
            ingreso1 = getItemOrEmptyString(ingresosList, 0),
            ingreso2 = getItemOrEmptyString(ingresosList, 1),
            ingreso3 = getItemOrEmptyString(ingresosList, 2),
            ingreso4 = getItemOrEmptyString(ingresosList, 3),
            nombreingreso1 = getItemOrEmptyString(nombreList, 0),
            nombreingreso2 = getItemOrEmptyString(nombreList, 1),
            nombreingreso3 = getItemOrEmptyString(nombreList, 2),
            nombreingreso4 = getItemOrEmptyString(nombreList, 3),
            categorialaboral1 = getItemOrEmptyString(empleoList, 0),
            categorialaboral2 = getItemOrEmptyString(empleoList, 1),
            categorialaboral3 = getItemOrEmptyString(empleoList, 2),
            categorialaboral4 = getItemOrEmptyString(empleoList, 3),
            ocupacion1 = getItemOrEmptyString(ocupacionList, 0),
            ocupacion2 = getItemOrEmptyString(ocupacionList, 1),
            ocupacion3 = getItemOrEmptyString(ocupacionList, 2),
            ocupacion4 = getItemOrEmptyString(ocupacionList, 3),
            telefono1 = getItemOrEmptyString(telefonoList, 0),
            telefono2 = getItemOrEmptyString(telefonoList, 1),
            telefono3 = getItemOrEmptyString(telefonoList, 2),
            telefono4 = getItemOrEmptyString(telefonoList, 3),
            domicilio1 = getItemOrEmptyString(domicilioList, 0),
            domicilio2 = getItemOrEmptyString(domicilioList, 1),
            domicilio3 = getItemOrEmptyString(domicilioList, 2),
            domicilio4 = getItemOrEmptyString(domicilioList, 3),
            lugar1 = getItemOrEmptyString(lugarList, 0),
            lugar2 = getItemOrEmptyString(lugarList, 1),
            lugar3 = getItemOrEmptyString(lugarList, 2),
            lugar4 = getItemOrEmptyString(lugarList, 3),
            fechadelrecibo1 = "$fechadia1/$fechames1/$fechano1",
            fechadelrecibo2 = "$fechadia2/$fechames2/$fechano2",
            fechadelrecibo3 = "$fechadia3/$fechames3/$fechano3",
            fechadelrecibo4 = "$fechadia4/$fechames4/$fechano4",
            principalpagador = getOrDefault(formData5, "a65"),
            dnipagador = getOrDefault(formData5, "a66"),
            ingresospagador = getOrDefault(formData5, "a67"),
            catlaboralpagador = getOrDefault(formData5, "a68"),
            garante = getOrDefault(formData5, "a69"),
            dnigarante = getOrDefault(formData5, "a70"),
            ingresosgarante = getOrDefault(formData5, "a71"),
            catlaboralgarante = getOrDefault(formData5, "a72"),
            declaracionjurada = getOrDefault(formData5, "a73"),
            titularprestamos = getOrDefault(formData5, "a74"),
            prestamomonto = getOrDefault(formData5, "a75"),
            plazo = getOrDefault(formData5, "a76"),
            consumomensual = getOrDefault(formData5, "a78"),
            tarjetas = getOrDefault(formData5, "a77"),
            tipodetarjeta = getOrDefault(formData5, "a79"),
            observacionessiteco = getOrDefault(formData5, "a80"),
            luz = getOrDefault(formData6, "a81"),
            montoluz = getOrDefault(formData6, "a82"),
            luzformal = getOrDefault(formData6, "a83"),
            agua = getOrDefault(formData6, "a84"),
            aguamonto = getOrDefault(formData6, "a85"),
            aguaformal = getOrDefault(formData6, "a86"),
            cable = getOrDefault(formData6, "a87"),
            cablemonto = getOrDefault(formData6, "a88"),
            cableformal = getOrDefault(formData6, "a89"),
            internet = getOrDefault(formData6, "a90"),
            internetmonto = getOrDefault(formData6, "a91"),
            internetformal = getOrDefault(formData6, "a92"),
            observacionesservicios = getOrDefault(formData6, "a93"),
            cuotasocial = getOrDefault(formData7, "a94"),
            cuotaprovisoria = getOrDefault(formData7, "a95"),
            cuotaestandar = getOrDefault(formData7, "a96"),
            planventa = getOrDefault(formData7, "a97"),
            planalquiler = getOrDefault(formData7, "a98"),
            otrosmodalidad = getOrDefault(formData7, "a99"),
            plazopresentacion = getOrDefault(formData7, "a100"),
            observacionesplazo = getOrDefault(formData7, "a101"),
            informelatitud = latestLatitud,
            informelongitud = latestLongitud
        )

        val status = sqliteHelper.insertInforme(iList!!)
        if (status > -1){
            Toast.makeText(this, "SQL GUARDADO", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@EncuestaActivity, FirmaActivity::class.java)
            intent.putExtra("Dni", getOrDefault(formData1, "a2"))
            loading.isDismiss()
            finish()
            startActivity(intent)
        } else {
            Toast.makeText(this, "ERROR SQL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValueExists(a: Int, b: Int): Int {
        return if (a >= 0 && a < fechaList.size && b >= 0 && b < fechaList[a].size) {
            fechaList[a][b]
        } else {
            0
        }
    }

    private fun getItemOrEmptyString(list: ArrayList<String>, index: Int): String {
        return list.getOrNull(index) ?: ""
    }

    private fun getOrDefault(map: Map<String, String>, key: String): String {
        return map[key] ?: ""
    }

    private fun radioCheck(text: String): String{
        if (text == "SELECCIONAR"){
            return ""
        } else {
            return text
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun setEditTextFilters(editText: EditText) {
        val filter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val pattern = "\\d+".toRegex()
                if (source != null && !pattern.matches(source)) {
                    return ""
                }
                return null
            }
        }
        editText.filters = arrayOf(filter)
    }

    private fun setEditTextFiltersFloat(editText: EditText) {
        val filter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val pattern = "^\\d*(\\.\\d+)?$".toRegex()
                if (source != null && !pattern.matches(source)) {
                    return ""
                }
                return null
            }
        }
        editText.filters = arrayOf(filter)
    }


}