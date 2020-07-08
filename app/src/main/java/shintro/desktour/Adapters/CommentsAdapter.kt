package shintro.desktour.Adapters

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_desk_comment.view.*
import shintaro.desktour_cluod_firestore.R
import shintaro.desktour_cluod_firestore.USER_REF
import shintro.desktour.Model.Comment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentsAdapter(val comments: ArrayList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.detail_desk_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindComment(comments[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val timestamp = itemView?.findViewById<TextView>(R.id.detail_desl_comment_timestamp)
        val commentTxt = itemView?.findViewById<TextView>(R.id.detail_desk_sendComment_textview)


        fun bindComment(comment: Comment) {
            commentTxt?.text = comment.comment

            val dateFormatter = SimpleDateFormat("MM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(comment.commentCreated)
            timestamp?.text = dateString

            val desktourCollectionRef = FirebaseFirestore.getInstance().collection(USER_REF)
                .document(comment.uid)

            desktourCollectionRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")

                        val username = document.data?.get("username")
                        val profileImageUrl = document.data?.get("profileImageUrl")

                        itemView.detail_desl_comment_username.text = username.toString()
                        Picasso.get().load(profileImageUrl.toString()).into(itemView.user_imageview_detail_desk_comment)

                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
        }
    }
}