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
	private lateinit var imageProcessor: CropMedicImageProcessor
	private lateinit var database: Database

	companion object{
		private val TAG="ProcessingActivity"
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		database=Database(this.applicationContext)
		retriveData()
		setContentView(R.layout.activity_processing)

		imageProcessor= CropMedicImageProcessor.getImageProcessor(uri,this,getString(R.string.firebaseModelName),contentResolver,getString(R.string.labelFileName))
		imageProcessor.processImage({success->
			val data=success.data
			getLabel(data,"Tomato")
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
	}
	//Generates the label from the output embeddings
	private fun getLabel(outputLabels: FloatBuffer , category:String){
		var label=""
		database.getWeights(category,{ it ->
			Log.d(TAG ,"Is HashMap Empty:"+it.isEmpty())
			Log.d(TAG ,"HashMap keys:"+it.keys.toString())
			if(it.isNotEmpty())
			{
				val keys=it.keys
				keys.forEach{key->
					it[key]?.let { it1 ->
						val norm=compareKeysandgetResult(outputLabels, it1)
						if(norm<0.5){
							label=key
						}
					}
				}
				Log.d(TAG ,label)
			}
			startnextActivity(label)
		},{ Log.e(TAG ,it.toString())})

	}

	//Compare the embeddings from the computed buffer and database retrieved array
	private fun compareKeysandgetResult(buffer1: FloatBuffer , array1:FloatArray):Float{
		val floatArray=FloatArray(buffer1.limit())
		buffer1.rewind()
		buffer1.get(floatArray)
		val distance=getEuclidianDistance(array1,floatArray)

		Log.d(TAG ,"Euclidian Distance: $distance")
		return  distance
	}
	//Calculate the Euclidian Distance between two points
	private fun getEuclidianDistance(storedOutput : FloatArray,inferredOutput : FloatArray):Float{
		var mean=0.0f
		val length=storedOutput.size-1
		Log.d(TAG ,"Inferred Length= $length")
		for (i in 0..length){
			Log.d(TAG ,"Inferred="+inferredOutput[i])
			mean+= (storedOutput[i] - inferredOutput[i]).toDouble().pow(2.0).toFloat()
		}
		mean= sqrt(mean)
		return mean
	}
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

	//Holds the final output label
	data class ResultHolder(var label:String,var value:Float){
		override fun toString() : String {
			return  "Label : $label Value:$value"
		}
	}
}
