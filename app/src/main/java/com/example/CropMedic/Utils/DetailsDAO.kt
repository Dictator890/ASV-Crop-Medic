package com.example.CropMedic.Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

open class DetailsDAO  {
  	var category:String?
  	var defaultPicture:Bitmap?=null
  	var name:String?
  	var precautions:ArrayList<String>?
  	var recommendedMedicines:ArrayList<String>?
  	var symptoms:String?
	var futureSteps:ArrayList<String>?

 	constructor() {
 	 	category=""
	 	name=""
	 	precautions= ArrayList()
	 	recommendedMedicines=ArrayList()
	 	symptoms= ""
		futureSteps= ArrayList()
 	}
	constructor(category:String,defaultPicture:Bitmap,name:String,precautions:ArrayList<String>,recommendedMedicines:ArrayList<String>,symptoms:String,futureSteps:ArrayList<String>)
	{
		this.category=category
		this.defaultPicture=defaultPicture
		this.name=name
		this.precautions=precautions
		this.recommendedMedicines=recommendedMedicines
		this.symptoms=symptoms
		this.futureSteps=futureSteps
	}

	fun decodeBase64(base64String:String):Boolean{
		Log.d("DAO", base64String.length.toString())
		if(base64String.trim().isNotEmpty()){
			val decodedData=Base64.decode(base64String,Base64.DEFAULT)
			defaultPicture=BitmapFactory.decodeByteArray(decodedData,0,decodedData.size)
			return true
		}
		return false
	}
}