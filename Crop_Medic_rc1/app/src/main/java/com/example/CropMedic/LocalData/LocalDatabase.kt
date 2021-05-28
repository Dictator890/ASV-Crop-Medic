package com.example.CropMedic.LocalData

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.room.Database
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class LocalDatabase(applicationContext : Context):ViewModel() {

	companion object{
		fun  getInstance(applicationContext:Context):LocalDatabase{
			return LocalDatabase(applicationContext)
		}
	}
	private val db=Room.databaseBuilder(applicationContext,AppDatabase::class.java,"crop_medic").build()
	private val historydao=db.historydao()
	fun getAllHistory(onSucess:(List<HistoryEntity>)->Unit){
		viewModelScope.launch {
			val history= historydao.getHistory()
			MainScope().launch {
				onSucess(history)
			}
		}
	}
	fun deleteAllHistory(){
		viewModelScope.launch {historydao.deleteTableData()}
	}
	fun addtoHistory(predictLabel : String?){
		viewModelScope.launch {
			val currentDate=Calendar.getInstance().timeInMillis
			val entity=HistoryEntity(predictLabel,currentDate)
			historydao.addHistory(entity)}
	}
}

@Entity(tableName = "cropmedichistory")
data class HistoryEntity(
	@ColumnInfo(name="predict_label")val predictLabel:String?,
	@ColumnInfo(name="timestamp") val timestamp:Long
){
	@PrimaryKey(autoGenerate = true)var id:Int=0
}

@Dao
interface HistoryDao{
	@Query("SELECT * FROM cropmedichistory order by timestamp desc")
	suspend fun getHistory():List<HistoryEntity>

	@Insert
	suspend fun addHistory(vararg  history:HistoryEntity)

	@Query("DELETE from cropmedichistory")
	suspend fun deleteTableData()
}

@Database(entities = arrayOf(HistoryEntity::class),version = 1)
abstract  class AppDatabase:RoomDatabase(){
	abstract fun historydao():HistoryDao
}