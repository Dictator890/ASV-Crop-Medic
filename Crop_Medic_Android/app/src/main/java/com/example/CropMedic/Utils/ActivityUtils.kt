package com.example.CropMedic.Utils

import android.content.Intent
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.CropMedic.ErrorActivity

class ActivityUtils {
	companion object{
		 fun hideActionBar(activity:AppCompatActivity){
			activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
			activity.supportActionBar?.hide()
			@Suppress("DEPRECATION")
			if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
				activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
			}
			else
			{
				activity.window.setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN
				)
			}
		}
		fun triggerErrorActivity(activity : AppCompatActivity){
			activity.startActivity(Intent(activity.baseContext,ErrorActivity::class.java))
		}
	}
}