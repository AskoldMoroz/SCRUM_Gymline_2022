package com.example.gymline

import android.graphics.Bitmap

open class Course(var title: String? = null, var author: String? = null, var desc: String? = null, var difficulty: String? = null, var type: String? = null){

}
open class FullCourse(title: String?, author: String?, desc: String?, difficulty: String?, type: String?, var id: String, var img: Bitmap) :
    Course(title, author, desc, difficulty, type)






