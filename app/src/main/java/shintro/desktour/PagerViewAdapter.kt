package shintro.desktour

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import shintro.desktour.Fragments.*

internal class PagerViewAdapter(fm: FragmentManager?): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment?{

        val user = FirebaseAuth.getInstance().currentUser

        return when(position){
            0 -> {
                YouTube()
            }
            1 -> {
                Home()
            }
            2 -> {
                Add()
            }
            3 -> {
                if (user == null) { Parson()
                }else{
                    Login()
                }
            }

            else -> null
        }
    }

    override fun getCount(): Int {

        return 4
    }
}