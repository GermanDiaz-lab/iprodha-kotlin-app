package com.example.test2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.test2.databinding.ActivityUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var sqliteHelper: SQLiteHelper

    private lateinit var tvmailid: TextView
    private lateinit var tvnombreid: TextView
    private lateinit var btnCerrarSesion: Button

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sqliteHelper = SQLiteHelper(this)
        firebaseAuth = FirebaseAuth.getInstance()


        tvmailid = binding.mailid
        tvnombreid = binding.nombreid
        btnCerrarSesion = binding.btnCerrarSesion

        btnCerrarSesion.setOnClickListener { signOut() }


        setUserNameAndEmail()
    }

    private fun setUserNameAndEmail() {
        val usrList =  sqliteHelper.getUsuario()

        tvnombreid.text = usrList[0].usuarionombre
        tvmailid.text = usrList[0].usuarioemail
    }


    private fun signOut() {
        sqliteHelper.borrarUsuario()
        startActivity(getLaunchIntent(this))
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        firebaseAuth.signOut();
    }
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, GoogelSignInActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

}