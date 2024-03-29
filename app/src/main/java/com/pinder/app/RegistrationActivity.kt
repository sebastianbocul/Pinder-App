package com.pinder.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.pinder.app.util.PaintText
import com.pinder.app.utils.BuildVariantsHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {
    private var logoTextView: TextView? = null;
    private var registerType: String? = null
    private var fragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        registerType = intent.getStringExtra("register_type")
        logoTextView = findViewById(R.id.logo_text_view)
        PaintText.paintLogo(logoTextView, "Pinder")
        handleBackArrow()
        when (registerType) {
            "email" -> {
                fragment = RegistrationEmailFragment()
            }
            "phone" -> {
                fragment = RegistrationPhoneFragment()
            }
            "external" -> {
                fragment = RegistrationExternalFragment()
            }
        }
        if (fragment == null) {
            deleteUser()
            logoutUser()
        } else {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.register_frame, fragment!!)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (registerType.equals("external") || registerType.equals("phone")) {
            deleteUser()
            logoutUser()
        }
        super.onBackPressed()
    }

    fun handleBackArrow() {
        var backArrowImage: ImageView = findViewById(R.id.back_arrow)
        BuildVariantsHelper.disableButton(backArrowImage)
        backArrowImage.setOnClickListener {
            val i = Intent(this, StartActivity::class.java)
            startActivity(i)
        }
    }

    private fun deleteUser() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.delete()
                .addOnCompleteListener { }
    }

    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
    }
}