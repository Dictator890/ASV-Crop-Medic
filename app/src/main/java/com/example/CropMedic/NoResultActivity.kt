package com.example.CropMedic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.CropMedic.Utils.ActivityUtils

class NoResultActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState : Bundle?) {
		ActivityUtils.hideActionBar(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_no_result)
	}
}