package shintro.desktour.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.add_comment.*
import kotlinx.android.synthetic.main.fragment_home.*
import shintro.desktour.AddActivity
import shintro.desktour.MainActivity
import shintro.desktour.R


/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        add_floatingactionbutton.setOnClickListener {

                val intent = Intent(activity, AddActivity::class.java)
                startActivity(intent)
            }

        }
    }
