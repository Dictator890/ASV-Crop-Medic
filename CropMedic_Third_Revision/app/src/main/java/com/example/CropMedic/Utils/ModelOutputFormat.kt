package com.example.CropMedic.Utils

 class ModelOutputFormat() {
 	var entries= mutableListOf<ModelOutputUnit>()

	 fun addEntry(entry:ModelOutputUnit){
	 	entries.add(entry)
	 }
	 fun getList():List<ModelOutputUnit>{
	 	return entries.toList()
	 }
	 fun getSize():Int{
	 	return entries.size
	 }


 }
data class ModelOutputUnit(val label:String,val confidence:Float)
