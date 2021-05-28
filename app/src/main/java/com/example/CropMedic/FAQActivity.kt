package com.example.CropMedic

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.CropMedic.LocalData.LocalConstantData
import kotlinx.android.synthetic.main.activity_faqactivity.*

class FAQActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_faqactivity)
		title="FAQ"

		faqlist.adapter=FAQAdapter(this)
	}
}
class FAQAdapter(val ctx:Context):BaseAdapter(){
		val  data=LocalConstantData().getFAQ();
		val keys=data.keys.toList()

	override fun getCount() : Int {
		return  keys.size;
	}

	override fun getItem(position : Int) : Any? {
		return null
	}

	override fun getItemId(position : Int) : Long {
		return  0L
	}

	@SuppressLint("InflateParams")
	override fun getView(position : Int , convertView : View? , parent : ViewGroup?) : View? {
		var finalview=convertView
		if(finalview == null){
			finalview=LayoutInflater.from(ctx).inflate(R.layout.faqsingle,null)
		}
		val num=finalview?.findViewById<TextView>(R.id.number)
		val question=finalview?.findViewById<TextView>(R.id.question)
		val answer=finalview?.findViewById<TextView>(R.id.answer)

		num?.text=(position+1).toString()
		question?.text=keys[position]
		answer?.text=data.get(keys[position])

		return  finalview


	}

}