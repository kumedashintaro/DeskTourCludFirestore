package shintro.desktour.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import shintaro.desktour_cluod_firestore.DeskTourDate
import shintaro.desktour_cluod_firestore.NUM_LIKES
import shintaro.desktour_cluod_firestore.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class HomeAdapter(val deskTour: ArrayList<DeskTourDate>, val itemClick: (DeskTourDate) -> Unit) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.desk_view, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return deskTour.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(deskTour[position])
    }

    inner class ViewHolder(itemView: View?, val itemClick: (DeskTourDate) -> Unit) : RecyclerView.ViewHolder(itemView!!) {

        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewThoughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikesLb)
        val likesImages = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)
        val numComments = itemView?.findViewById<TextView>(R.id.numCommentsLbl)

        fun bindThought(thought: Thought) {

            username?.text = thought.username
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            itemView.setOnClickListener { itemClick(thought) }
            numComments?.text = thought.NumComments.toString()

            val dateFormatter = SimpleDateFormat("MM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(thought.timestamp)
            timestamp?.text = dateString

            likesImages?.setOnClickListener{
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes +1)
            }
        }
    }
}