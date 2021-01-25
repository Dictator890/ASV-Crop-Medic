package com.example.CropMedic.ImageProcessor

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.CropMedic.Utils.Codes
import com.example.CropMedic.Utils.ResultFormat
import com.example.CropMedic.ml.ModelMininet
import org.tensorflow.lite.support.image.TensorImage
import java.lang.Exception


class CropMedicImageProcessor(file:Uri,ctx:Context,modelPath:String)  {
var file_URI=file

var call_context:Context?=ctx
private val modelPath=modelPath

    companion object{
        public fun getImageProcessor(file_URI:Uri,context: Context,modelPath:String):CropMedicImageProcessor {
            if(imageProcessorObject == null){
                imageProcessorObject= CropMedicImageProcessor(file_URI,context,modelPath)
            }


                return imageProcessorObject as CropMedicImageProcessor


        }
        private val TAG="ImageProcessor"
        private  var imageProcessorObject:CropMedicImageProcessor?=null
    }



    fun processImageModelBundling():ImageProcessResult{
    if(call_context !=null){
        val model=ModelMininet.newInstance(call_context!!)
        Log.d(TAG,"Encoded PAth : "+file_URI.encodedPath)
        Log.d(TAG,"Is absolute:"+file_URI.isAbsolute)
val bitmapTensorImage=if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.P){

    TensorImage.fromBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(call_context !!.contentResolver,file_URI)))
}else{
    TensorImage.fromBitmap(MediaStore.Images.Media.getBitmap(call_context !!.contentResolver,file_URI))
}

       val output= model.process(bitmapTensorImage)
        val result=output.probabilityAsCategoryList
        Log.d(TAG,"Sucess in ImageProcessor")
        model.close()
        return ImageProcessResult.Success(ResultFormat(Codes.SUCCESS,result))

           }
        Log.e(TAG,"Failure in ImageProcessor")

       return ImageProcessResult.Error(ResultFormat(Codes.FAILURE,Exception("Call context not found")))

    }
sealed class ImageProcessResult{
    data class Success(val data : ResultFormat):ImageProcessResult()
    data class Error(val exception: ResultFormat):ImageProcessResult()
}
    }