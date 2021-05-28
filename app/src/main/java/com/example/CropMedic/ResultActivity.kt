package com.example.CropMedic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.CropMedic.Network.Database
import com.example.CropMedic.LocalData.LocalDatabase
import com.example.CropMedic.Utils.ActivityUtils
import com.example.CropMedic.Utils.DetailsDAO
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    private  var label:String? = null
   private lateinit var database:Database
    companion object{
        private const val TAG="ResultActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)  {
          title="Result"
          database=Database()
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_result)

         try{
             label= intent.getStringExtra("Label")
             label?.let { processtoSucess(it) }
            LocalDatabase.getInstance(applicationContext).addtoHistory(label)
         }catch (e:Exception){
             Log.e(TAG,e.toString())
             ActivityUtils.triggerErrorActivity(this)
             finish()
         }

    }
    private fun processtoSucess(name:String){
        database.getOfflineInformation(name,{detailsDAO ->setDetailstoGUI(detailsDAO)},
            {exception ->
            ActivityUtils.triggerErrorActivity(this)
            Log.d(TAG,exception.toString()) })
    }

    private fun setDetailstoGUI(detailsDAO : DetailsDAO){
        categoryData.text=detailsDAO.category
        addArraytoGUI( detailsDAO.recommendedMedicines,medicinesData)
        symptomsData.text=detailsDAO.symptoms
        addArraytoGUI(detailsDAO.precautions,precautionsData)
        addArraytoGUI(detailsDAO.futureSteps,futurestepsData)
        Log.d(TAG, (detailsDAO.defaultPicture == null).toString())
        imageDisplay?.setImageBitmap(detailsDAO.defaultPicture)
        nameField?.text=detailsDAO.name

    }
    @SuppressLint("SetTextI18n")
    private fun addArraytoGUI(array:ArrayList<String>? , element:TextView){
        if (array != null) {
            if(array.isNotEmpty()){
                if(array.size == 1){
                    element.text= array.get(0)
                } else {
                    var counter=1
                    element.text=""
                    array.forEach {
                        element.text= element.text.toString() +"$counter : $it \n"
                        counter++
                    }
                }
            }
        }
        else{
            element.text="Unable to retrive the data"
        }


    }


}