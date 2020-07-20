package shintro.desktour.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.desk_view.view.*
import shintaro.desktour_cluod_firestore.DESKTOUR_REF
import shintaro.desktour_cluod_firestore.DeskTourDate
import shintaro.desktour_cluod_firestore.NUM_LIKES
import shintaro.desktour_cluod_firestore.R
import shintro.desktour.Interface.HomeOptionsClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class HomeAdapter(val deskTour: ArrayList<DeskTourDate>, val homeOptionsListener: HomeOptionsClickListener, val itemClick: (DeskTourDate) -> Unit) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.desk_view, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return deskTour.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThought(deskTour[position])
    }

    inner class ViewHolder(itemView: View?, val itemClick: (DeskTourDate) -> Unit) : RecyclerView.ViewHolder(itemView!!) {

        val title = itemView?.findViewById<TextView>(R.id.desk_title)
        val timestamp = itemView?.findViewById<TextView>(R.id.desk_timstamp)
        val comment = itemView?.findViewById<TextView>(R.id.desk_comment)
        val numLikes = itemView?.findViewById<TextView>(R.id.desk_numLikes)
        val likesImages = itemView?.findViewById<ImageView>(R.id.desk_ikesImage)
        val numComments = itemView?.findViewById<TextView>(R.id.desk_numCommentsLbl)
        val deskImageUri = itemView?.findViewById<ImageView>(R.id.desk_image)
        val homeoptionsImage = itemView?.findViewById<ImageView>(R.id.homeOptionsImage)

        fun bindThought(deskTourDate: DeskTourDate) {

            title?.text = deskTourDate.title
            comment?.text = deskTourDate.comment
            numLikes?.text = deskTourDate.numLikes.toString()
            itemView.setOnClickListener { itemClick(deskTourDate) }
            numComments?.text = deskTourDate.NumComments.toString()

            Picasso.get().load(deskTourDate.deskImageUri).into(itemView.desk_image)


            val dateFormatter = SimpleDateFormat("MM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(deskTourDate.timestamp)
            timestamp?.text = dateString

            likesImages?.setOnClickListener{
                FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDate.documentId)
                    .update(NUM_LIKES, deskTourDate.numLikes +1)
            }
            if(FirebaseAuth.getInstance().currentUser?.uid == deskTourDate.uid){
                homeoptionsImage?.visibility = View.VISIBLE
                homeoptionsImage?.setOnClickListener {
                    homeOptionsListener.homeOptionsMenunClicked(deskTourDate)

                }
            }
        }
    }
}