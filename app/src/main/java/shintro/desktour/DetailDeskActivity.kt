package shintro.desktour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_desk.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.derail_desk_user.*
import kotlinx.android.synthetic.main.desk_view.view.*
import kotlinx.android.synthetic.main.detail_desk_comment.view.*
import kotlinx.android.synthetic.main.detail_desk_image_comment.*


class DetailDeskActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var toDesk: Desk? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_desk)

        toDesk = intent.getParcelableExtra<Desk>(MainActivity.DESK_KEY)
        Log.d("DetailDeskActivity", "key: " + toDesk)

        profileset()
        commentset()


        listenForMessages()


        val adapter = GroupAdapter<ViewHolder>()
        recyclerview_detail_desk.adapter = adapter
        recyclerview_detail_desk.layoutManager = LinearLayoutManager(this)
        fetchDesk()

        send_button.setOnClickListener {
            val user = FirebaseAuth.getInstance().uid
            if(user == null){
                Toast.makeText(this, "コメントするにはログインが必要です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveCommentToFirebaseDatabase()
        }
    }

    private fun profileset() {

        val userUid = toDesk?.uid
        Log.d("DetailDeskActivity", "uid: " + userUid)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userUid")
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

    private fun commentset() {
        val DeskComment = toDesk?.comment
        val DeskImage = toDesk?.profileImageUrl
        Log.d("DetailActivity", "comment: " + DeskComment)

        detail_desk_comment_textview.setText(DeskComment)
        Picasso.get().load(DeskImage).into(detail_desk_imageview)

    }

    private fun fetchDesk(){
        val DeskUid = toDesk?.deskuid
        val ref = FirebaseDatabase.getInstance().getReference("/deskpost/$DeskUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    Log.d("MainActivity", it.toString())
                    val detaildesk = it.getValue(DetailDesk::class.java)
                    if (detaildesk != null){
                        adapter.add(DetailDeskItem(detaildesk))
                    }
                }
                recyclerview_detail_desk.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }



    val latestMessageMap = HashMap<String, DetailDesk>()

        private fun listenForMessages(){
            val DeskUid = toDesk?.deskuid
            val ref = FirebaseDatabase.getInstance().getReference("/deskpost/$DeskUid")
            ref.addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(DetailDesk::class.java) ?: return
                    latestMessageMap[p0.key!!] = chatMessage
                    //refreshRecyclerViewMessages()

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }
                override fun onChildRemoved(p0: DataSnapshot) {

                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }









    private fun saveCommentToFirebaseDatabase() {

        val user = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val sendcomment = send_comment.text.toString()

                if (sendcomment.isEmpty()) {
                    return
                }


                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(User::class.java)

                val username = post?.username
                val profileImageUrl = post?.profileImageUrl

                val DeskUid = toDesk?.deskuid
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val refDesk =
                    FirebaseDatabase.getInstance().getReference("/deskpost/$DeskUid").push()

                val detaildesk = DetailDesk(uid, sendcomment, username!!, profileImageUrl!!)

                refDesk.setValue(detaildesk)
                    .addOnSuccessListener {
                        send_comment.text.clear()
                        Log.d("DetailDeskActivity", "Finally we saved comment to Firebase Database")
                        fetchDesk()
                        recyclerview_detail_desk.scrollToPosition(adapter.itemCount -1)
                    }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }
}

class DetailDeskItem(val detaildesk: DetailDesk): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.detail_desk_comment_textview.text = detaildesk.deskdetailcommnt
        viewHolder.itemView.detail_desl_comment_username.text = detaildesk.username
        Picasso.get().load(detaildesk.profileImageUrl).into(viewHolder.itemView.user_imageview_detail_desk_comment)

    }

    override fun getLayout(): Int {
        return R.layout.detail_desk_comment
    }
}

class DetailDesk(
    val uid: String,
    val deskdetailcommnt: String,
    val username: String,
    val profileImageUrl: String
) {
    constructor() : this("", "", "", "")
}