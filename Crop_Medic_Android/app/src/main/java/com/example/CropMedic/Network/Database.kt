package com.example.CropMedic.Network

import android.content.Context
import android.util.Log
import com.example.CropMedic.Utils.DetailsDAO
import com.example.CropMedic.Utils.EmptyResultException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class Database(context : Context) {
	companion object{
		val firebaseCommunication=FirebaseCommunication()
		private  val TAG="Database"

	}
	fun getWeights(category:String,success:(HashMap<String,FloatArray>)->Unit,failure:(Exception)->Unit){
		firebaseCommunication.getsingleCollection(FirebaseConfig.collectionName,FirebaseConfig.categoryName,category,{
			val map= HashMap<String,FloatArray>()
			Log.d(TAG,it.toString())
			Log.d(TAG,"iS Query Null : "+(it == null).toString())
			if (it != null) {
				Log.d(TAG,"Size of result:"+it.size())
				Log.d(TAG,"Is Empty"+it.isEmpty)
				Log.d(TAG,"Metadata:" +it.metadata)
				it.forEach {
					map[it.get(DatabaseConfig.name) as String] = (it.get(DatabaseConfig.Weights) as ArrayList<Float>).toFloatArray()
				}
				success(map)
			}
			else
			{
				failure(EmptyResultException())
			}
		},failure)

	}
	fun getOfflineInformation(name : String,success:(DetailsDAO)->Unit,failure:(Exception)->Unit){
		firebaseCommunication.getsinglecollectionOffline(FirebaseConfig.collectionName,FirebaseConfig.nameField,name,
			{
				if(it!=null){
					Log.d(TAG,it.documents.size.toString())
					val mutableMap=it.documents[0].data
					mutableMap?.remove(DatabaseConfig.Weights)
					val dao=DetailsDAO()
					dao.category=mutableMap?.get(DatabaseConfig.category).toString()
					dao.cause=mutableMap?.get(DatabaseConfig.cause).toString()
					dao.decodeBase64(mutableMap?.get(DatabaseConfig.DefaultPicture).toString())
					dao.name=mutableMap?.get(DatabaseConfig.name).toString()
					dao.recommendedMedicines=mutableMap?.get(DatabaseConfig.medicines).toString()
					dao.precautions=mutableMap?.get(DatabaseConfig.precautions) as ArrayList<String>
					dao.symptoms= mutableMap[DatabaseConfig.symptoms] as ArrayList<String>
					dao.futureSteps= mutableMap[DatabaseConfig.futureSteps] as ArrayList<String>
					success(dao)

				}
				else
				{
					failure(EmptyResultException())
				}
			},failure)
	}



}
