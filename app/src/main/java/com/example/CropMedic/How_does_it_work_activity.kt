package com.example.CropMedic

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.CropMedic.LocalData.LocalConstantData
import kotlinx.android.synthetic.main.activity_how_does_it_work.*

class How_does_it_work_activity : AppCompatActivity() {


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_how_does_it_work)
		title="How do I recognize"

		howdoesitworklist.adapter=Howitworkadapter(applicationContext,
			LocalConstantData().gethowitwork_data().toTypedArray()
		)
	}
}
class Howitworkadapter(private val ctx:Context , val data:Array<String>):BaseAdapter(){


	override fun getCount() : Int {
		return data.size
	}

	override fun getItem(position : Int) : Any? {
		return  null
	}

	override fun getItemId(position : Int) : Long {
		return 0
	}

	override fun getView(position : Int , convertView : View? , parent : ViewGroup?) : View? {
		var finalView = convertView
		if(finalView == null){
			finalView=LayoutInflater.from(ctx).inflate(R.layout.how_does_it_work_single_element,null)
		}
		val tv=finalView?.findViewById<TextView>(R.id.how_does_it_work_single_text)
		val num=finalView?.findViewById<TextView>(R.id.number)
		tv?.text=data[position]
		num?.text= (position+1).toString()
		return finalView
	}

}