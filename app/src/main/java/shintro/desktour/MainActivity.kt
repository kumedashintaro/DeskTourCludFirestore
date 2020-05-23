package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSelect()

        val homeFragment = HomeFragment()

        findViewById<Button>(R.id.fragment_homeBtn).setOnClickListener {
            replaceFragment(homeFragment)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    private fun bottomSelect() {

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "投稿するにはログインが必要です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
        }

        personBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(this, NotLoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}



