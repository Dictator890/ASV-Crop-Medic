package com.example.CropMedic


import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.example.CropMedic.CameraUsage.CameraActivity
import com.example.CropMedic.FileChooserActivity.FileChooserActivity
import com.example.CropMedic.Utils.AppConstants
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
        private lateinit var cameraOpenButton : Button
        private lateinit var filechooserOpenButton : Button
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        //The code to hide the action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }

        //Set the layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Assign the layout elements to objects
        cameraOpenButton = findViewById(R.id.CameraActivityTrigger) // Button for camera opening
        filechooserOpenButton =
            findViewById(R.id.FileChooserActivityTrigger) //Button for file chooser opening
        cameraOpenButton.setOnClickListener {
            openCamera() //Open the camera when open camera button is pressed
        }
        filechooserOpenButton.setOnClickListener {
            openFileChooser()//Open the file chooser when open file chooser button is pressed
        }
        //Start the Model Download Activity
        openModelDownloader()



    }

    //The most important method as it will handle all the results of opened activities
    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        Log.d(TAG , "onActivityResultFired")
        //Result code is which consist if result is OK or not ok
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    //Activity return from camera activity :Gets the URI
                    AppConstants.CAMERA_INTENT_TRIGGER -> {
                        val s = data?.getStringExtra(AppConstants.INTENT_CALL)
                        Toast.makeText(this , "Data received : $s" , Toast.LENGTH_LONG).show()
                        Log.d(TAG , "Data has been received")
                        callResult(s)
                    }
                    //Activity return from file chooser activity :Gets the URI
                    AppConstants.FILE_CHOOSER_INTENT_TRIGGER -> {
                        val s = data?.getStringExtra(AppConstants.INTENT_CALL)
                        Toast.makeText(this , "Data received : $s" , Toast.LENGTH_LONG).show()
                        Log.d(TAG , "Data has been received")
                        callResult(s)
                    }
                    AppConstants.MODEL_DOWNLOAD_TRIGGER->{
                        val answer=data?.getBooleanExtra(AppConstants.MODEL_DOWNLOAD_RESULT,false)
                        Log.d(TAG,"Result received for Model Download $answer")
                        if(answer == false){

                        }
                    }
                }
            }


            Activity.RESULT_CANCELED -> {
                Log.e(TAG , data?.getStringExtra(AppConstants.INTENT_CALL)?.length.toString())
                Log.e(TAG , "Error code : $requestCode")
                Toast.makeText(this , R.string.imageNotReceived , Toast.LENGTH_LONG).show()
            }
        }
    }

    //Opens the camera
    private fun openCamera() {
        startActivityForResult(
            Intent(this.applicationContext , CameraActivity::class.java) ,
            AppConstants.CAMERA_INTENT_TRIGGER
        )
    }

    //Opens the file chooser
    private fun openFileChooser() {
        var intent=   Intent(this.applicationContext , FileChooserActivity::class.java)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(
          intent ,
            AppConstants.FILE_CHOOSER_INTENT_TRIGGER
        )
    }
    private fun openModelDownloader(){
        startActivityForResult(Intent(this.applicationContext,ModelDownloadActivity::class.java),AppConstants.MODEL_DOWNLOAD_TRIGGER)
    }

    //When the URI is got from any source this function will then open the processing/result activity
    private fun callResult(uri : String?) {
        if (uri == null) {
            throw  NullPointerException("Cannot call Result with null value")
        } else {
            val intent = Intent(this.applicationContext , ResultActivity::class.java)
            intent.putExtra(AppConstants.TRANSFER_DATA , uri)
            startActivityForResult(intent , AppConstants.RESULT_INTENT_TRIGGER)
        }

    }
}
