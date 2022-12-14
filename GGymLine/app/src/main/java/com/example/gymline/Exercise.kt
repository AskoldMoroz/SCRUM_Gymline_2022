package com.example.gymline

import android.graphics.Bitmap

open class Exercise(var exTitle: String?, var exImg: Bitmap?, var exDesc: String?, var exId: String){
}

open class ExerciseShort(val exTitleShort: String? = null, val exDescShort: String? = null)