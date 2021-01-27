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
import kotlin.Exception


class CropMedicImageProcessor(private val fileUri:Uri ,
                              private val callContext:Context ,
                              private val firebaseModelName:String ,
                              private val contentResolver:ContentResolver ,
                              private val labelPathname:String,
                              private val bundledmodelName : String)
{
    companion object{
         fun getImageProcessor(file_URI:Uri,context: Context,firebaseModelName_:String,contentResolver : ContentResolver,labelpathName:String,bundledmodelName:String):CropMedicImageProcessor {
            if(imageProcessorObject == null){
                return CropMedicImageProcessor(file_URI,context,firebaseModelName_,contentResolver,labelpathName,bundledmodelName)
            }
                return imageProcessorObject as CropMedicImageProcessor
        }
        private val TAG="ImageProcessor"
        private var imageProcessorObject: CropMedicImageProcessor?=null
    }



    fun processImage(success : (ImageProcessResult.Success)->Any?,failure : (ImageProcessResult.Error)->Any?){
        val remoteModel=FirebaseCustomRemoteModel.Builder(firebaseModelName).build()
        val outputFormat=ModelOutputFormat()
      try{
            FirebaseModelManager.getInstance().getLatestModelFile(remoteModel).addOnCompleteListener {
              val model=it.result
              if(model!= null)
              {
                  Log.d(TAG,"Processing using firebase model")
                  val interpreter=Interpreter(model)
                  val imageBitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(fileUri,"r")?.fileDescriptor),256,256,true)
                  Log.d(TAG,"Image bitmap scaled")
                  Log.d(TAG,"Width: " + imageBitmap.width +"\n Height: "+imageBitmap.height)
                  val inputImage=ByteBuffer.allocateDirect(256*256*3*4).order(ByteOrder.nativeOrder())
                  Log.d(TAG,"Input image buffer allocated \n Memory is :" +inputImage.capacity())
                  for(ycord in 0 until 255){
                      for(xcord in 0 until 255){
                          val pixel=imageBitmap.getPixel(xcord,ycord)
                          inputImage.putFloat(Color.red(pixel).toFloat())
                          inputImage.putFloat(Color.green(pixel).toFloat())
                          inputImage.putFloat(Color.blue(pixel).toFloat())
                      }
                  }
                  imageBitmap.recycle()
                  Log.d(TAG,"Image btmap recycled")
                  val modelOutput=ByteBuffer.allocateDirect((38*java.lang.Float.SIZE/java.lang.Byte.SIZE)).order(ByteOrder.nativeOrder())
                  interpreter.run(inputImage,modelOutput)
                  Log.d(TAG,"Model is running now")
                  modelOutput.rewind()
                  val probs=modelOutput.asFloatBuffer()
                  val probsCapacity=probs.capacity()-1
                  Log.d(TAG,"Got probabilities \n $probs")
                  try{
                      val reader = BufferedReader(InputStreamReader(callContext.assets.open(labelPathname)))

                      for(i in 0..probsCapacity)
                      {

                          val textData=reader.readLine()
                          Log.d(TAG,"Readline data: $textData")
                          val unit=ModelOutputUnit(textData,probs.get(i))
                          outputFormat.addEntry(unit)
                      }

                      success(ImageProcessResult.Success(ResultFormat(Codes.SUCCESS,outputFormat.getList())))
                  }
                  catch (ex:Exception){
                      failure(ImageProcessResult.Error(ex))
                      Log.e(TAG,ex.toString())
                  }

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
        val outputFormat=ModelOutputFormat()
        Log.d(TAG,"Processing using  Asset Bundled Model")
        try{
            val inputStream=callContext.assets.open(bundledmodelName)
            val bundledModel=ByteArray(inputStream.available())
            inputStream.read(bundledModel)
            val modelBuffer=ByteBuffer.allocateDirect(bundledModel.size)
            modelBuffer.put(bundledModel)
            val interpreter=Interpreter(modelBuffer)

            val imageBitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(fileUri,"r")?.fileDescriptor),256,256,true)
            Log.d(TAG,"Image bitmap scaled")
            Log.d(TAG,"Width: " + imageBitmap.width +"\n Height: "+imageBitmap.height)
            val inputImage=ByteBuffer.allocateDirect(256*256*3*4).order(ByteOrder.nativeOrder())
            Log.d(TAG,"Input image buffer allocated \n Memory is :" +inputImage.capacity())
            for(ycord in 0 until 255){
                for(xcord in 0 until 255){
                    val pixel=imageBitmap.getPixel(xcord,ycord)
                    inputImage.putFloat(Color.red(pixel).toFloat())
                    inputImage.putFloat(Color.green(pixel).toFloat())
                    inputImage.putFloat(Color.blue(pixel).toFloat())
                }
            }
            imageBitmap.recycle()
            Log.d(TAG,"Image bitmap recycled")
            val modelOutput=ByteBuffer.allocateDirect((38*java.lang.Float.SIZE/java.lang.Byte.SIZE)).order(ByteOrder.nativeOrder())
            interpreter.run(inputImage,modelOutput)
            Log.d(TAG,"Model is running now")
            modelOutput.rewind()
            val probs=modelOutput.asFloatBuffer()
            val probsCapacity=probs.capacity()-1
            Log.d(TAG,"Got probabilities \n $probs")
            try{
                val reader = BufferedReader(InputStreamReader(callContext.assets.open(labelPathname)))

                for(i in 0..probsCapacity)
                {
                    val textData=reader.readLine()
                    Log.d(TAG,"Readline data: $textData")
                    val unit=ModelOutputUnit(textData,probs.get(i))
                    outputFormat.addEntry(unit)
                }

                success(ImageProcessResult.Success(ResultFormat(Codes.SUCCESS,outputFormat.getList())))
            }
            catch (ex:Exception){
                failure(ImageProcessResult.Error(ex))
                Log.e(TAG,ex.toString())
            }
        }
        catch (ex:Exception){
            failure(ImageProcessResult.Error(ex))
        }

    }



    }
sealed class ImageProcessResult{
    data class Success(val data : ResultFormat):ImageProcessResult()
    data class Error(val exception: java.lang.Exception):ImageProcessResult()
}
