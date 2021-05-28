package com.example.CropMedic.FileChooserActivity.FileChooser
import android.content.Intent

/**
 * @param {fileType} String
 * */
//Trigger the system file chooser
class FileChooser(fileType:String) {

    val fileType=fileType
    fun getFileIntent():Intent{
        val tempIntent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply { setType(fileType) }
        return  Intent.createChooser(tempIntent,"Select a file")
    }
}
