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

    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retriveData()
        setContentView(R.layout.activity_result)
        resulttextcomponent = findViewById(R.id.resulttext)
        imageProcessor= CropMedicImageProcessor.getImageProcessor(uri,this,getString(R.string.relativeModelPath))
        if (uri == null){
            resulttextcomponent.visibility= View.VISIBLE
            Log.d(TAG,"Null URI")
        }
        else
        {
            Log.d(TAG,"Non Null URI")
              val value:CropMedicImageProcessor.ImageProcessResult= imageProcessor.processImageModelBundling()
            Log.d(TAG,"Hello")
            when(value){
                is CropMedicImageProcessor.ImageProcessResult.Success->{Log.d(TAG,"Sucess")
                    val data=value.data.data.toString()
                    resulttextcomponent.text=getString(R.string.SucessMessage,data)
                Log.d(TAG,data)
                }
                is CropMedicImageProcessor.ImageProcessResult.Error->{Log.e(TAG,"Fail")
                    val data=value.exception.data.toString()
                    resulttextcomponent.text=getString(R.string.ErrorMessage,data)
                    Log.d(TAG,data)}
            }

        }

    }
    private fun retriveData(){
        val intent=intent
        uri= Uri.parse(intent.getStringExtra(AppConstants.TRANSFER_DATA).toString())
    }
}