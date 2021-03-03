package com.example.CropMedic
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.CropMedic.Utils.ActivityUtils
import com.example.CropMedic.Utils.AppConstants
import com.example.CropMedic.Utils.GenerateTransferIntent
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import kotlinx.android.synthetic.main.activity_model_download.*
import kotlinx.coroutines.*

class ModelDownloadActivity : AppCompatActivity() {

companion object{
private val TAG="ModelDownloadActivity"
	private val permissions= arrayOf(Manifest.permission.INTERNET)
	private val permissionsRequestCode:Int=5
	private var backgroundCoroutine= MainScope()
}
	override fun onPause() {
		super.onPause()
		backgroundCoroutine.cancel()
		backgroundCoroutine=MainScope()
	}

	@InternalCoroutinesApi
	override fun onResume() {
		super.onResume()
		startImage(image,activity_model_download_text)
	}
	@InternalCoroutinesApi
	override fun onCreate(savedInstanceState : Bundle?) {
		ActivityUtils.hideActionBar(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_model_download)

		if(!checkifPermissionsGranted()){
			Log.d(TAG,"Permissions not grnated previously")
			requestPermissions()

		}
		else{
			Log.d(TAG,"All permissions have ben granted previously")
			startImage(image,activity_model_download_text)
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
	@InternalCoroutinesApi
	override fun onRequestPermissionsResult(
		requestCode : Int ,
		permissions : Array<out String> ,
		grantResults : IntArray
	) {
		super.onRequestPermissionsResult(requestCode , permissions , grantResults)
		when(requestCode){
			permissionsRequestCode->{
				if(checkifPermissionsGranted()){
					startImage(image,activity_model_download_text)
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
			finish()
		}.addOnFailureListener {
			setResult(AppConstants.MODEL_DOWNLOAD_TRIGGER,GenerateTransferIntent.generateBooleanIntent(AppConstants.MODEL_DOWNLOAD_RESULT,false))
			finish()
		}
	}
	@InternalCoroutinesApi
	private fun startImage(imgview:ImageView,textView:TextView){
		backgroundCoroutine.launch {
			while (true){
				if(isActive){
					imgview.scaleType=ImageView.ScaleType.FIT_END
					textView.text="Downloading."
					delay(500)
					imgview.scaleType=ImageView.ScaleType.FIT_CENTER
					textView.text="Downloading.."
					delay(500)
					imgview.scaleType=ImageView.ScaleType.FIT_START
					textView.text="Downloading..."
					delay(500)
				}
			}
		}

	}
	override fun toString() : String {
		return "ModelDownloadActivity()"
	}
}