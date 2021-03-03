package com.example.CropMedic.ImageProcessor

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.example.CropMedic.Utils.*
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.Exception

/**
 * @param fileUri Uri The Uri of the image
 * @param callContext Context The context of the activity invoking the object
 * @param firebaseModelName String The name of the firebase model on the Firebase Server
 * @param contentResolver ContentResolver The content resolver of the calling activity
 * @param labelPathname String The name of the label file in the assets
 * @param bundledmodelName String The name of the locally bundled model in the assets
 *
 * **/
class CropMedicImageProcessor(
                              private val callContext:Context ,
                              private val modelName:String ,
                              private val contentResolver:ContentResolver ,
                              private val labelPathname:String
                              )
{
    companion object{
        private const val TAG="ImageProcessor"
        private const val outputSize=AppConstants.OUTPUT_EMBEDDINGS*java.lang.Float.SIZE/java.lang.Byte.SIZE
        private lateinit var instance:CropMedicImageProcessor
        fun getInstance(callContext:Context , modelName:String , contentResolver:ContentResolver , labelPathname:String):CropMedicImageProcessor{
            instance= CropMedicImageProcessor(callContext,modelName,contentResolver,labelPathname)
            return instance
        }
    }
    private  var inputBuffer : ByteBuffer = ByteBuffer.allocateDirect(AppConstants.IMAGE_WIDTH*AppConstants.IMAGE_HEIGHT*3*4).order(ByteOrder.nativeOrder())
    private var outputBuffer:ByteBuffer=ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder())

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

        val remoteModel=FirebaseCustomRemoteModel.Builder(modelName).build()
      try{
            FirebaseModelManager.getInstance().getLatestModelFile(remoteModel).addOnCompleteListener {
              val model=it.result
                var counter=0
              if(model!= null)
              {
                  Log.d(TAG,"Processing using firebase model")
                  val interpreter=Interpreter(model,getModelOptions())
                  interpreter.run(inputBuffer,outputBuffer)
                  Log.d(TAG,"Model is running now")
                  outputBuffer.rewind()
                  val probs=outputBuffer.asFloatBuffer()
                  Log.d(TAG,"Got probabilities \n $probs")
                  success(ImageProcessResult.Success(probs))

              }
              else
              {
                  useBundledModel(success,failure)
              }
          }
      }
      catch (ex:Exception){
         failure(ImageProcessResult.Error(ex))
      }
    }

    private fun useBundledModel(success : (ImageProcessResult.Success) -> Any? , failure : (ImageProcessResult.Error) -> Any?)
    {
        Log.d(TAG,"OutputSize : $outputSize")
        Log.d(TAG,"Processing using  Asset Bundled Model")
        try{
            val bundledModel=callContext.assets.open(modelName+".tflite").readBytes()
            val modelBuffer=ByteBuffer.allocateDirect(bundledModel.size).order(ByteOrder.nativeOrder())
            modelBuffer.put(bundledModel)
            val interpreter=Interpreter(modelBuffer,getModelOptions())
            interpreter.run(inputBuffer,outputBuffer)
            Log.d(TAG,"Model is running now")
            outputBuffer.rewind()
            val probs=outputBuffer.asFloatBuffer()
            Log.d(TAG,"Got probabilities \n $probs")
            success(ImageProcessResult.Success(probs))
        }
        catch (ex:Exception){
            failure(ImageProcessResult.Error(ex))
        }

    }
    private fun getModelOptions():Interpreter.Options {
        val compatList=CompatibilityList()
        return Interpreter.Options().apply {
            Log.d(TAG,"compatList.isDelegatesuppourted=${compatList.isDelegateSupportedOnThisDevice}")
            if(compatList.isDelegateSupportedOnThisDevice){
                addDelegate(GpuDelegate(compatList.bestOptionsForThisDevice))
                Log.d(TAG ,"Gpu Delegate Suppourted")
            }
            else{
                setNumThreads(Runtime.getRuntime().availableProcessors())
                Log.d(TAG ,"Gpu Delegate Not suppourted")
                Log.d(TAG ,"Available processors:${Runtime.getRuntime().availableProcessors()}")
            }
        }

    }



    }
sealed class ImageProcessResult{
    data class Success(val data : FloatBuffer):ImageProcessResult()
    data class Error(val exception: java.lang.Exception):ImageProcessResult()
}
