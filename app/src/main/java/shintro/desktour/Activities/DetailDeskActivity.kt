package shintaro.desktour_cluod_firestore

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_desk.*
import kotlinx.android.synthetic.main.derail_desk_user.*
import kotlinx.android.synthetic.main.detail_desk_image_comment.*
import shintro.desktour.Activities.UpdateCommentActivity
import shintro.desktour.Adapters.CommentsAdapter
import shintro.desktour.Model.Comment
import java.util.HashMap


class DetailDeskActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    lateinit var deskTourDocumentId : String
    val comments = arrayListOf<Comment>()
    lateinit var desktourListener: ListenerRegistration

    lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_desk)

        deskTourDocumentId = intent.getStringExtra(DOCUMENT_KEY)

        commentsAdapter = CommentsAdapter(comments)
        recyclerview_detail_desk.adapter = commentsAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerview_detail_desk.layoutManager = layoutManager

        profileset()
        commentset()
        listenForMessages(deskTourDocumentId)

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
                    val DeskImage = document.data?.get("deskImageUri")
                    val DeskTitel = document.data?.get("title")

                    detail_desk_comment_textview.text = ComentText.toString()
                    detail_desk_title_textview.text = DeskTitel.toString()
                    Picasso.get().load(DeskImage.toString()).into(detail_desk_imageview)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
    }

        private fun listenForMessages(deskTourDocumentId: String) {

            val desktourCommentRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDocumentId).collection(
                COMMENTS_REF)

            desktourListener = desktourCommentRef
                .orderBy(COMMENT_CREATED, Query.Direction.ASCENDING)
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
                    if (data?.get(COMMENT_CREATED) != null) {
                        val comment = data?.get(COMMENT_TXT) as String
                        val commentCreated = data?.get(COMMENT_CREATED) as Timestamp
                        val userUid = data[USERID] as String
                        val documentId = document.id

                        val newComments = Comment(comment, commentCreated.toDate(), userUid, documentId)
                        comments.add(newComments)

                    }

                    commentsAdapter.notifyDataSetChanged()
                }
                recyclerview_detail_desk.scrollToPosition(adapter.itemCount - 1)
            }

    private fun saveCommentToFirebaseDatabase() {

        val userid = FirebaseAuth.getInstance().uid
        val sendcomment = send_comment.text.toString()
        val deskTourRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDocumentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val thought = transaction.get(deskTourRef)
            val numComments = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(deskTourRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
                .document(deskTourDocumentId).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data.put(USERID, userid.toString())
            data.put(COMMENT, sendcomment)
            data.put(COMMENT_CREATED, FieldValue.serverTimestamp())

            transaction.set(newCommentRef, data)
        }
            .addOnSuccessListener {

                Toast.makeText(this, "コメントしました ", Toast.LENGTH_LONG).show()
                send_comment.text.clear()
                recyclerview_detail_desk.scrollToPosition(adapter.itemCount -1)

            }
            .addOnFailureListener {exception ->
                Log.e("Exception", "Could not add comment $exception")
                Toast.makeText(this, "コメントに失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
            }
   }

    override fun optionMenuClicked(comment: Comment) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.options_menu,null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        builder.setView(dialogView)
            .setNegativeButton("Cancel"){ _, _ ->}
        val ad = builder.show()

        deleteBtn.setOnClickListener {
            val commentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
                .collection(COMMENTS_REF).document(comment.documentId)
            val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)


            FirebaseFirestore.getInstance().runTransaction { transaction ->

                val thought = transaction.get(thoughtRef)
                val numComments = thought.getLong(NUM_COMMENTS)?.minus(1)
                transaction.update(thoughtRef, NUM_COMMENTS, numComments)

                transaction.delete(commentRef)
            }.addOnSuccessListener{
                ad.dismiss()
            }.addOnFailureListener{exception ->
                Log.e("Exception", "Could not add comment ${exception.localizedMessage}")

            }
        }
        editBtn.setOnClickListener {
            val updateIntent = Intent(this, UpdateCommentActivity::class.java)
            updateIntent.putExtra(THOUGHT_DOC_ID_EXTRA, thoughtDocumentId)
            updateIntent.putExtra(COMMENT_DOC_ID_EXTRA, comment.documentId)
            updateIntent.putExtra(COMMENT_TXT_EXTRA, comment.commentTxt)
            ad.dismiss()
            startActivity(updateIntent)
        }
    }
}

