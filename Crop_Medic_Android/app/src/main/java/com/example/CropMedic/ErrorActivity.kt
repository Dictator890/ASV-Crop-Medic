package com.example.CropMedic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.CropMedic.Utils.ActivityUtils

class ErrorActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityUtils.hideActionBar(this)
		setContentView(R.layout.activity_error)
	}
}