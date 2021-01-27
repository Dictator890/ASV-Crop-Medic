package com.example.CropMedic.CameraUsage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.CropMedic.R
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.CropMedic.Utils.AppConstants
import com.example.CropMedic.Utils.GenerateTransferIntent
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    companion object{
        private val requiredPermissionsforCameraActivity= arrayOf(Manifest.permission.CAMERA)  //Permission Array consisting of all the permission
        private val TAG="CameraActivity"         // TAG required for Logging
    }
    private lateinit var imagecapture:ImageCapture      //When the image is captured using CameraX it is generated as ImageCapture Object
    private lateinit var cameraProvider: ProcessCameraProvider //It provides the camera access
    private lateinit var cameraExecutor:ExecutorService        //Executes operations on Camera
    private val cameraNumber=CameraSelector.DEFAULT_BACK_CAMERA  //Stores the Camera to be used
    private  lateinit var cameraProviderInstance:ListenableFuture<ProcessCameraProvider>  //This listenablefuture will generate the Camera Access Provider in the Future when Camera is Avaliable
    private  lateinit  var previewSurface:Preview                                          //A surface to see the preview of Camera
    private lateinit var previewViewElement : PreviewView                     // The preview view element on the layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_camera) // Display the layout

        previewViewElement=findViewById(R.id.cameraPreviewSurface)
        if (allPermissionsGranted()){
            requestSuccess()    // If all permissions are granted execute the next functions
        }
        else
        {
            requestPermissions()     //If no permissions are granted them ask the user to give them
        }
    }
    //Check if all permissions are granted and returns a result true if all are true
    private fun allPermissionsGranted()= requiredPermissionsforCameraActivity.all { iterator->ContextCompat.checkSelfPermission(baseContext,iterator)==PackageManager.PERMISSION_GRANTED}

    //Request the permissions that are not given
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, requiredPermissionsforCameraActivity,
            AppConstants.CAMERA_PERMISSION_REQUEST)
    }

    //This function is fired when all the results of permission are generated whether it be 0 or 1
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == AppConstants.CAMERA_PERMISSION_REQUEST){
            if(allPermissionsGranted()){
               requestSuccess()      //All permissions are granted
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdownNow()  //Shutdown the Camera Stream after this activity is closed
    }

    //Intializes the objects required by the Camera like the cameraprovider,cameraExecutor
    private fun initializeCamera(){
        Toast.makeText(this,"Camera Initialized",Toast.LENGTH_LONG).show()
        cameraProviderInstance=ProcessCameraProvider.getInstance(this)
        cameraProvider=cameraProviderInstance.get()
        previewSurface=Preview.Builder().build().also { it.setSurfaceProvider(cameraPreviewSurface.surfaceProvider)}
        imagecapture=ImageCapture.Builder().build()
        cameraExecutor=Executors.newSingleThreadExecutor()



    }
    //Binds the cameraAccess generator with the surface
    private fun CameraBinder(cameraProviderinstance:ListenableFuture<ProcessCameraProvider>,cameraprovider:ProcessCameraProvider,displaySurface:Preview,cameraFace:CameraSelector,currentContext:Context,currentLifeCycleOwner:LifecycleOwner,imgCapture:ImageCapture){
        cameraProviderinstance.addListener(Runnable {
                try{
                    cameraprovider.unbindAll()
                   val camera=cameraprovider.bindToLifecycle(currentLifeCycleOwner,cameraFace,displaySurface,imgCapture)
                    cameraUtilities(camera,previewViewElement)
                }
                catch (exception:Exception){
                    Toast.makeText(currentContext,"An error occured while binding the camera",Toast.LENGTH_LONG).show()
                    Log.e(TAG,exception.message.toString())

                }
        },ContextCompat.getMainExecutor(currentContext))
    }
    //Carry out operations when all permissions are given
    private fun requestSuccess()
    {
        setContentView(R.layout.activity_camera)
        initializeCamera()
        CameraBinder(cameraProviderInstance,cameraProvider,previewSurface,cameraNumber,this,this,imagecapture)
        val click=findViewById<ImageButton>(R.id.Capture)
        click.setOnClickListener {
            CaptureImage(imagecapture)
        }
    }


//This function returns the application directory for  storage of the clicked picture
  private fun getAppDir(): File? {
      val temp_save_dir=externalMediaDirs.firstOrNull()?.let {
          File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
      return  if( temp_save_dir != null && temp_save_dir.exists())
          temp_save_dir
      else
          null
  }
    //This function captures the image and saves it to storage
    private fun CaptureImage(imageCapture: ImageCapture){
        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        val op_dir=getAppDir()
        val photoFile=File(op_dir, SimpleDateFormat(FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpg")
        val opOptions=ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(opOptions,ContextCompat.getMainExecutor(this),object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG,"Could not save the image")
                Log.e(TAG,exception.toString())
                setResult(Activity.RESULT_CANCELED, GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,""))
                finish()
            }
    
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri=Uri.fromFile(photoFile)
                setResult(Activity.RESULT_OK, GenerateTransferIntent.generateStringIntent(AppConstants.INTENT_CALL,savedUri.toString()))

                finish()
            }
        })
    }
    //Camera utilities to add tap to focus
    private fun cameraUtilities(cameraProvider:Camera,viewSurface:PreviewView){
        val cameraControl=cameraProvider.cameraControl
        viewSurface.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP->{
                    val point=viewSurface.meteringPointFactory.createPoint(event.x,event.y)
                    val action=FocusMeteringAction.Builder(point).build()
                    cameraControl.startFocusAndMetering(action)
                    return@setOnTouchListener  true
                }
                else -> return@setOnTouchListener false
            }
        }

    }

}