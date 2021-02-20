package com.example.CropMedic.Network

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseCommunication() {
	private val TAG="FirebaseCommunication"
	val firebaseDatabase=Firebase.firestore
	init
	{

		Log.d("FirebaseCommunication", firebaseDatabase.toString())
		firebaseDatabase.firestoreSettings=FirebaseFirestoreSettings.Builder().setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).setPersistenceEnabled(true).build()
	}


	fun getsingleCollection(collectionName:String,key:String,value:String,success:(QuerySnapshot?)->Unit,failure:(Exception)->Unit)
	{
		firebaseDatabase.collection(collectionName).whereEqualTo(key,value).get().addOnSuccessListener {
			Log.d(TAG,"Success")
			success(it)
		}.addOnFailureListener(failure)
	}

	fun getwholeCollection(collectionName:String,success:(QuerySnapshot?)->Unit,failure:(Exception)->Unit)
	{
		firebaseDatabase.collection(collectionName).get().addOnSuccessListener(success).addOnFailureListener(failure)
	}

	fun getsinglecollectionOffline(name:String,key:String,value:String,success:(QuerySnapshot?)->Unit,failure:(Exception)->Unit){
        Log.d(TAG,"CollectionName:$name Key:$key Value:$value")
		firebaseDatabase.disableNetwork()
		firebaseDatabase.collection(name).whereEqualTo(key,value).get().addOnSuccessListener{
			success(it)
			firebaseDatabase.enableNetwork()
		}.addOnFailureListener{
			failure(it)
			firebaseDatabase.enableNetwork()
		}
	}




}