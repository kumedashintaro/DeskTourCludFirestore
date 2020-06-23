package shintaro.desktour_cluod_firestore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.person_fragment.*

class PersonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.person_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userid = FirebaseAuth.getInstance().uid
        if (userid == null) {
            sign_in_screen_button.isVisible = true
            regsiter_text_view.isVisible = true
            sign_out_button.isVisible = false
            username_textview.isVisible = false
        } else {
            sign_in_screen_button.isVisible = false
            regsiter_text_view.isVisible = false
            sign_out_button.isVisible = true
            username_textview.isVisible = true
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$userid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(User::class.java)

                username_rogin_edittext.text = post?.username
                Picasso.get().load(post?.profileImageUrl).into(selectphoto_imageview)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        sign_out_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        sign_in_screen_button.setOnClickListener {

            val intent = Intent(activity, NotLoginActivity::class.java)
            startActivity(intent)
        }

        regsiter_text_view.setOnClickListener {

            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}