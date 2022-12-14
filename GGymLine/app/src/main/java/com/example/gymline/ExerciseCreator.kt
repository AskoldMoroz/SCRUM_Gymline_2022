package com.example.gymline

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymline.databinding.ActivityExerciseCreatorBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ExerciseCreator : AppCompatActivity() {

    lateinit var binding : ActivityExerciseCreatorBinding

    private val adapter = ExerciseAdapter()

    private lateinit var imageUri: Uri
    private var isImgSet = false

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseCreatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        if(binding.rcViewEx.isEmpty()){
            binding.textView14.visibility = View.VISIBLE
        }

        if(adapter.exerciseList.isEmpty()){
            binding.apply {
                descriptionCW.visibility = View.GONE
                exAddCW.visibility = View.VISIBLE
                exOpenAddingBtn.isEnabled = false
            }
        }

        binding.exOpenAddingBtn.setOnClickListener {
            binding.apply {
                descriptionCW.visibility = View.GONE
                exAddCW.visibility = View.VISIBLE
                exOpenAddingBtn.isEnabled = false
                scView.post{scView.fullScroll(View.FOCUS_UP)}

            }

        }


    }

    override fun onBackPressed() {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val i = Intent(this@ExerciseCreator, CoursesActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {

                    }
                }
            }

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this@ExerciseCreator)
        builder.setMessage("You are gonna be redirected to the CourseFinder page")
            .setPositiveButton("OK", dialogClickListener)
            .setNegativeButton(
                "No, I want to stay",
                dialogClickListener
            ).setCancelable(false).show()
    }

    private fun init(){

        val tempId = intent.getStringExtra("currCourseId")
        var tempTitle = intent.getStringExtra("currCourseName").toString()
        if (intent.getStringExtra("currCourseName").toString().length > 15) {
            tempTitle = tempTitle.subSequence(0, 15).toString().plus("...")
        }

        binding.allCourseExLabel.text = "All '$tempTitle' course exercises: "

        adapter.exerciseList.clear()
        binding.apply {
            rcViewEx.layoutManager = LinearLayoutManager(this@ExerciseCreator)
            rcViewEx.adapter = adapter

            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/${tempId}/Exercises")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val children = snapshot!!.children
                        children.forEach {
                            val exerciseShort = it.getValue(ExerciseShort::class.java)
                            val id = it.key!!.toString()

                            storageReference = FirebaseStorage.getInstance().reference.child("UnfinishedCourses/${tempId}/Exercises/${id}.jpg")
                            val localFile = File.createTempFile("tempImage", "jpg")

                            storageReference.getFile(localFile).addOnSuccessListener {

                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)


                                val exercise =
                                    Exercise(exerciseShort!!.exTitleShort, bitmap, exerciseShort.exDescShort, id)

                                adapter.addCourse(exercise)

                            }


                        }
                    }
                }
            })

        }


        adapter.setOnItemClickListener(object: ExerciseAdapter.OnItemClickListener{
            override fun onItemClick(exercise: Exercise, item: View) {
                binding.apply {
                    exAddCW.visibility = View.GONE
                    descriptionCW.visibility = View.GONE
                    exTitle.text = exercise.exTitle
                    exImgView.setImageBitmap(exercise.exImg)
                    exDesc.text = exercise.exDesc
                    descriptionCW.visibility = View.VISIBLE
                    exOpenAddingBtn.isEnabled = true
                    scView.post{scView.fullScroll(View.FOCUS_DOWN)}

                }
            }

        })

        binding.exImgViewSelector.setOnClickListener{

            selectImage()

        }


        binding.exAddBtn.setOnClickListener {
            if(binding.inputExName.text.trim().isNotEmpty() && binding.exDescInput.text!!.trim().isNotEmpty() && isImgSet){
                val shortEx = ExerciseShort(binding.inputExName.text.toString().trim(), binding.exDescInput.text.toString().trim())
                var i = 0
                while(adapter.exerciseList.size > i){
                    if(adapter.exerciseList[i].exTitle == shortEx.exTitleShort){
                        break
                    }
                    i++
                }
                if(i == adapter.exerciseList.size){
                    databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Exercises")
                    databaseReference.child("${shortEx.exTitleShort}").setValue(shortEx).addOnCompleteListener{
                        if(it.isSuccessful){
                            if(this@ExerciseCreator::imageUri.isInitialized) uploadProfilePic(shortEx.exTitleShort.toString())

                            Toast.makeText(this@ExerciseCreator,
                                "Success", Toast.LENGTH_SHORT).show()

                            val i = Intent(this@ExerciseCreator, ExerciseCreator::class.java)
                            i.putExtra("currCourseId", tempId)
                            i.putExtra("currCourseName", tempTitle)

                            showProgressBar()
                            Handler().postDelayed(Runnable {

                                hideProgressBar()
                                startActivity(i)
                                finish()

                            }, 2000)


                        }
                        else{
                            Toast.makeText(this@ExerciseCreator,
                                "Please try again", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
                else{
                    Toast.makeText(this@ExerciseCreator,
                        "Exercise with this title is already exists", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@ExerciseCreator,
                    "All fields must be filled", Toast.LENGTH_SHORT).show()
            }


        }
        binding.removeExBtn.setOnClickListener {
            FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Exercises").child(binding.exTitle.text.toString()).removeValue()
            FirebaseStorage.getInstance().getReference("UnfinishedCourses/$tempId/Exercises").child("/${binding.exTitle.text.toString()}.jpg").delete()

            val i = Intent(this@ExerciseCreator, ExerciseCreator::class.java)
            i.putExtra("currCourseId", tempId)
            i.putExtra("currCourseName", tempTitle)

            showProgressBar()
            Handler().postDelayed(Runnable {

                hideProgressBar()
                startActivity(i)
                finish()

            }, 2000)

        }

        binding.nextStepBtn.setOnClickListener {
            val i = Intent(this@ExerciseCreator, WorkoutsCreator::class.java)
            i.putExtra("currCourseId", tempId)
            i.putExtra("currCourseName", tempTitle)
            startActivity(i)
            finish()
        }

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

            binding.exImgViewSelector.setImageURI(imageUri)

            isImgSet = true
        }
    }

    private fun uploadProfilePic(imgId: String){

        val tempId = intent.getStringExtra("currCourseId")
        if(imgId != ""){
            storageReference = FirebaseStorage.getInstance().getReference("UnfinishedCourses/${tempId}/Exercises/${imgId}.jpg")
            storageReference.putFile(imageUri).addOnSuccessListener {

            }.addOnFailureListener{

                Toast.makeText(this@ExerciseCreator,
                    "Failed to upload the image", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this@ExerciseCreator,
                "Error", Toast.LENGTH_SHORT).show()
        }



    }
    private fun showProgressBar(){
        dialog = Dialog(this@ExerciseCreator)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        if (!this@ExerciseCreator.isFinishing) {
            dialog.show()
        }

    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }

}