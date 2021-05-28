package com.example.CropMedic

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.CropMedic.LocalData.LocalConstantData
import com.example.CropMedic.Utils.ActivityUtils
import com.example.CropMedic.Utils.AppConstants

class PlantChooserActivity : AppCompatActivity() {

//Start Processing Activity after this
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_plant_chooser)
		title="Select a Plant Category"

		val grid=findViewById<GridView>(R.id.plant_chooser_grid)

	PlantChooserGrid(applicationContext) { value ->
		val intent = Intent(applicationContext , ProcessingActivity::class.java)
		intent.putExtra(AppConstants.PLANT_NAME,value)
		intent.putExtra(AppConstants.TRANSFER_DATA , this.intent.getStringExtra(AppConstants.TRANSFER_DATA).toString())
		startActivity(intent)
		finish()
	}.also { grid.adapter = it }

	}
	class PlantChooserGrid(val ctx : Context , val onButtonClick:(String)->Unit) :BaseAdapter(){
		val data= LocalConstantData().get_plantcategories()
		val key=data.keys.toTypedArray()
		override fun getCount() : Int {
			Log.d("PlantChooser","Size of data :${data.size}")
			return data.size
		}

		override fun getItem(position : Int) : Any? {
			return  null
		}

		override fun getItemId(position : Int) : Long {
			return 0L
		}

		override fun getView(position : Int , convertView : View? , parent : ViewGroup?) : View? {
			var final_view=convertView
			if(convertView === null){
				final_view=LayoutInflater.from(this.ctx).inflate(R.layout.plantchoosergrid,parent,false)
			}
			val root = final_view?.findViewById<LinearLayout>(R.id.plantchooser_grid_root)
			val text= final_view?.findViewById<TextView>(R.id.plantsinglegridtext)
			val image=final_view?.findViewById<ImageView>(R.id.plantsingleimageview)

			data[key[position]]?.let { image?.setImageResource(it) }
			text?.text= key[position]

			root?.setOnClickListener {
				onButtonClick(key[position])
			}
			return final_view
		}

	}
}