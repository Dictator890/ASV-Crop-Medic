package com.example.CropMedic


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.CropMedic.LocalData.LocalDatabase
import com.example.CropMedic.LocalData.HistoryEntity
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_history)
		title="History"

		val adapter=findViewById<ListView>(R.id.historydisplaylistview)
		LocalDatabase.getInstance(applicationContext).getAllHistory { data ->
			adapter.adapter = HistoryDisplayAdapter(applicationContext , data)
		}

	}
}
class HistoryDisplayAdapter(val ctx: Context , val data : List<HistoryEntity>):BaseAdapter(){

	override fun getCount() : Int {
		return data.size
	}

	override fun getItem(position : Int) : Any? {
		return  null
	}

	override fun getItemId(position : Int) : Long {
		return  0
	}

	override fun getView(position : Int , convertView : View? , parent : ViewGroup?) : View? {
		var returnview=convertView
		if(returnview === null){
			returnview=LayoutInflater.from(ctx).inflate(R.layout.historydisplaylistitem,parent,false)
		}
		val labeltv=returnview?.findViewById<TextView>(R.id.historylabel)
		val timestamptv=returnview?.findViewById<TextView>(R.id.historytimestamp)

		val current=data[position]
		labeltv?.text=current.predictLabel
		val calendar=Calendar.getInstance()
		calendar.timeInMillis=current.timestamp
		timestamptv?.text=SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(calendar.time)
		return  returnview

	}

}