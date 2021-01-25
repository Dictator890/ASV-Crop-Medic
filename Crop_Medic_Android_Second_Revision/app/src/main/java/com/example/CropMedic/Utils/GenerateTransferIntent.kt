package com.example.CropMedic.Utils

import android.content.Intent

class GenerateTransferIntent{
    companion object {
        fun generateStringIntent(Name:String,data:String):Intent{
            return Intent().apply { putExtra(Name,data) }

        }
        fun generateIntegerIntent(Name:String,data:Int):Intent{
            return Intent().apply { putExtra(Name,data) }

        }
        fun generateBooleanIntent(Name : String,data:Boolean):Intent{
            return Intent().apply { putExtra(Name,data) }
        }



    }
}
