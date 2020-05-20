package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_not_login.*


class NotLoginActivity : AppCompatActivity() {
    private lateinit var youtubeBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private lateinit var parsonBtn: ImageButton
    private lateinit var addBtn: ImageButton
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_login)

        bottomSelect()

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
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d(
                        "LoginActivity",
                        "Successfully created user with uid:${it.result?.user?.uid}"
                    )
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ログインに失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                    Log.d("LoginActivity", "Failed to create user: ${it.message}")
                }
        }


        regsiter_text_view.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    private fun bottomSelect(){

        // init image buttons
//        youtubeBtn = findViewById(R.id.youtubeBtn)
        homeBtn = findViewById(R.id.homeBtn)
        addBtn = findViewById(R.id.addBtn)
        parsonBtn = findViewById(R.id.personBtn)

        //onclick listner
//        youtubeBtn.setOnClickListener {

//        }

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            if(user == null){
                Toast.makeText(this, "投稿するにはログインが必要です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
        }

        parsonBtn.setOnClickListener {
            if(user == null){
                val intent = Intent(this, NotLoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
