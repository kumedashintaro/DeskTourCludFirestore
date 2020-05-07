package shintro.desktour

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        login_button_register.setOnClickListener {
            performRegister()
        }

        selectophoto_button.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // procead and check what the selected image was...
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotUri)

            selectphoto_imageview.setImageBitmap(bitmap)

            selectophoto_button.alpha = 0f
        }
    }


    private fun performRegister(){
        val email = email_edittext.text.toString()
        val password = paswward_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-mail 又は Password を入力して下さい ", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity", "Password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful)return@addOnCompleteListener

                Log.d("RegisterActivity", "Successfully created user with uid:${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Toast.makeText(this, "登録に失敗しました、もう一度入力して下さい ", Toast.LENGTH_LONG).show()
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
            }

    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotUri!!)
            .addOnSuccessListener{
                Log.d("RegisterActivity","Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener{
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("RegisterActivity","Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }

}

class User(val uid: String, val username: String, val profileImageUrl: String){
   constructor() : this("","","")
}
