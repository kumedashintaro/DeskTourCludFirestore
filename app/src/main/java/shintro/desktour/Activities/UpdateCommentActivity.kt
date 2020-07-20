package shintro.desktour.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_comment.*
import shintaro.desktour_cluod_firestore.*

class UpdateCommentActivity : AppCompatActivity() {

    lateinit var deskTourDocId: String
    lateinit var commentDocId: String
    lateinit var commentTxt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_comment)

        deskTourDocId = intent.getStringExtra(DESKTOUR_DOC_ID_EXTRA)
        commentDocId = intent.getStringExtra(COMMENT_DOC_ID_EXTRA)
        commentTxt = intent.getStringExtra(COMMENT_TXT_EXTRA)

        updateCommentTxt.setText(commentTxt)

    }

    fun updateCommentClicked(view: View) {
        FirebaseFirestore.getInstance().collection(DESKTOUR_REF).document(deskTourDocId)
            .collection(COMMENTS_REF).document(commentDocId)
            .update(COMMENT_TXT, updateCommentTxt.text.toString())
            .addOnSuccessListener{
                finish()
            }
            .addOnFailureListener {exception ->
                Log.e("Exception", "could not update comment: ${exception.localizedMessage}")
            }

    }

}
