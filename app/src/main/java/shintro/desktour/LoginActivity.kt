package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var youtubeBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private lateinit var parsonBtn: ImageButton
    private lateinit var addBtn: ImageButton

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bottomselct()

        val user = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(User::class.java)

                username_rogin_edittext.setText(post?.username)
                Picasso.get().load(post?.profileImageUrl).into(selectphoto_imageview)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        sign_out_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    private fun bottomselct(){

        // init image buttons
//        youtubeBtn = findViewById(R.id.youtubeBtn)
        homeBtn = findViewById(R.id.homeBtn)
        addBtn = findViewById(R.id.addBtn)
        parsonBtn = findViewById(R.id.personBtn)

        //onclick listner
//        youtubeBtn.setOnClickListener {
//
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
