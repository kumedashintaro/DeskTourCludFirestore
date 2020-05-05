package shintro.desktour

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import shintro.desktour.Fragments.Add
import shintro.desktour.Fragments.Home
import shintro.desktour.Fragments.Parson
import shintro.desktour.Fragments.YouTube

internal class PagerViewAdapter(fm: FragmentManager?): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment?{


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


                    Parson()

            }

            else -> null
        }
    }

    override fun getCount(): Int {

        return 4
    }
}