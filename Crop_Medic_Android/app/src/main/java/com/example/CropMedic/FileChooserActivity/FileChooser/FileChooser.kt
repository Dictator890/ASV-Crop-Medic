package com.example.CropMedic.FileChooserActivity.FileChooser

import android.content.Context
import android.content.Intent

/**
 * @param {context}
 * */
class FileChooser(fileType:String) {

    val fileType=fileType
    fun getFileIntent():Intent{
        val tempIntent=Intent(Intent.ACTION_GET_CONTENT).apply { setType(fileType) }
        return  Intent.createChooser(tempIntent,"Select a file")
    }
}
