package com.example.CropMedic
import android.Manifest
import android.content.Intent
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
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
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
			requestPermissions()

		}
		else{
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
		val conditions=CustomModelDownloadConditions.Builder().build()
		Log.d(TAG,"Remote Model and conditions have been built")
		FirebaseModelDownloader.getInstance().getModel(modelName,DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,conditions).addOnSuccessListener {
			startActivity(Intent(applicationContext,MainActivity::class.java))
			finish()
		}.addOnFailureListener {
			startActivity(Intent(applicationContext,MainActivity::class.java))
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