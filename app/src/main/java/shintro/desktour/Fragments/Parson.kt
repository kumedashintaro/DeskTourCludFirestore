package shintro.desktour.Fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.email_edittext_login
import kotlinx.android.synthetic.main.activity_login.login_button
import kotlinx.android.synthetic.main.activity_login.paswward_edittext_login
import kotlinx.android.synthetic.main.fragment_parson.*
import shintro.desktour.MainActivity

import shintro.desktour.R
import shintro.desktour.RegisterActivity

/**
 * A simple [Fragment] subclass.
 */
class Parson : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parson, container, false)
    }

    override fun onStart() {
        super.onStart()


        login_button.setOnClickListener {

            val email = email_edittext_login.text.toString()
            val password = paswward_edittext_login.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(activity, "E-mail 又は Password を入力して下さい ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.d("LoginActivity", "Email is: " + email)
            Log.d("LoginActivity", "Password: $password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d(
                        "LoginActivity",
                        "Successfully created user with uid:${it.result?.user?.uid}"
                    )
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "ログインに失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                    Log.d("LoginActivity", "Failed to create user: ${it.message}")
                }
        }


        regsiter_text_view.setOnClickListener {

            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)

        }
    }
}