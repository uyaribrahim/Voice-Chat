package com.ibrhmuyar.voicechat.view

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ibrhmuyar.voicechat.R
import com.ibrhmuyar.voicechat.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_profile_picture.*

class ProfilePicture : AppCompatActivity() {

    private lateinit var authviewmodel: AuthViewModel
    private var storeReference: StorageReference = FirebaseStorage.getInstance().getReference()
    private var imageUri: Uri? = null
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        authviewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)


        addProfileImage.setOnClickListener {

            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent, 2)
        }

        btnDone.setOnClickListener {
            if (imageUri != null) {
                uploadingProgressBar.visibility = View.VISIBLE
                btnDone.isEnabled = false
                uploadToFirebase(imageUri!!)
            }
        }

    }

    fun uploadToFirebase(uri: Uri) {
        var fileRef: StorageReference = storeReference.child("images")
            .child(auth.uid.toString())
            .child(System.currentTimeMillis().toString() + "." + getFileExtension(uri))
        fileRef.putFile(uri).addOnSuccessListener(OnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                saveDataToFirebase()
                uploadingProgressBar.visibility = View.GONE
            }
        })
            .addOnFailureListener {
                Toast.makeText(this, "Resim ekleme işlemi başarısız", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirebase() {

        authviewmodel.updateProfilePictures(imageUrl)
        Toast.makeText(this, "Profil resmi eklendi", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
        finish()

    }

    fun getFileExtension(mUri: Uri): String? {

        var cr: ContentResolver
        cr = this.getContentResolver()
        var mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == -1 && data != null) {
            imageUri = data.data!!
            imageView.visibility = View.GONE
            profileImage.visibility = View.VISIBLE
            profileImage.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}