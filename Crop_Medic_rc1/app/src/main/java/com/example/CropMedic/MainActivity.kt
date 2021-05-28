package com.example.CropMedic


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import com.example.CropMedic.CameraUsage.CameraActivity
import com.example.CropMedic.FileChooserActivity.FileChooserActivity
import com.example.CropMedic.Utils.ActivityUtils
import com.example.CropMedic.Utils.AppConstants
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    private lateinit var cameraOpenButton : ImageButton
    private lateinit var filechooserOpenButton : ImageButton

       val TAG = "MainActivity"


    override fun onCreate(savedInstanceState : Bundle?) {
       try{
           //The code to hide the action bar
           ActivityUtils.hideActionBar(this)
           //Set the layout
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_main)

           Firebase.initialize(applicationContext)



           CameraActivityTrigger.setOnClickListener{}

           //Activity Results

           //Result launcher for file chooser
            val fileActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                   res->
               if(res.resultCode == Activity.RESULT_OK){
                   val s = res?.data?.getStringExtra(AppConstants.INTENT_CALL)
                   callResult(s)
               }
           }

           //Result Launcher to open camera
           val cameraActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                   res->
               if(res.resultCode == Activity.RESULT_OK){
                   val s = res?.data?.getStringExtra(AppConstants.INTENT_CALL)
                   callResult(s)
               }
           }

           //Assign the layout elements to objects
           cameraOpenButton = findViewById(R.id.CameraActivityTrigger) // Button for camera opening
           filechooserOpenButton =
               findViewById(R.id.FileChooserActivityTrigger) //Button for file chooser opening

           cameraOpenButton.setOnClickListener {
               cameraActivityResultLauncher.launch(Intent(this.applicationContext,CameraActivity::class.java))
           }
           filechooserOpenButton.setOnClickListener {
               val intent= Intent(this.applicationContext , FileChooserActivity::class.java)
               intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
               intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

               fileActivityResultLauncher.launch(intent)
           }


           //Menu buttons stuff
           howdoesitworkbutton.setOnClickListener {
               startActivity(Intent(applicationContext,How_does_it_work_activity::class.java))
           }

           mainactivityhistory.setOnClickListener {
               startActivity(Intent(applicationContext,HistoryActivity::class.java))
           }

           faqbutton.setOnClickListener {
               startActivity(Intent(this,FAQActivity::class.java))
           }
       }catch (e:Exception){
           Log.e(TAG,e.toString())
           ActivityUtils.triggerErrorActivity(this)
       }
    }


    //When the URI is got from any source this function will then open the processing/result activity
    private fun callResult(uri : String?) {
        if (uri == null) {
            throw  NullPointerException("Cannot call Result with null value")
        } else {
            val intent = Intent(this.applicationContext , PlantChooserActivity::class.java)
            intent.putExtra(AppConstants.TRANSFER_DATA , uri)
            startActivity(intent)
        }

    }
     override fun toString():String{
         return "MainActivity()"
     }

}
