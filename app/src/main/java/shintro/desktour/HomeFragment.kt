package shintro.desktour


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.desk_view.view.*
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        recyclerview_desk_homeFragment.adapter = adapter
        recyclerview_desk_homeFragment.layoutManager = LinearLayoutManager(activity)

        fetchDesk()
    }

    companion object {
        val DESK_KEY = "DESK_KEY"
    }

    private fun fetchDesk() {
        val ref = FirebaseDatabase.getInstance().getReference("/desk")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("MainActivity", it.toString())
                    val desk = it.getValue(Desk::class.java)
                    if (desk != null) {
                        adapter.add(DeskItem(desk))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val deskItem = item as shintro.desktour.DeskItemHome
                    val intent = Intent(view.context, DetailDeskActivity::class.java)
                    intent.putExtra(DESK_KEY, deskItem.desk)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class DeskItemHome(val desk: Desk) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.desk_title.text = desk.titel
        viewHolder.itemView.desk_comment.text = desk.comment

        Picasso.get().load(desk.profileImageUrl).into(viewHolder.itemView.desk_image)

    }

    override fun getLayout(): Int {
        return R.layout.desk_view
    }
}


