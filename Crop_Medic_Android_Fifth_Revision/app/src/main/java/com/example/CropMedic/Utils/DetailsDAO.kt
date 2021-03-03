package com.example.CropMedic.Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

open class DetailsDAO  {
  	var category:String
  	var cause:String
  	var defaultPicture:Bitmap?=null
  	var name:String
  	var precautions:ArrayList<String>
  	var recommendedMedicines:String
  	var symptoms:ArrayList<String>
	var futureSteps:ArrayList<String>

 	constructor() {
 	 	category=""
	 	cause=""
	 	name=""
	 	precautions= ArrayList()
	 	recommendedMedicines=""
	 	symptoms= ArrayList()
		futureSteps= ArrayList()
 	}
	constructor(category:String,cause:String,defaultPicture:Bitmap,name:String,precautions:ArrayList<String>,recommendedMedicines:String,symptoms:ArrayList<String>,futureSteps:ArrayList<String>)
	{
		this.category=category
		this.cause=cause
		this.defaultPicture=defaultPicture
		this.name=name
		this.precautions=precautions
		this.recommendedMedicines=recommendedMedicines
		this.symptoms=symptoms
		this.futureSteps=futureSteps
	}

	fun decodeBase64(base64String:String):Boolean{
		if(base64String.trim().isNotEmpty()){
			Log.d("DetailsDAO",base64String)
			Log.d("DetailsDAO",base64String.length.toString())
			val decodedData=Base64.decode(base64String,Base64.DEFAULT)
			defaultPicture=BitmapFactory.decodeByteArray(decodedData,0,decodedData.size)
			return true
		}
		return false
	}
	fun destroyDefaultPicture():Boolean{
		if(defaultPicture !=null){
			defaultPicture?.recycle()
			defaultPicture=null
			return true
		}
		return false
	}
}