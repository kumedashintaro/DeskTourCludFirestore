package shintaro.desktour_cluod_firestore



import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.home_fragment.*
import shintro.desktour.Adapters.HomeAdapter
import shintro.desktour.Interface.HomeOptionsClickListener
import kotlin.concurrent.thread


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), HomeOptionsClickListener {

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

        homeAdapter = HomeAdapter(desktour,this){desktour ->
            val detailDeskActivity = Intent(activity, DetailDeskActivity::class.java)
            detailDeskActivity.putExtra(DOCUMENT_KEY, desktour.documentId)
            startActivity(detailDeskActivity)
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
                var desukImageUri = data[DESKIMAGEURI] as? String
                val documentId = document.id
                val userUid = data[USERID] as? String

                if (numComments == null) numComments = 0

                val newDeskTour = numLikes?.toInt()?.let {
                    DeskTourDate(
                        title,
                        timestamp.toDate(),
                        commentTxt.toString(),
                        it,
                        numComments.toInt(),
                        desukImageUri.toString(),
                        documentId,
                        userUid.toString()
                    )
                }

                if (newDeskTour != null) {
                    desktour.add(newDeskTour)
                }
            }
        }
        homeAdapter.notifyDataSetChanged()
    }

    override fun homeOptionsMenunClicked(deskTourDate: DeskTourDate) {
        val builder = activity?.let { AlertDialog.Builder(it) }
        val dialogView = layoutInflater.inflate(R.layout.options_menu,null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        if (builder != null) {
            builder.setView(dialogView)
                .setNegativeButton("Cancel"){ _, _ ->}
        }
        val ad = builder?.show()

        deleteBtn.setOnClickListener {
            val thoughtRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
                .document(deskTourDate.documentId)
            val collectionRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
                .document(deskTourDate.documentId)
                .collection(COMMENTS_REF)

            deleteCollection(collectionRef, deskTourDate) { success ->
                if (success) {
                    thoughtRef.delete()
                        .addOnSuccessListener {
                            if (ad != null) {
                                ad.dismiss()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Exception", "Could not delete though: $exception")
                        }
                }
            }
        }

        editBtn.setOnClickListener {

        }

    }

    fun deleteCollection(collection: CollectionReference, deskTourDate: DeskTourDate, complete: (Boolean) -> Unit) {
        collection.get().addOnSuccessListener { snapshot ->
            thread{
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in snapshot) {
                    val docRef = FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDate.documentId)
                        .collection(COMMENTS_REF).document(document.id)
                    batch.delete(docRef)
                }
                batch.commit()
                    .addOnSuccessListener{
                        complete(true)
                    }
                    .addOnFailureListener{exception ->
                        Log.e("Exception", "Could not delete subcollection: $exception")
                    }
            }
        }.addOnFailureListener { exception ->
            Log.e("Exception", "Could not retrieve documents: $exception")
        }
    }

}




