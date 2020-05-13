package shintro.desktour

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_comment.*
import kotlinx.android.synthetic.main.add_deskimage.*
import java.util.*

class AddActivity : AppCompatActivity() {
    private lateinit var youtubeBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private lateinit var parsonBtn: ImageButton
    private lateinit var addBtn: ImageButton

    companion object {
        private val PERMISSIONS_REQUEST_CODE = 100
    }

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        bottomselct()

        selectophoto_desk_button.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // 許可されている
                    myStorageEnable()
                } else {
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_CODE
                    )
                    return@setOnClickListener
                }
            } else {
                myStorageEnable()
            }
        }

        add_button.setOnClickListener {
            performRegister()

        }
    }

    private fun myStorageEnable(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
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
        val comment = add_comment_edittext.text.toString()
        val title = add_title_edittext.text.toString()

        if (comment.isEmpty() || title.isEmpty() || selectedPhotUri == null) {
            Toast.makeText(this, "写真の選択 又は 入力漏れがあります。", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("AddActivity", "title: " + title)
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
        val deskuid = UUID.randomUUID().toString()


        val uid = FirebaseAuth.getInstance().uid ?: ""
       // val ref = FirebaseDatabase.getInstance().getReference("/desk/$uid")

        val ref = FirebaseDatabase.getInstance().getReference("/desk").push()

        val desk = Desk(uid,add_title_edittext.text.toString(),add_comment_edittext.text.toString(), profileImageUrl,deskuid)

        ref.setValue(desk)
            .addOnSuccessListener {
                Log.d("AddActivity", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }


    private fun bottomselct(){

        // init image buttons
//        youtubeBtn = findViewById(R.id.youtubeBtn)
        homeBtn = findViewById(R.id.homeBtn)
        addBtn = findViewById(R.id.addBtn)
        parsonBtn = findViewById(R.id.parsonBtn)

        //onclick listner
//        youtubeBtn.setOnClickListener {

//        }

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addBtn.setOnClickListener {
            if(user == null){
                Toast.makeText(this, "投稿するにはログインが必要です。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
        }

        parsonBtn.setOnClickListener {
            if(user == null){
                val intent = Intent(this, NotLoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
