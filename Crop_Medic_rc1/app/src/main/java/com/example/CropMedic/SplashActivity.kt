package com.example.CropMedic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.CropMedic.Utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityUtils.hideActionBar(this)
		setContentView(R.layout.activity_splash)
		val displayWidth=(this.resources.displayMetrics.widthPixels/1.5).toInt()
		val marginHeight=this.resources.displayMetrics.heightPixels/10

		var constraints=SplashImage.layoutParams as ConstraintLayout.LayoutParams
		constraints.leftMargin=0
		constraints.rightMargin=0
		constraints.topMargin=marginHeight
		constraints.width=displayWidth
		constraints.height= displayWidth
		SplashImage.layoutParams=constraints

		constraints=titleText.layoutParams as ConstraintLayout.LayoutParams
		constraints.bottomMargin=marginHeight
		titleText.layoutParams=constraints

		MainScope().launch {
			delay(1500)
			startActivity(Intent(applicationContext,ModelDownloadActivity::class.java))
			finish()
		}
	}
}