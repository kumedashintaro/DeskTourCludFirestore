package shintro.desktour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.derail_desk_user.*
import kotlinx.android.synthetic.main.detail_desk_image_comment.*


class DetailDeskActivity : AppCompatActivity() {

    var toDesk: Desk? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_desk)

        toDesk = intent.getParcelableExtra<Desk>(MainActivity.DESK_KEY)
        Log.d("DetailActivity", "key: " + toDesk)

        profileset()
        commentset()





    }





    private fun profileset(){

        val DeskUid = toDesk?.uid
        Log.d("DetailActivity", "uid: " + DeskUid)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$DeskUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(User::class.java)

                detail_desl_username.setText(post?.username)
                Picasso.get().load(post?.profileImageUrl).into(user_imageview_detail_desk)

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    private fun commentset(){
        val DeskComment = toDesk?.comment
        val DeskImage = toDesk?.profileImageUrl
        Log.d("DetailActivity", "comment: " + DeskComment)

        detail_desk_comment_textview.setText(DeskComment)
        Picasso.get().load(DeskImage).into(detail_desk_imageview)

    }



}