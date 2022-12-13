package com.example.gymline

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymline.databinding.ActivityCourseCreatorBinding
import com.example.gymline.databinding.ActivityExerciseCreatorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class CourseCreator : AppCompatActivity() {

    lateinit var binding : ActivityCourseCreatorBinding
    lateinit var auth: FirebaseAuth

    private lateinit var dialog: Dialog

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var imageUri: Uri

    private var isImgSet = false

    private val adapter = CourseAdapter()

    lateinit var unfinishedCourse: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseCreatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btmMenu.menu.getItem(3).isChecked = true

        //checkifunfinishedcourseexist

        databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val children = snapshot!!.children
                    children.forEach {
                        val course = it.getValue(Course::class.java)
                        val id = it.key!!.toString()

                        if(course!!.author == auth.currentUser!!.displayName) {
                            val dialogClickListener =
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            val i = Intent(this@CourseCreator, ExerciseCreator::class.java)
                                            i.putExtra("currCourseId", id)
                                            i.putExtra("currCourseName", course.title)
                                            startActivity(i)
                                            finish()
                                        }
                                        DialogInterface.BUTTON_NEGATIVE -> {

                                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$id/Exercises")
                                            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onCancelled(snapshotError: DatabaseError) {
                                                }

                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val children = snapshot!!.children
                                                    children.forEach {


                                                        val imgId = it.key!!.toString()


                                                        FirebaseStorage.getInstance().getReference("UnfinishedCourses/$id/Exercises/$imgId.jpg").delete()


                                                    }

                                                }
                                            })

                                            Thread.sleep(1500)

                                            showProgressBar()

                                            FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses").child(id).removeValue()
                                            FirebaseStorage.getInstance().getReference("UnfinishedCourses").child("$id/$id.jpg").delete()

                                            hideProgressBar()

                                        }
                                    }
                                }

                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(this@CourseCreator)
                            builder.setMessage("Seems you have a course preview with no exercises. Would you like to fill it?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton(
                                    "No, delete the previous one",
                                    dialogClickListener
                                ).setCancelable(false).show()
                        }
                    }

                }
                else{
                    Toast.makeText(
                        baseContext, "cringe",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })

        //finish




        val spinnerDiff: Spinner = binding.spinnerDiff
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.trainDifficulties,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerDiff.adapter = adapter
        }

        val spinnerType: Spinner = binding.spinnerType
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.trainTypes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerType.adapter = adapter
        }

        binding.btmMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profile_item -> {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
                R.id.logout -> {
                    if (!InternetConn.internetIsConnected()){
                        Toast.makeText(
                            baseContext, "No internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        auth.signOut()
                        val i = Intent(this, signInActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                }
                R.id.courses -> {
                    val i = Intent(this, CoursesActivity::class.java)
                    startActivity(i)
                    finish()

                }
                R.id.courseAdd -> {
                    Toast.makeText(this@CourseCreator,
                        "Current page: Course Add", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        binding.apply {
            courseNameBtn.setOnClickListener {
                if(inputCourseName.text.toString().trim() != ""){
                    courseDiffTypeChooseCW.visibility = View.VISIBLE
                    courseDiffTypeChooseCW.startAnimation(AnimationUtils.loadAnimation(this@CourseCreator, R.anim.searchappear))
                    srclView.post { srclView.fullScroll(View.FOCUS_DOWN) }
                    spinnerDiff.requestFocus()
                    inputCourseName.isEnabled = false
                    courseNameBtn.isEnabled = false
                    courseNameCW.alpha = 0.6f
                    courseNameBtn.isEnabled = false
                }
                else {
                    Toast.makeText(this@CourseCreator,
                    "Course' name can't be empty!", Toast.LENGTH_SHORT).show()
                }

            }
            courseDiffTypeBtn.setOnClickListener {
                courseDescCW.visibility = View.VISIBLE
                courseDescCW.startAnimation(AnimationUtils.loadAnimation(this@CourseCreator, R.anim.searchappear))
                srclView.post { srclView.fullScroll(View.FOCUS_DOWN) }
                courseDescInput.requestFocus()
                spinnerType.isEnabled = false
                spinnerDiff.isEnabled = false
                courseDiffTypeBtn.isEnabled = false
                courseDiffTypeBackBtn.isEnabled = false
                courseDiffTypeChooseCW.alpha = 0.6f
            }
            courseDescBtn.setOnClickListener {
                if(courseDescInput.text.toString().trim() != ""){
                    couseImgCW.visibility = View.VISIBLE
                    couseImgCW.startAnimation(AnimationUtils.loadAnimation(this@CourseCreator, R.anim.searchappear))
                    srclView.post {srclView.fullScroll(View.FOCUS_DOWN) }
                    courseImgViewSelector.requestFocus()
                    courseDescInput.isEnabled = false
                    courseDescBtn.isEnabled = false
                    courseDescBackBtn.isEnabled = false
                    courseDescCW.alpha = 0.6f
                }
                else{
                    Toast.makeText(this@CourseCreator,
                        "Course' description can't be empty!", Toast.LENGTH_SHORT).show()
                }

            }
            courseImgBackBtn.setOnClickListener {
                Thread.sleep(200)
                couseImgCW.visibility = View.GONE
                courseDescInput.requestFocus()
                courseDescInput.isEnabled = true
                courseDescBtn.isEnabled = true
                courseDescBackBtn.isEnabled = true
                courseDescCW.alpha = 1f
            }
            courseDescBackBtn.setOnClickListener {
                Thread.sleep(200)
                courseDescCW.visibility = View.GONE
                spinnerDiff.requestFocus()
                spinnerType.isEnabled = true
                spinnerDiff.isEnabled = true
                courseDiffTypeBtn.isEnabled = true
                courseDiffTypeBackBtn.isEnabled = true
                courseDiffTypeChooseCW.alpha = 1f
            }
            courseDiffTypeBackBtn.setOnClickListener {
                Thread.sleep(200)
                courseDiffTypeChooseCW.visibility = View.GONE
                inputCourseName.requestFocus()
                inputCourseName.isEnabled = true
                courseNameBtn.isEnabled = true
                courseNameCW.alpha = 1f
            }
            courseImgViewSelector.setOnClickListener{

                selectImage()

            }

            courseImgBtn.setOnClickListener{
                if(isImgSet) {
                    courseNameCW.visibility = View.GONE
                    courseDiffTypeChooseCW.visibility = View.GONE
                    courseDescCW.visibility = View.GONE
                    couseImgCW.visibility = View.GONE
                    coursePreviewCW.visibility = View.VISIBLE

                    adapter.clear()

                    init()

                    greetingTitle2.text = "Check if you like the course preview below"
                    greetingsTitle.visibility = View.GONE
                }
                else{
                    Toast.makeText(this@CourseCreator,
                        "Course preview image must be uploaded", Toast.LENGTH_SHORT).show()
                }
            }
            coursePreviewBackBtn.setOnClickListener{
                coursePreviewCW.visibility = View.GONE
                courseNameCW.visibility = View.VISIBLE
                courseDiffTypeChooseCW.visibility = View.VISIBLE
                courseDescCW.visibility = View.VISIBLE
                couseImgCW.visibility = View.VISIBLE

                greetingTitle2.text = "Welcome to the Course Creator!"
                greetingsTitle.visibility = View.VISIBLE
                srclView.post{srclView.fullScroll(View.FOCUS_DOWN) }
            }

            coursePreviewBtn.setOnClickListener {
                val course = Course(adapter.courseList[0].title, adapter.courseList[0].author, adapter.courseList[0].desc, adapter.courseList[0].difficulty, adapter.courseList[0].type)

                databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses")
                databaseReference.child("${adapter.courseList[0].id}").setValue(course).addOnCompleteListener{
                    if(it.isSuccessful){
                        if(this@CourseCreator::imageUri.isInitialized) uploadProfilePic(adapter.courseList[0].id)

                        Toast.makeText(this@CourseCreator,
                            "Success", Toast.LENGTH_SHORT).show()

                        val i = Intent(this@CourseCreator, ExerciseCreator::class.java)
                        i.putExtra("currCourseId", adapter.courseList[0].id)
                        i.putExtra("currCourseName", course.title)
                        startActivity(i)
                        finish()
                    }
                    else{
                        Toast.makeText(this@CourseCreator,
                            "Please try again", Toast.LENGTH_SHORT).show()

                        val i = Intent(this@CourseCreator, CoursesActivity::class.java)
                        startActivity(i)
                        finish()

                    }
                }
            }
        }

    }

    private fun init(input: String?= null, searchFilter: String? = null){
        binding.apply {
            coursePreviewRcView.layoutManager = LinearLayoutManager(this@CourseCreator)
            coursePreviewRcView.adapter = adapter

            val bitmap = (courseImgViewSelector.getDrawable() as BitmapDrawable).getBitmap()

            val fullCourse =
                FullCourse(inputCourseName.text.toString().trim(), auth.currentUser!!.displayName.toString(), courseDescInput.text.toString().trim(),
                    spinnerDiff.selectedItem.toString(), spinnerType.selectedItem.toString(), auth.uid.toString() + inputCourseName.text.toString().trim(), bitmap)

            adapter.addCourse(fullCourse)



        }

        adapter.setOnItemClickListener(object: CourseAdapter.OnItemClickListener{
            override fun onItemClick(fullCourse: FullCourse, item: View) {


            }

        })



    }



    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!

            binding.courseImgViewSelector.setImageURI(imageUri)

            isImgSet = true
        }
    }
    private fun uploadProfilePic(imgId: String){

        storageReference = FirebaseStorage.getInstance().getReference("UnfinishedCourses/$imgId/$imgId.jpg")
        storageReference.putFile(imageUri).addOnSuccessListener {

        }.addOnFailureListener{

            Toast.makeText(this@CourseCreator,
                "Failed to upload the image", Toast.LENGTH_SHORT).show()
        }


    }
    private fun showProgressBar(){
        dialog = Dialog(this@CourseCreator)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        if (!this@CourseCreator.isFinishing) {
            dialog.show()
        }

    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}