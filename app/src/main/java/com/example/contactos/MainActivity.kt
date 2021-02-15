package com.example.contactos

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream



class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE: Int = 101
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var imageView: ImageView
    lateinit var captureButton: ImageButton
    lateinit var bitmap: Bitmap
    lateinit var name: EditText
    lateinit var surname: EditText
    lateinit var phone: EditText
    lateinit var email: EditText
    lateinit var photo: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        name = findViewById(R.id.editTextTextPersonName)
        surname = findViewById(R.id.editTextTextPersonSurname)
        phone = findViewById(R.id.editTextPhone)
        email = findViewById(R.id.editTextTextEmailAddress)
        photo = findViewById(R.id.imageView)

        imageView = findViewById(R.id.imageView)
        captureButton = findViewById(R.id.imageButton)
        captureButton.setOnClickListener(View.OnClickListener {
            if (checkPersmission()) {
                takePicture()
            } else {
                requestPermission()
            }
        })
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    takePicture()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                bitmap = data!!.extras!!.get("data") as Bitmap
                imageView.setImageBitmap(bitmap)
            }

        }
    }


    fun Guardar(view: View) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }

        var fullname = name.text.toString() + " " + surname.text.toString()



        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()


        intent.apply {

            val row = ContentValues().apply {
                putExtra(ContactsContract.CommonDataKinds.Photo.PHOTO, image)
                putExtra(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            }
            val data = arrayListOf(row)
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)

            putExtra(ContactsContract.Intents.Insert.NAME, fullname)


            putExtra(ContactsContract.Intents.Insert.PHONE, phone.text)

            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_HOME
            )

            putExtra(ContactsContract.Intents.Insert.EMAIL, email.text)

            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_HOME
            )


        }
        startActivity(intent)
    }
}