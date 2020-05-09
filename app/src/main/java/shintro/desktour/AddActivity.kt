package shintro.desktour

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.selectphoto_imageview
import kotlinx.android.synthetic.main.add_comment.*
import kotlinx.android.synthetic.main.add_deskimage.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val user = FirebaseAuth.getInstance().currentUser

        if(user == null){

            Toast.makeText(this, "投稿するにはログインが必要です。", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        }



    }

    override fun onStart() {
        super.onStart()
        selectophoto_desk_button.setOnClickListener {
            Log.d("AddActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        add_button.setOnClickListener {
            performRegister()
        }

    }

    var selectedPhotUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // procead and check what the selected image was...
            Log.d("AddActivity", "Photo was selected")

            selectedPhotUri = data.data

            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotUri)
            //selectdeskphoto_imageview.setImageBitmap(bitmap)

            Picasso.get().load(selectedPhotUri).into(selectdeskphoto_imageview)

            selectophoto_desk_button.alpha = 0f
        }
    }

    private fun performRegister() {
        val comment = add_comment__edittext.text.toString()

        if (comment.isEmpty() || selectedPhotUri == null) {
            Toast.makeText(this, "写真の選択 又は コメント を入力して下さい ", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("AddActivity", "comment: " + comment)
        Log.d("AddActivity", "selectedPhotUri:" + selectedPhotUri)

        uploadImageToFirebaseStorage()
    }


    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/deskimages/$filename")

        ref.putFile(selectedPhotUri!!)
            .addOnSuccessListener {
                Log.d("AddActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("AddActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener {
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/desk/$uid")

        val desk = Desk(uid, add_comment__edittext.text.toString(), profileImageUrl)

        ref.setValue(desk)
            .addOnSuccessListener {
                Log.d("AddActivity", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }

}

class Desk(val uid: String, val comment: String, val profileImageUrl: String) {
    constructor() : this("", "", "")

}
