package shintro.desktour.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import shintro.desktour.MainActivity

import shintro.desktour.R

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

        sign_out_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)


        }
    }



}
