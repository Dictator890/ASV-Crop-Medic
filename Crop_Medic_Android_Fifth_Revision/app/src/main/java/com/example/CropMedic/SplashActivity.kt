package com.example.CropMedic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.CropMedic.Utils.ActivityUtils
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
companion object{
	private const val TAG="SplashActivity"
}

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityUtils.hideActionBar(this)
		setContentView(R.layout.activity_splash)
		MainScope().launch {
			delay(1500)
			val remoteModel=FirebaseCustomRemoteModel.Builder(getString(R.string.firebaseModelName)).build()
			FirebaseModelManager.getInstance().isModelDownloaded(remoteModel).addOnSuccessListener {
				if(it){
					startActivity(Intent(applicationContext,MainActivity::class.java))
					finish()
				}
				else
				{
					startActivity(Intent(applicationContext,ModelDownloadActivity::class.java))
					finish()
				}
			}.addOnFailureListener {
				startActivity(Intent(applicationContext,ModelDownloadActivity::class.java))
				Log.e(TAG,it.toString())
			}
		}
	}
}