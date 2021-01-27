package com.example.CropMedic

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.TextView
import com.example.CropMedic.ImageProcessor.CropMedicImageProcessor
import com.example.CropMedic.Utils.AppConstants

class ResultActivity : AppCompatActivity() {

    private lateinit var resulttextcomponent:TextView
    private  lateinit var uri:Uri
    private lateinit var imageProcessor: CropMedicImageProcessor

    companion object{
        private const val TAG="ResultActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        retriveData()
        setContentView(R.layout.activity_result)
        resulttextcomponent = findViewById(R.id.resulttext)
        imageProcessor= CropMedicImageProcessor.getImageProcessor(uri,this,getString(R.string.firebaseModelName),contentResolver,getString(R.string.labelFileName),getString(R.string.modelName))

            imageProcessor.processImage({success->
                val data=success.data.data
                resulttextcomponent.text=getString(R.string.SucessMessage,data.toString())
                Log.d(TAG,"Image Processing Successful")
                return@processImage null

            },{failure->
                val exception = failure.exception
                Log.e(TAG,"Error in Image Processing $exception")
                resulttextcomponent.text=getString(R.string.ErrorMessage,exception.toString())
                return@processImage null
            })



    }
    private fun retriveData(){
        val intent=intent
        uri= Uri.parse(intent.getStringExtra(AppConstants.TRANSFER_DATA).toString())
    }
}