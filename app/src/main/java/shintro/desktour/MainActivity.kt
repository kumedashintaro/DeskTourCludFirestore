package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var youtubeBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private lateinit var parsonBtn: ImageButton
    private lateinit var addBtn: ImageButton

    private lateinit var mViewPager: ViewPager
    private lateinit var mPagerViewAdapter: PagerViewAdapter

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init views
        mViewPager = findViewById(R.id.mViewPager)

        // init image buttons
        youtubeBtn = findViewById(R.id.youtubeBtn)
        homeBtn = findViewById(R.id.homeBtn)
        addBtn = findViewById(R.id.addBtn)
        parsonBtn = findViewById(R.id.parsonBtn)

        //onclick listner


        youtubeBtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        homeBtn.setOnClickListener {
            mViewPager.currentItem = 1
        }

        addBtn.setOnClickListener {
            mViewPager.currentItem = 2
        }

        parsonBtn.setOnClickListener {
            mViewPager.currentItem = 3
        }







        mPagerViewAdapter = PagerViewAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 4

        // add page change listener
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        mViewPager.currentItem = 1
        homeBtn.setImageResource(R.drawable.ic_home_palevioletred)

    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            youtubeBtn.setImageResource(R.drawable.ic_slideshow_palevioletred)
            homeBtn.setImageResource(R.drawable.ic_home_black)
            addBtn.setImageResource(R.drawable.ic_add_box_black)
            parsonBtn.setImageResource(R.drawable.ic_person_outline_black)

        }
        if (position == 1) {
            youtubeBtn.setImageResource(R.drawable.ic_slideshow_black)
            homeBtn.setImageResource(R.drawable.ic_home_palevioletred)
            addBtn.setImageResource(R.drawable.ic_add_box_black)
            parsonBtn.setImageResource(R.drawable.ic_person_outline_black)

        }
        if (position == 2) {
            youtubeBtn.setImageResource(R.drawable.ic_slideshow_black)
            homeBtn.setImageResource(R.drawable.ic_home_black)
            addBtn.setImageResource(R.drawable.ic_add_box_palevioletred)
            parsonBtn.setImageResource(R.drawable.ic_person_outline_black)

        }
        if (position == 3) {
            youtubeBtn.setImageResource(R.drawable.ic_slideshow_black)
            homeBtn.setImageResource(R.drawable.ic_home_black)
            addBtn.setImageResource(R.drawable.ic_add_box_black)
            parsonBtn.setImageResource(R.drawable.ic_person_outline_palevioletred)
        }
    }
}

