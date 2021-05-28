package com.example.CropMedic.FileChooserActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.CropMedic.Utils.AppConstants
import com.example.CropMedic.FileChooserActivity.FileChooser.FileChooser
import com.example.CropMedic.R
import com.example.CropMedic.Utils.ActivityUtils
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
    private lateinit var chooseanotherFileButton:ImageButton
    private lateinit var okButton:ImageButton
    private lateinit var imageDisplaySurface: ImageView

    private lateinit var contract : ActivityResultLauncher<Intent?>

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.hideActionBar(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_chooser)

            if(arePermissionsGranted(permissions)){
                allPermissionsGranted()
            }
            else
            {
                requestPermissions(permissions)
                contract=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    res->
                    if(res.resultCode == Activity.RESULT_OK){
                        val uri=res.data?.dataString.toString()
                        updateStorage(uri,storage)
                    }
                    else{
                        finish()
                    }
                }
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
        @Suppress("UNCHECKED_CAST")
        if(arePermissionsGranted(permissions as Array<String>)){
            allPermissionsGranted()
        }
    }
    private fun allPermissionsGranted(){
try{
    initVars()
    chooseanotherFileButton.setOnClickListener {
        initChooser(chooserIntent,
            AppConstants.FILE_CHOOSER_PERMISSION_REQUEST)
    }
    okButton.setOnClickListener {
        endActivity()
    }

    initChooser(chooserIntent, AppConstants.FILE_CHOOSER_PERMISSION_REQUEST)
}catch (e:Exception){
    finish()
}

    }
    private fun initVars()
    {
        searchMIME="image/*"
        chooserIntent=FileChooser(if(searchMIME.trim().isNotEmpty()) searchMIME else "file/*").getFileIntent()
        chooseanotherFileButton=findViewById(R.id.ChooseAnotherFileBtn)
        okButton=findViewById(R.id.OKButton)
        imageDisplaySurface=findViewById(R.id.ImageSurface)

    }
    private fun initChooser(fileChooser:Intent,Code:Int){
        contract.launch(fileChooser)
    }

    private fun endActivity(){
        if (storage.uri.trim().isNotEmpty()){
            setResult(Activity.RESULT_OK,GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,storage.uri))
            finish()
        }
        else
        {
            setResult(Activity.RESULT_CANCELED,GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,storage.uri))
            finish()
        }
    }
    private fun updateStorage(uri: String,storage: UriStorage){
        storage.uri=uri
        imageDisplaySurface.setImageURI(Uri.parse(storage.uri))
    }


    private data class UriStorage(var uri:String="")
}
