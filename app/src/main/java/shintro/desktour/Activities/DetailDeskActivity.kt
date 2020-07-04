package shintaro.desktour_cluod_firestore

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_desk.*
import kotlinx.android.synthetic.main.derail_desk_user.*
import kotlinx.android.synthetic.main.detail_desk_comment.*
import kotlinx.android.synthetic.main.detail_desk_comment.view.*
import kotlinx.android.synthetic.main.detail_desk_image_comment.*
import kotlinx.android.synthetic.main.detail_desk_image_comment.view.*
import shintro.desktour.Model.Comment
import java.util.HashMap


class DetailDeskActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    lateinit var deskTourDocumentId : String

    var toDesk: Desk? = null

    val comments = arrayListOf<Comment>()

    val desktourCommentRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDocumentId).collection(
        COMMENTS_REF)
    lateinit var desktourListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_desk)

        recyclerview_detail_desk.adapter = adapter

        deskTourDocumentId = intent.getStringExtra(DOCUMENT_KEY)

        Log.d("DetailDeskActivity", "key: " + deskTourDocumentId)

        profileset()
        commentset()
        listenForMessages()

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

        val userid = FirebaseAuth.getInstance().uid
        val desktourCollectionRef = FirebaseFirestore.getInstance().collection(USER_REF)
            .document(userid.toString()
            )

        desktourCollectionRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val username = document.data?.get("username")
                    val profileImageUrl = document.data?.get("profileImageUrl")

                    detail_desk_username.text = username.toString()
                    Picasso.get().load(profileImageUrl.toString()).into(user_imageview_detail_desk)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
    }

    private fun commentset() {

        val desktourCommentsCollectionRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
            .document(deskTourDocumentId)

        desktourCommentsCollectionRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")

                    val ComentText = document.data?.get("comment")
                    val DeskImage = document.data?.get("deskImageUrl")
                    val DeskTitel = document.data?.get("title")

                    detail_desk_comment_textview.text = ComentText.toString()
                    detail_desk_title_textview.text = DeskTitel.toString()
                    Picasso.get().load(DeskImage.toString()).into(detail_desk_imageview)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
    }




        private fun listenForMessages() {


            desktourListener = desktourCommentRef
                .orderBy(COMMENT_CREATED, Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        Log.e("Exception", "Could not retrieve documents: $exception")
                    }

                    if (snapshot != null) {
                        Log.e("Exception", "retrieve documents: $exception")
                        paresData(snapshot)
                    }
                }
        }

            private fun paresData(snapshot: QuerySnapshot) {


                comments.clear()
                for (document in snapshot.documents) {
                    val data = document.data
                    val comment = data?.get(COMMENT_TXT) as String
                    val commentCreated = data?.get(COMMENT_CREATED) as Timestamp
                    val userUid = data[USERID] as String
                    val documentId = document.id


                    val newComments = Comment(
                        comment,
                        commentCreated.toDate(),
                        userUid,
                        documentId
                    )



                        adapter.add(DetailDeskItem(newComments))

                        recyclerview_detail_desk.adapter = adapter
                        recyclerview_detail_desk.scrollToPosition(adapter.itemCount - 1)

                }

              //  val chatMessage = getValue(DetailDesk::class.java) ?: return



            }





//            val DeskUid = toDesk?.deskuid
//
//            val ref = FirebaseDatabase.getInstance().getReference("/deskpost/$DeskUid")
//
//            ref.addChildEventListener(object: ChildEventListener {
//
//                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//
//                    val chatMessage = p0.getValue(DetailDesk::class.java) ?: return
//
//                    if(chatMessage != null) {
//
//                        adapter.add(DetailDeskItem(chatMessage))
//                    }
//                    recyclerview_detail_desk.adapter = adapter
//                    recyclerview_detail_desk.scrollToPosition(adapter.itemCount -1)
//                }
//
//                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                }
//
//                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//                }
//                override fun onChildRemoved(p0: DataSnapshot) {
//
//                }
//                override fun onCancelled(p0: DatabaseError) {
//                }
//            })
//        }








    private fun saveCommentToFirebaseDatabase() {

        val userid = FirebaseAuth.getInstance().uid
        val sendcomment = send_comment.text.toString()

        val data = HashMap<String, Any>()
        data.put(USERID, userid.toString())
        data.put(COMMENT, sendcomment)
        data.put(COMMENT_CREATED, FieldValue.serverTimestamp())

        if (userid != null) {
            FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDocumentId).
                collection(COMMENTS_REF)
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "コメントしました ", Toast.LENGTH_LONG).show()

                    send_comment.text.clear()
                    Log.d("DetailDeskActivity", "Finally we saved comment to Firebase Database")
                    recyclerview_detail_desk.scrollToPosition(adapter.itemCount -1)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "コメントに失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                    // Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                    Log.e(
                        "Exception:",
                        "Could not user document: ${exception.localizedMessage} "
                    )
                }
        }
    }
}


class DetailDeskItem(val detaildesk: Comment): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.detail_desk_comment_textview.text = detaildesk.comment
       // viewHolder.itemView.detail_desl_comment_username.text = detaildesk.username
        //Picasso.get().load(detaildesk.profileImageUrl).into(viewHolder.itemView.user_imageview_detail_desk_comment)

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