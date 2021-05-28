package com.example.CropMedic.Network

import android.util.Log
import com.example.CropMedic.Utils.DetailsDAO
import com.example.CropMedic.Utils.EmptyResultException

class Database {
	companion object{
		private val firebaseCommunication=FirebaseCommunication()
		private  val TAG="Database"

	}
	fun getWeights(category:String,success:(HashMap<String,FloatArray>)->Unit,failure:(Exception)->Unit){
		firebaseCommunication.getsingleCollection(FirebaseConfig.collectionName,FirebaseConfig.categoryName,category,{ it ->
			val map= HashMap<String,FloatArray>()
			if (it != null) {
				it.forEach {
					map[it.get(DatabaseConfig.name) as String] = (it.get(DatabaseConfig.Weights) as ArrayList<Float>).toFloatArray()
				}
				success(map)
			}
			else
			{
				failure(EmptyResultException())
			}
		},{exception:Exception ->
			failure(exception)
		})

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
					dao.decodeBase64(mutableMap?.get(DatabaseConfig.DefaultPicture).toString())
					Log.d(TAG,"Map data keys: ${mutableMap?.keys.toString()}")
					dao.name=mutableMap?.get(DatabaseConfig.name).toString()
					dao.recommendedMedicines=mutableMap?.get(DatabaseConfig.medicines) as? ArrayList<String>
					dao.precautions=mutableMap?.get(DatabaseConfig.precautions) as ArrayList<String>
					dao.symptoms= mutableMap?.get(DatabaseConfig.symptoms).toString()
					dao.futureSteps= mutableMap?.get(DatabaseConfig.futureSteps) as? ArrayList<String>
					success(dao)
				}
				else
				{
					failure(EmptyResultException())
				}
			},failure)
	}





}
