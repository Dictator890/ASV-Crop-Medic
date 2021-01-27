package com.example.CropMedic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.CropMedic.Utils.AppConstants
import com.example.CropMedic.Utils.GenerateTransferIntent
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel


class ModelDownloadActivity : AppCompatActivity() {

companion object{
private val TAG="ModelDownloadActivity"
	private val permissions= arrayOf(Manifest.permission.INTERNET)
	private val permissionsRequestCode:Int=5

}

	override fun onCreate(savedInstanceState : Bundle?) {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		supportActionBar?.hide()
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
			@Suppress("DEPRECATION")
			window.setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN ,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			)
		} else {
			window.insetsController?.hide(WindowInsets.Type.statusBars())
		}
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_model_download)

		if(! checkifPermissionsGranted()){
			Log.d(TAG,"Permissions not grnated previously")

			requestPermissions()

		}
		else{
			Log.d(TAG,"All permissions have ben granted previously")
			downloadModel(getString(R.string.firebaseModelName))
		}
	}

	private fun checkifPermissionsGranted()= permissions.all {
		ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
	}
	private fun requestPermissions()
	{

		ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
	}

	override fun onRequestPermissionsResult(
		requestCode : Int ,
		permissions : Array<out String> ,
		grantResults : IntArray
	) {
		super.onRequestPermissionsResult(requestCode , permissions , grantResults)
		when(requestCode){
			permissionsRequestCode->{
				if(checkifPermissionsGranted()){
					Log.d(TAG,"All the permissions have been granted")
					downloadModel(getString(R.string.firebaseModelName))
				}
			}
		}
	}
	private fun downloadModel(modelName:String){
		val remoteModel=FirebaseCustomRemoteModel.Builder(modelName).build()
		val conditions=FirebaseModelDownloadConditions.Builder().build()
		Log.d(TAG,"Remote Model and conditions have been built")
		FirebaseModelManager.getInstance().download(remoteModel,conditions).addOnCompleteListener {
			setResult(AppConstants.MODEL_DOWNLOAD_TRIGGER,GenerateTransferIntent.generateBooleanIntent(AppConstants.MODEL_DOWNLOAD_RESULT,true))
			Toast.makeText(baseContext,"Model Download is Sucessful",Toast.LENGTH_LONG).show()
			finish()
		}.addOnFailureListener {
			setResult(AppConstants.MODEL_DOWNLOAD_TRIGGER,GenerateTransferIntent.generateBooleanIntent(AppConstants.MODEL_DOWNLOAD_RESULT,false))
			Toast.makeText(baseContext,"Model Download is Failed",Toast.LENGTH_LONG).show()
			finish()
		}
	}


	override fun toString() : String {
		return "ModelDownloadActivity()"
	}
}