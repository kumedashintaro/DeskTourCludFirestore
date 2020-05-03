package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        login_button_register.setOnClickListener {
            performRegister()
        }

        selectophoto_button.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }


    private fun performRegister(){
        val email = email_edittext.text.toString()
        val password = paswward_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-mail 又は Password を入力して下さい ", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful)return@addOnCompleteListener

                Log.d("RegisterActivity", "Successfully created user with uid:${it.result?.user?.uid}")
            }
            .addOnFailureListener{
                Toast.makeText(this, "登録に失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
            }

    }
}
