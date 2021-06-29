package com.example.instafire


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instafire.models.Post
import com.example.instafire.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.item_post.*

private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 1234
@Suppress("DEPRECATION")
class CreateActivity : AppCompatActivity() {
    private var signedInUser: User? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }

        btnpickimage.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        btnsubmit.setOnClickListener {
            if (photoUri == null) {
                Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tvdescription.text.isBlank()) {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                Log.i(TAG,"HELLO")
                return@setOnClickListener
            }
            if (signedInUser == null) {
                Toast.makeText(this, "No signed in user, please wait", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnsubmit.isEnabled = false
            val photoUploadUri = photoUri as Uri
            val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
            // Upload photo to Firebase Storage
            photoReference.putFile(photoUploadUri)
                .continueWithTask { photoUploadTask ->
                    Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                    // Retrieve image url of the uploaded image
                    photoReference.downloadUrl
                }.continueWithTask { downloadUrlTask ->
                    // Create a post object with the image URL and add that to the posts collection
                    val post = Post(
                        tvdescription.text.toString(),
                        downloadUrlTask.result.toString(),
                        System.currentTimeMillis(),
                        signedInUser)
                    firestoreDb.collection("posts").add(post)
                }.addOnCompleteListener { postCreationTask ->
                    btnsubmit.isEnabled = true
                    if (!postCreationTask.isSuccessful) {
                        Log.e(TAG, "Exception during Firebase operations", postCreationTask.exception)
                        Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                    }
                    tvdescription.text=""
                    imageView.setImageResource(0)
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    val profileIntent = Intent(this, ProfileActivity::class.java)
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(profileIntent)
                    finish()
                }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                Log.i(TAG, "photoUri $photoUri")
                imageView.setImageURI(photoUri)
            } else {
                Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
