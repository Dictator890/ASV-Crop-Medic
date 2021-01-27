package com.example.CropMedic.FileChooserActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.CropMedic.Utils.AppConstants
import com.example.CropMedic.FileChooserActivity.FileChooser.FileChooser
import com.example.CropMedic.R
import com.example.CropMedic.Utils.GenerateTransferIntent
import kotlin.Exception

class FileChooserActivity : AppCompatActivity() {
    companion object{
        private const val TAG="FileChooserActivity"
        private val permissions= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private lateinit var searchMIME:String
    private val storage=UriStorage()
    private lateinit var chooserIntent:Intent
    private lateinit var chooseanotherFileButton:Button
    private lateinit var okButton:Button
    private lateinit var imageDisplaySurface: ImageView
    private lateinit var textMessage:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_chooser)

            if(arePermissionsGranted(permissions)){
                allPermissionsGranted()
            }
            else
            {
                requestPermissions(permissions)
            }


    }
    private fun arePermissionsGranted(permissions:Array<String>)= permissions.all { iterator-> ContextCompat.checkSelfPermission(baseContext,iterator)== PackageManager.PERMISSION_GRANTED}

    //Request the permissions that are not given
    private fun requestPermissions(permissions:Array<String>){
        ActivityCompat.requestPermissions(this, permissions,
            AppConstants.FILE_CHOOSER_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(arePermissionsGranted(permissions as Array<String>)){
            allPermissionsGranted()
        }
    }
    private fun allPermissionsGranted(){
try{
    initVars()
    Log.d(TAG,"initVarsFired")
    chooseanotherFileButton.setOnClickListener {
        initChooser(chooserIntent,
            AppConstants.FILE_CHOOSER_PERMISSION_REQUEST)
    }
    okButton.setOnClickListener {
        endActivity()
    }
    initChooser(chooserIntent, AppConstants.FILE_CHOOSER_PERMISSION_REQUEST)
}catch (e:Exception){
    Log.e(TAG,e.printStackTrace().toString())
    finish()
}

    }
    private fun initVars()
    {
        searchMIME="image/*"
        chooserIntent=FileChooser(if(searchMIME.trim().length >0 ) searchMIME else "file/*").getFileIntent()
        chooseanotherFileButton=findViewById(R.id.ChooseAnotherFileBtn)
        okButton=findViewById(R.id.OKButton)
        imageDisplaySurface=findViewById(R.id.ImageSurface)
        textMessage=findViewById(R.id.textView)
    }
    private fun initChooser(fileChooser:Intent,Code:Int){
        Log.d(TAG,"In init file choosser")
        startActivityForResult(fileChooser,Code)
    }

    private fun endActivity(){
        if (!(storage.uri.trim().isEmpty())){
            Log.e(TAG,"In completed")
            Log.e(TAG,"Uri is : ${storage.uri} and length is ${storage.uri.length}")
            setResult(Activity.RESULT_OK,GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,storage.uri))
            finish()
        }
        else
        {
            Log.e(TAG,"Uri is : ${storage.uri} and length is ${storage.uri.length}")
            Log.wtf(TAG,"Uri is : ${storage.uri} and length is ${storage.uri.length}")
            setResult(Activity.RESULT_CANCELED,GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,storage.uri))
            finish()
        }
    }
    private fun updateStorage(uri: String,storage: UriStorage){
        storage.uri=uri
        imageDisplaySurface.setImageURI(Uri.parse(storage.uri))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG,"OnActivityResultFired")
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            Activity.RESULT_OK->
                when(requestCode){
                    AppConstants.FILE_CHOOSER_PERMISSION_REQUEST-> {
                        var uri=data?.dataString.toString()
                        Log.d(TAG,"URi Is :"+uri)
                        Log.e(TAG,uri+"LOL")
                        updateStorage(uri,storage)
                        if (uri.trim().isEmpty())
                        {
                            textMessage.visibility= View.VISIBLE
                        }
                        else
                        {
                            textMessage.visibility=View.GONE
                        }

                    }
                }
        }
    }
    private data class UriStorage(var uri:String=""){
    }
}
