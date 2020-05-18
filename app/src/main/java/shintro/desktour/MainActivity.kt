package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
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

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSelect()

        val adapter = GroupAdapter<ViewHolder>()

        //adapter.add(DeskItem())

        recyclerview_desk.adapter = adapter
        recyclerview_desk.layoutManager = LinearLayoutManager(this)

        fetchDesk()
    }


    companion object{
        val DESK_KEY = "DESK_KEY"
    }

    private fun fetchDesk(){
        val ref = FirebaseDatabase.getInstance().getReference("/desk")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {


                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("MainActivity", it.toString())
                    val desk = it.getValue(Desk::class.java)
                    if (desk != null){
                        adapter.add(DeskItem(desk))
                    }
                }

                adapter.setOnItemClickListener{item, view ->


                    val deskItem = item as DeskItem

                    val intent = Intent(view.context,DetailDeskActivity::class.java)
 //                  intent.putExtra(DESK_KEY, deskItem.desk.uid)
                    intent.putExtra(DESK_KEY,deskItem.desk)


                    startActivity(intent)
                }

                recyclerview_desk.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    private fun bottomSelect(){

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if(user == null){
                Toast.makeText(this, "投稿するにはログインが必要です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
        }

        parsonBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
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

class DeskItem(val desk: Desk): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.desk_title.text = desk.titel
        viewHolder.itemView.desk_comment.text = desk.comment

        Picasso.get().load(desk.profileImageUrl).into(viewHolder.itemView.desk_image)

    }

    override fun getLayout(): Int {
        return R.layout.desk_view
    }
}

