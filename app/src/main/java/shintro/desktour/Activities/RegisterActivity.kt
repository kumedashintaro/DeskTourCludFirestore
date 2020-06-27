package shintaro.desktour_cluod_firestore

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        selectophoto_button.setOnClickListener {
            checkPermission()
        }

        login_button_register.setOnClickListener {
            performRegister()
        }
    }

    var selectedPhotUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // procead and check what the selected image was...
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotUri)

            selectphoto_imageview.setImageBitmap(bitmap)

            selectophoto_button.alpha = 0f
        }
    }


    private fun performRegister() {
        val email = email_edittext.text.toString()
        val password = paswward_edittext.text.toString()
        val username = username_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "E-mail 又は Password を入力して下さい ", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")


//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//
//                Log.d(
//                    "RegisterActivity",
//                    "Successfully created user with uid:${it.result?.user?.uid}"
//                )
//
//                uploadImageToFirebaseStorage()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "登録に失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
//                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
//            }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                result.user?.updateProfile(changeRequest)?.addOnFailureListener { exception ->
                    Log.e(
                        "Exception:",
                        "Could not update display name: ${exception.localizedMessage} "
                    )
                }

                val data = HashMap<String, Any>()
                data.put(USERNAME, username)
                data.put(DATE_CREATED, FieldValue.serverTimestamp())

                result.user?.uid?.let {
                    FirebaseFirestore.getInstance().collection(USER_REF).document(it)
                        .set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "登録しました ", Toast.LENGTH_LONG).show()

                            uploadImageToFirebaseStorage()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "登録に失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                            // Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                            Log.e(
                                "Exception:",
                                "Could not user document: ${exception.localizedMessage} "
                            )
                        }
                }
            }

            }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener {
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }


    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            myStorageEnable()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //許可を求め、拒否されていた場合
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            //まだ許可を求めていない
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された
                    myStorageEnable()
                } else {
                    Toast.makeText(this, "画像を選択できません", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun myStorageEnable() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

}



