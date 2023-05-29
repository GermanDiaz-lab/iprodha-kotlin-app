
package com.example.test2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.databinding.ActivityBorrarConveniosBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class BorrarConveniosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBorrarConveniosBinding
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var obraBorrarAdapter: ObraBorrarAdapter

    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBorrarConveniosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        sqliteHelper = SQLiteHelper(this)

        // Menu...
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
                R.id.borrar_obras -> Toast.makeText(applicationContext, "Ya se encuentra aqui", Toast.LENGTH_SHORT).show()
                R.id.borrar_convenios -> startActivity(Intent(this, BorrarConveniosActivity::class.java))

            }

            true

        }

        //Menu fin...

        setupObraRecyclerView()
        loadObraFromDbIntoRecyclerView()
        setUserNameAndEmail()

    }

    private fun initView(){

        obraBorrarAdapter = ObraBorrarAdapter()

    }

    private fun setupObraRecyclerView() = binding.borrarObraItem.apply {
        adapter = obraBorrarAdapter
        layoutManager = LinearLayoutManager(this@BorrarConveniosActivity, LinearLayoutManager.VERTICAL, false)

        obraBorrarAdapter.setOnClickFotoItem { obraFoto ->
            alertBorrarObra(obraFoto)
        }

    }

    private fun loadObraFromDbIntoRecyclerView() {
        lifecycleScope.launch{
            val obra = loadObraFromDb()
            Log.d("TAG", obra.size.toString())
            obraBorrarAdapter.submitList(obra)
        }
    }

    private fun loadObraFromDb(): List<ObraModel> {
        return sqliteHelper.getAllConv()
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

    private fun alertBorrarObra (oListintent: ObraModel){
        Log.d("TAG", "Click")

        var builder = AlertDialog.Builder(this@BorrarConveniosActivity)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("Esta seguro de que quiere eliminar esta obra?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener { dialogInterface, i ->
            borrarObra(oListintent)
            sqliteHelper.borrarAllFotoConv(oListintent.id.toString())
            loadObraFromDbIntoRecyclerView()
            dialogInterface.cancel()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })
        val alert :AlertDialog = builder.create()
        alert.show()
    }

    private fun borrarObra(oListintent: ObraModel): Int {
        return sqliteHelper.borrarConvItems(oListintent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}