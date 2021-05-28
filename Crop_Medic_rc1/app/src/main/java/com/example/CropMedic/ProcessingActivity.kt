package com.example.CropMedic

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.CropMedic.ImageProcessor.CropMedicImageProcessor
import com.example.CropMedic.Network.Database
import com.example.CropMedic.Utils.ActivityUtils
import com.example.CropMedic.Utils.AppConstants
import java.nio.FloatBuffer
import kotlin.math.pow
import kotlin.math.sqrt

class ProcessingActivity : AppCompatActivity() {

	private  lateinit var uri: Uri
	private lateinit var plant_category :String
	private lateinit var imageProcessor: CropMedicImageProcessor
	private lateinit var database: Database

	companion object{
		private val TAG="ProcessingActivity"
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		database=Database() //Get the instance of the database class for sending and receiving firebase data
		retriveData()
		setContentView(R.layout.activity_processing)
		//Get the static instance of image processor
		imageProcessor= CropMedicImageProcessor.getInstance(this,getString(R.string.firebaseModelName),contentResolver)

		imageProcessor.readNewImage(uri)

		imageProcessor.processImage({success->
			val data=success.data
			Log.d(TAG,"Data capacity from image processor is:${data.capacity().toString()}")
			getLabel(data,plant_category)
			Log.d(TAG ,"Image Processing Successful")
		},{failure->
			val exception = failure.exception
			Log.e(TAG ,"Error in Image Processing $exception")
			ActivityUtils.triggerErrorActivity(this)
			finish()
		})
	}


	//Gets the String URI and store it into the Uri global variable
	private fun retriveData(){
		val intent=intent
		uri= Uri.parse(intent.getStringExtra(AppConstants.TRANSFER_DATA).toString())
		plant_category=intent.getStringExtra(AppConstants.PLANT_NAME).toString()
		Log.d(TAG,"Plant category : $plant_category")
	}


	//Generates the label from the output embeddings
	private fun getLabel(outputLabels: FloatBuffer , category:String){
		var predictedLabel=""
		database.getWeights(category,{
			//Compare all the plant euclidians based on the plant category
			if(it.isNotEmpty())
			{
				val keys=it.keys
				keys.forEach{key->
					it[key]?.let { it1 ->
						val norm=compareKeysandgetResult(outputLabels, it1)
						if(norm<0.5){
							predictedLabel=key
						}
					}
				}
			}
			startnextActivity(predictedLabel)
		},{
			Log.e(TAG ,it.toString())
		ActivityUtils.triggerErrorActivity(this)})

	}

	//Compare the embeddings from the computed buffer and database retrieved array and return distance
	private fun compareKeysandgetResult(buffer1: FloatBuffer , array1:FloatArray):Float{

		val floatArray=FloatArray(buffer1.limit())
		buffer1.rewind()
		buffer1.get(floatArray)
		Log.d(TAG,"Float array: ${floatArray[2]}")
		val distance=getEuclidianDistance(array1,floatArray)
		return  distance
	}


	//Calculate the Euclidian Distance/L2 Norm between two points
	private fun getEuclidianDistance(storedOutput : FloatArray,inferredOutput : FloatArray):Float{
		var mean=0.0f
		val length=storedOutput.size-1
		for (i in 0..length){
			mean+= (storedOutput[i] - inferredOutput[i]).toDouble().pow(2.0).toFloat()
		}
		mean= sqrt(mean)
		return mean
	}

	//Start the next activity based on the distance
	private fun startnextActivity(predictedLabel:String){
		if(predictedLabel.isEmpty()){
			val resultIntent= Intent(this,NoResultActivity::class.java)
			startActivity(resultIntent)
			finish()
		}
		else{
			val resultIntent= Intent(this,ResultActivity::class.java)
			resultIntent.putExtra("Label",predictedLabel)
			startActivity(resultIntent)
			finish()
		}
	}
}
