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
import java.io.BufferedReader
import java.io.InputStreamReader
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
class CropMedicImageProcessor(private val fileUri:Uri ,
                              private val callContext:Context ,
                              private val modelName:String ,
                              private val contentResolver:ContentResolver ,
                              private val labelPathname:String
                              )
{
    companion object{
         fun getImageProcessor(file_URI:Uri,context: Context,firebaseModelName_:String,contentResolver : ContentResolver,labelpathName:String):CropMedicImageProcessor {
            if(imageProcessorObject == null){
                return CropMedicImageProcessor(file_URI,context,firebaseModelName_,contentResolver,labelpathName)
            }
                return imageProcessorObject as CropMedicImageProcessor
        }
        private val TAG="ImageProcessor"
        private var imageProcessorObject: CropMedicImageProcessor?=null
        private val outputSize=AppConstants.OUTPUT_EMBEDDINGS*java.lang.Float.SIZE/java.lang.Byte.SIZE
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
                  val interpreter=Interpreter(model)
                  val imageBitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(fileUri,"r")?.fileDescriptor),AppConstants.IMAGE_WIDTH,AppConstants.IMAGE_HEIGHT,true)
                 // Log.d(TAG,"Image bitmap scaled")
                 // Log.d(TAG,"Width: " + imageBitmap.width +"\n Height: "+imageBitmap.height+"ByteCount:"+imageBitmap.byteCount)
                 // Log.d(TAG,"Image Bitmap Config:"+imageBitmap.config+"Pixel value at 20:"+imageBitmap.getPixel(20,20))

                  val inputImage=ByteBuffer.allocateDirect(AppConstants.IMAGE_WIDTH*AppConstants.IMAGE_HEIGHT*3*4).order(ByteOrder.nativeOrder())
                 // Log.d(TAG,"Input image buffer allocated \n Memory is :" +inputImage.capacity())
                  for(ycord in 0 until AppConstants.IMAGE_WIDTH){
                      for(xcord in 0 until AppConstants.IMAGE_HEIGHT){
                          val pixel=imageBitmap.getPixel(xcord,ycord)
                          val rf=Color.red(pixel)/255f
                          val gf=Color.green(pixel)/255f
                          val bf=Color.blue(pixel)/255f

                          inputImage.putFloat(rf)
                          inputImage.putFloat(gf)
                          inputImage.putFloat(bf)
                      }
                  }
                  imageBitmap.recycle()
                 // Log.d(TAG,"Image bitmap recycled")
                  val modelOutput=ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder())
                  interpreter.run(inputImage,modelOutput)
                  Log.d(TAG,"Model is running now")
                  modelOutput.rewind()
                  val probs=modelOutput.asFloatBuffer()
                  Log.d(TAG,"Got probabilities \n $probs")
                  success(ImageProcessResult.Success(probs))

              }
              //The firebase model was unable to be fetched so the bundled model is being used
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
            val interpreter=Interpreter(modelBuffer)

            val imageBitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(fileUri,"r")?.fileDescriptor),256,256,true)
           // Log.d(TAG,"Image bitmap scaled")
            //Log.d(TAG,"Width: " + imageBitmap.width +"\n Height: "+imageBitmap.height+"ByteCount:"+imageBitmap.byteCount)
            val inputImage=ByteBuffer.allocateDirect(AppConstants.IMAGE_WIDTH*AppConstants.IMAGE_HEIGHT*3*4).order(ByteOrder.nativeOrder())
            // Log.d(TAG,"Input image buffer allocated \n Memory is :" +inputImage.capacity())
            for(ycord in 0 until AppConstants.IMAGE_WIDTH){
                for(xcord in 0 until AppConstants.IMAGE_HEIGHT){
                    val pixel=imageBitmap.getPixel(xcord,ycord)
                    val rf=Color.red(pixel)/255f
                    val gf=Color.green(pixel)/255f
                    val bf=Color.blue(pixel)/255f

                    inputImage.putFloat(rf)
                    inputImage.putFloat(gf)
                    inputImage.putFloat(bf)
                }
            }
            imageBitmap.recycle()
           // Log.d(TAG,"Image bitmap recycled")
            val modelOutput=ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder())

            interpreter.run(inputImage,modelOutput)
            Log.d(TAG,"Model is running now")
            modelOutput.rewind()
            val probs=modelOutput.asFloatBuffer()
            Log.d(TAG,"Got probabilities \n $probs")
            success(ImageProcessResult.Success(probs))
        }
        catch (ex:Exception){
            failure(ImageProcessResult.Error(ex))
        }

    }



    }
sealed class ImageProcessResult{
    data class Success(val data : FloatBuffer):ImageProcessResult()
    data class Error(val exception: java.lang.Exception):ImageProcessResult()
}
