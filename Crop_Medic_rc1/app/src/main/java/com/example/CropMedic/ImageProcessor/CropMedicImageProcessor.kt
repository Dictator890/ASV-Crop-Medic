package com.example.CropMedic.ImageProcessor

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.example.CropMedic.Utils.*
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.Exception

/**
 * @param callContext Context The context of the activity invoking the object
 * @param contentResolver ContentResolver The content resolver of the calling activity
 *
 * **/
class CropMedicImageProcessor(
                              private val callContext:Context ,
                              private val modelName:String ,
                              private val contentResolver:ContentResolver
                              )
{
    companion object{
        private const val TAG="ImageProcessor"
        private const val outputSize=AppConstants.OUTPUT_EMBEDDINGS*java.lang.Float.SIZE/java.lang.Byte.SIZE

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance:CropMedicImageProcessor
        fun getInstance(callContext:Context , modelName:String , contentResolver:ContentResolver ):CropMedicImageProcessor{
            instance= CropMedicImageProcessor(callContext,modelName,contentResolver)
            return instance
        }
    }
    private  var inputBuffer : ByteBuffer = ByteBuffer.allocateDirect(AppConstants.IMAGE_WIDTH*AppConstants.IMAGE_HEIGHT*3*4).order(ByteOrder.nativeOrder())
    private var outputBuffer:ByteBuffer=ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder())


    /***
     * @param fileUri
     *         The uri of the image to be read
     * */
    fun readNewImage(fileUri:Uri){
        val imageBitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(fileUri,"r")?.fileDescriptor),AppConstants.IMAGE_WIDTH,AppConstants.IMAGE_HEIGHT,true)
        for(ycord in 0 until AppConstants.IMAGE_WIDTH){
            for(xcord in 0 until AppConstants.IMAGE_HEIGHT){
                val pixel=imageBitmap.getPixel(xcord,ycord)
                val rf=Color.red(pixel)/255f
                val gf=Color.green(pixel)/255f
                val bf=Color.blue(pixel)/255f

                inputBuffer.putFloat(rf)
                inputBuffer.putFloat(gf)
                inputBuffer.putFloat(bf)
            }
        }
        imageBitmap.recycle()
    }


    fun processImage(success : (ImageProcessResult.Success)->Unit,failure : (ImageProcessResult.Error)->Unit){
        val conditions = CustomModelDownloadConditions.Builder().build() // Also possible: .requireCharging() and .requireDeviceIdle()

      try{
            FirebaseModelDownloader.getInstance().getModel(modelName,DownloadType.LOCAL_MODEL,conditions).addOnSuccessListener {
              val model=it?.file
              if(model!= null)
              {
                  val interpreter=Interpreter(model)
                  interpreter.run(inputBuffer,outputBuffer)
                  outputBuffer.rewind()
                  val probs=outputBuffer.asFloatBuffer()
                  success(ImageProcessResult.Success(probs))
              }
              else
              {
                  useBundledModel(success,failure)
              }
          }.addOnFailureListener {
                useBundledModel(success,failure)
            }
      }
      catch (ex:Exception){
         failure(ImageProcessResult.Error(ex))
      }
    }

    private fun useBundledModel(success : (ImageProcessResult.Success) -> Any? , failure : (ImageProcessResult.Error) -> Any?)
    {
        try{
            val bundledModel=callContext.assets.open("$modelName.tflite").readBytes()
            val modelBuffer=ByteBuffer.allocateDirect(bundledModel.size).order(ByteOrder.nativeOrder())
            modelBuffer.put(bundledModel)
            val interpreter=Interpreter(modelBuffer,getModelOptions())
            interpreter.run(inputBuffer,outputBuffer)
            outputBuffer.rewind()
            val probs=outputBuffer.asFloatBuffer()
            success(ImageProcessResult.Success(probs))
        }
        catch (ex:Exception){
            failure(ImageProcessResult.Error(ex))
        }

    }
    private fun getModelOptions():Interpreter.Options {
        val compatList=CompatibilityList()
        return Interpreter.Options().apply {
            if(compatList.isDelegateSupportedOnThisDevice){
                addDelegate(GpuDelegate(compatList.bestOptionsForThisDevice))
            }
            else{
                setNumThreads(Runtime.getRuntime().availableProcessors())
            }
        }

    }



    }
sealed class ImageProcessResult{
    data class Success(val data : FloatBuffer):ImageProcessResult()
    data class Error(val exception: java.lang.Exception):ImageProcessResult()
}
