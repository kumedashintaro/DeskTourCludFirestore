package shintaro.desktour_cluod_firestore



import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.home_fragment.*
import shintro.desktour.Adapters.HomeAdapter


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var homeAdapter: HomeAdapter
    val desktour = arrayListOf<DeskTourDate>()
    val desktourCollectionRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
    lateinit var desktourListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeAdapter = HomeAdapter(desktour){
        }

        recyclerview_desk_homeFragment.adapter = homeAdapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerview_desk_homeFragment.layoutManager = layoutManager

        setListener()
    }


    fun setListener() {

        desktourListener = desktourCollectionRef
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
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

    fun paresData(snapshot: QuerySnapshot) {
        desktour.clear()
        for (document in snapshot.documents) {
            val data = document.data

            if (data?.get(TIMESTAMP) != null) {

                val title = data?.get(TITLE) as String
                val timestamp = data[TIMESTAMP] as Timestamp
                val commentTxt = data[COMMENT_TXT] as? String
                val numLikes = data[NUM_LIKES] as? Long
                var numComments = data[NUM_COMMENTS] as? Long
                val documentId = document.id

                if (numComments == null) numComments = 0

                val newDeskTour = numLikes?.toInt()?.let {
                    DeskTourDate(
                        title,
                        timestamp.toDate(),
                        commentTxt.toString(),
                        it,
                        numComments.toInt(),
                        documentId
                    )
                }

                if (newDeskTour != null) {
                    desktour.add(newDeskTour)
                }

            }

        }
        homeAdapter.notifyDataSetChanged()

    }
}




