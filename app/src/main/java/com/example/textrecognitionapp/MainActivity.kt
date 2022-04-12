package com.example.textrecognitionapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    lateinit var txtResult: TextView
    lateinit var btnPic:Button
    var intentActivityLauncher: ActivityResultLauncher<Intent>?=null
    lateinit var image: InputImage
    lateinit var textRecognizer: TextRecognizer
    private val CAMERA_PERMISSION_CODE=123
    private val STORAGE_PERMISSION_CODE=113
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtResult=findViewById(R.id.result)
        btnPic=findViewById(R.id.take_pic)
        textRecognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        intentActivityLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),{
                val data=it.data
                val imageUrl=data?.data
                convertImgToText(imageUrl)
            }
        )
        btnPic.setOnClickListener {
            //checkPermission(android.Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE)

            val chooseIntent=Intent()
            chooseIntent.type="image/*"
            chooseIntent.action=Intent.ACTION_GET_CONTENT
            intentActivityLauncher?.launch(chooseIntent)
        }
    }

    private fun convertImgToText(imageUrl: Uri?) {

        try {
            image = imageUrl?.let { InputImage.fromFilePath(applicationContext, it) }!!
            //get text from image

            val result :Task<Text> =textRecognizer.process(image)
                .addOnSuccessListener {
                    txtResult.text=it.text
                }
                .addOnFailureListener {
                    txtResult.text="Error: ${it.message}"
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)
    }

    private fun checkPermission(permission:String,requestCode:Int){
        if(ContextCompat.checkSelfPermission(this@MainActivity ,permission)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@MainActivity , arrayOf(permission),requestCode)
        }
        else{
            Toast.makeText(this@MainActivity ,"permission already is garented ",Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MainActivity ,"permission already is garented ",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this@MainActivity ,"permission already is denied ",Toast.LENGTH_LONG).show()
            }
        }
        else if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MainActivity ,"storage already is garented ",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this@MainActivity ,"storage already is denied ",Toast.LENGTH_LONG).show()
            }
        }
    }
}