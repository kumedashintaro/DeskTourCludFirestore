package shintro.desktour

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton

import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class MainActivity : AppCompatActivity() {

    private lateinit var youtubeBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private lateinit var parsonBtn: ImageButton
    private lateinit var addBtn: ImageButton
    val user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomselct()

    }















    private fun bottomselct(){

        // init image buttons
        youtubeBtn = findViewById(R.id.youtubeBtn)
        homeBtn = findViewById(R.id.homeBtn)
        addBtn = findViewById(R.id.addBtn)
        parsonBtn = findViewById(R.id.parsonBtn)

        //onclick listner
        youtubeBtn.setOnClickListener {

        }

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

