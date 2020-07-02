package shintaro.desktour_cluod_firestore

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_comment.*
import kotlinx.android.synthetic.main.add_deskimage.*
import java.util.*

class AddFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectophoto_desk_button.setOnClickListener {

            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                myStorageEnable()
            } else {
                // 許可されていないので許可ダイアログを表示する
                val PERMISSIONS_REQUEST_CODE = 0
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE

                )
                return@setOnClickListener
            }
        }

        add_button.setOnClickListener {
            val user = FirebaseAuth.getInstance().uid
            if (user == null) {
                Toast.makeText(activity, "投稿するにはログインして下さい。", Toast.LENGTH_LONG).show()
            } else {
                val comment = add_comment_edittext.text.toString()
                val title = add_title_edittext.text.toString()
                performRegister(comment, title, selectedPhotoUri)
            }
        }
    }

    private fun myStorageEnable() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("AddFragment", "Photo was selected")

            selectedPhotoUri = data.data

            Picasso.get().load(selectedPhotoUri).into(selectdeskphoto_imageview)

            selectophoto_desk_button.alpha = 0f
        }
    }

    private fun performRegister(comment: String, title: String, selectedPhotoUri: Uri?) {

        if (comment.isEmpty() || title.isEmpty() || selectedPhotoUri == null) {
            Toast.makeText(activity, "写真の選択 又は 入力漏れがあります。", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("AddFragment", "title: " + title)
        Log.d("AddFragment", "comment: " + comment)
        Log.d("AddFragment", "selectedPhotUri:" + selectedPhotoUri)

        uploadImageToFirebaseStorage(comment, title, selectedPhotoUri)
    }

    private fun uploadImageToFirebaseStorage(comment: String, title: String, selectedPhotoUri: Uri) {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/deskimages/$filename")

        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {
                Log.d("AddFragment", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("AddFragment", "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString(),comment, title)
                }
            }
            .addOnFailureListener {
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(deskImageUri: String, comment: String, title: String) {
        val userid = FirebaseAuth.getInstance().uid
        if (userid != null) {

            val data = HashMap<String, Any>()
            data.put(NUM_COMMENTS, 0)
            data.put(NUM_LIKES, 0)
            data.put(TITLE, title)
            data.put(COMMENT_TXT, comment)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERID, userid)
            data.put(DESKIMAGEURI, deskImageUri)
            //data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())


            FirebaseFirestore.getInstance().collection(DESKTOUR_REF)
                .add(data)
                .addOnSuccessListener {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    Log.e("Exception", "Could not add post: $exception")
                }
        }
    }
}