package shintro.desktour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {

            val email = email_edittext_login.text.toString()
            val password = paswward_edittext_login.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "E-mail 又は Password を入力して下さい ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.d("LoginActivity", "Email is: " + email)
            Log.d("LoginActivity", "Password: $password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (!it.isSuccessful)return@addOnCompleteListener

                    Log.d("LoginActivity", "Successfully created user with uid:${it.result?.user?.uid}")
                }
                .addOnFailureListener{
                    Toast.makeText(this, "ログインに失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                    Log.d("LoginActivity", "Failed to create user: ${it.message}")
                }
        }
    }
}
