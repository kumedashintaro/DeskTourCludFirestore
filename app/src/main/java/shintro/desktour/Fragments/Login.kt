package shintro.desktour.Fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_login.*
import shintro.desktour.MainActivity

import shintro.desktour.R
import shintro.desktour.User

/**
 * A simple [Fragment] subclass.
 */
class Login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()

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

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

        }
    }
}
