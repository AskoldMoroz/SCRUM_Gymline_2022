package com.example.gymline

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymline.databinding.ActivityScheduleCreatorBinding
import com.example.gymline.databinding.ActivityWorkoutsCreatorBinding
import com.example.gymline.daysAdapters.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.concurrent.thread

class ScheduleCreator : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    private lateinit var imageUri: Uri

    lateinit var binding : ActivityScheduleCreatorBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference25: DatabaseReference

    private lateinit var storageReference: StorageReference

    lateinit var tempId:String
    lateinit var tempTitle:String

    private lateinit var dialog: Dialog

    private lateinit var tempItem: View

    private lateinit var course: Course

    private val adapterWorkouts = WorkoutAdapter()
    private val adapterMonday = MondayAdapter()
    private val adapterTuesday = TuesdayAdapter()
    private val adapterWednesday = WednesdayAdapter()
    private val adapterThursday = ThursdayAdapter()
    private val adapterFriday = FridayAdapter()
    private val adapterSaturday = SaturdayAdapter()
    private val adapterSunday = SundayAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCreatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        initWorkout()

        binding.apply {
            mondayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            mondayRcView.adapter = adapterMonday

            tuesdayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            tuesdayRcView.adapter = adapterTuesday

            wednesdayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            wednesdayRcView.adapter = adapterWednesday

            thursdayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            thursdayRcView.adapter = adapterThursday

            fridayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            fridayRcView.adapter = adapterFriday

            saturdayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            saturdayRcView.adapter = adapterSaturday

            sundayRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            sundayRcView.adapter = adapterSunday
        }


        binding.placeWorkoutCW.isVisible = false

        tempId = intent.getStringExtra("currCourseId")!!
        tempTitle = intent.getStringExtra("currCourseName")!!

        binding.backBtn.setOnClickListener {
            val i = Intent(this@ScheduleCreator, WorkoutsCreator::class.java)
            i.putExtra("currCourseId", tempId)
            i.putExtra("currCourseName", tempTitle)
            startActivity(i)
            finish()

        }

        binding.nextBtn.setOnClickListener {

            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            showProgressBar()
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Monday")
                            var index = 0
                            while(adapterMonday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterMonday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Tuesday")
                            index = 0
                            while(adapterTuesday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterTuesday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Wednesday")
                            index = 0
                            while(adapterWednesday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterWednesday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Thursday")
                            index = 0
                            while(adapterThursday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterThursday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Friday")
                            index = 0
                            while(adapterFriday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterFriday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Saturday")
                            index = 0
                            while(adapterSaturday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterSaturday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/Sunday")
                            index = 0
                            while(adapterSunday.workoutsList.size > index){
                                databaseReference.child(index.toString()).setValue(adapterSunday.workoutsList[index].name).addOnCompleteListener{
                                    if(it.isSuccessful){ }
                                    else{
                                        Toast.makeText(this@ScheduleCreator,
                                            "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                index++
                            }

                            databaseReference25 = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId")

                            databaseReference25.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        course = snapshot.getValue(Course::class.java)!!

                                        databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("Courses")
                                        val nameCourse = auth.uid.toString() + course.title.toString()
                                        databaseReference.child(nameCourse).setValue(course).addOnCompleteListener{
                                            if(it.isSuccessful){
                                                storageReference = FirebaseStorage.getInstance().reference.child("UnfinishedCourses/${auth.uid.toString() + course.title}/${auth.uid.toString() + course.title}.jpg")
                                                val localFile = File.createTempFile("tempImage", "jpg")
                                                storageReference.getFile(localFile).addOnSuccessListener {
                                                    storageReference = FirebaseStorage.getInstance().getReference("Courses/${auth.uid.toString() + course.title}.jpg")
                                                    storageReference.putFile(localFile.toUri()).addOnSuccessListener {

                                                    }.addOnFailureListener{

                                                        Toast.makeText(this@ScheduleCreator,
                                                            "Failed to upload the image", Toast.LENGTH_SHORT).show()
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })


                            fun courseTransferring(day: String){
                                Handler().postDelayed(Runnable {

                                    databaseReference25 = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Schedule/$day")

                                    databaseReference25.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            var workoutsMonday = ArrayList<String>()
                                            val children = snapshot.children   //getValue(workoutsMonday::class.java)
                                            children.forEach{
                                                workoutsMonday.add(it.getValue(String::class.java)!!)
                                            }

                                            for (i in 0 until workoutsMonday.size) {
                                                databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference(
                                                    "UnfinishedCourses/$tempId/Workouts/${workoutsMonday[i]}")
                                                var exerciseSetForDBList = ArrayList<ExerciseSetForDB>()
                                                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if(snapshot.exists()){
                                                            val set = snapshot.children
                                                            set.forEach{
                                                                exerciseSetForDBList.add(it.getValue(ExerciseSetForDB::class.java)!!)
                                                            }

                                                            var exerciseSetList = ArrayList<ExerciseSetNoImage>()

                                                            for (i in 0 until  exerciseSetForDBList.size){
                                                                databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference(
                                                                    "UnfinishedCourses/$tempId/Exercises/${exerciseSetForDBList[i].exerciseName}")
                                                                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        if(snapshot.exists()){
                                                                            exerciseSetList.add(ExerciseSetNoImage(snapshot.getValue(ExerciseShort::class.java)!!, exerciseSetForDBList[i].repeats!!))
                                                                        }
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {}

                                                                })
                                                            }

                                                            Handler().postDelayed(Runnable {
                                                                val fullWorkout = FullWorkout(workoutsMonday[i], exerciseSetList)
                                                                databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("Courses/${tempId}/Schedule/$day")
                                                                databaseReference.child(i.toString()).setValue(fullWorkout).addOnCompleteListener{
                                                                    if(it.isSuccessful){

                                                                    }

                                                                }

                                                            }, 1500)





                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {}

                                                })


                                            }

                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })


                                }, 2000)
                            }

                            courseTransferring("Monday")
                            courseTransferring("Tuesday")
                            courseTransferring("Wednesday")
                            courseTransferring("Thursday")
                            courseTransferring("Friday")
                            courseTransferring("Saturday")
                            courseTransferring("Sunday")

                            Handler().postDelayed(Runnable {
                            FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses").child(tempId).removeValue()
                            hideProgressBar()
                            }, 1500)


                            val dialogClickListener =
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            val i = Intent(this@ScheduleCreator, CoursesActivity::class.java)
                                            startActivity(i)
                                            finish()
                                        }
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                            val i = Intent(this@ScheduleCreator, CoursesActivity::class.java)
                                            startActivity(i)
                                            finish()
                                        }
                                    }
                                }

                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(this@ScheduleCreator)
                            builder.setMessage("Redirect to")
                                .setPositiveButton("My courses", dialogClickListener)
                                .setNegativeButton(
                                    "CourseFinder",
                                    dialogClickListener
                                ).show()

                        }
                        DialogInterface.BUTTON_NEGATIVE -> {

                        }
                    }
                }

            val builder: AlertDialog.Builder =
                AlertDialog.Builder(this@ScheduleCreator)
            builder.setMessage("You are about to finish your course creation")
                .setPositiveButton("Finish", dialogClickListener)
                .setNegativeButton(
                    "Cancel",
                    dialogClickListener
                ).show()





        }
    }
    private fun initWorkout(){

        val tempId = intent.getStringExtra("currCourseId")
        var tempTitle = intent.getStringExtra("currCourseName").toString()
        if (intent.getStringExtra("currCourseName").toString().length > 15) {
            tempTitle = tempTitle.subSequence(0, 15).toString().plus("...")
        }

        adapterWorkouts.workoutsList.clear()
        binding.apply {
            workoutsRcView.layoutManager = LinearLayoutManager(this@ScheduleCreator)
            workoutsRcView.adapter = adapterWorkouts

            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/${tempId}/Workouts")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val workout = snapshot!!.children

                        workout.forEach {
                            val setsList = ArrayList<ExerciseSetForDB>()
                            val set = it!!.children
                            set.forEach{
                                setsList.add(it.getValue(ExerciseSetForDB::class.java)!!)
                            }

                            adapterWorkouts.addWorkout((Workout(it.key.toString(), setsList)))

                        }



                    }
                }

            })

        }

        adapterMonday.setOnItemClickListener(object:MondayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterMonday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterMonday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterMonday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterMonday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterMonday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterMonday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterMonday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterMonday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterMonday.workoutsList.size

                                adapterMonday.workoutsList.removeAt(adapterMonday.workoutsList.indexOf(workout))
                                adapterMonday.notifyItemRangeRemoved(0, prsize);
                                adapterMonday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterTuesday.setOnItemClickListener(object:TuesdayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterTuesday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterTuesday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterTuesday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterTuesday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterTuesday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterTuesday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterTuesday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterTuesday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterTuesday.workoutsList.size

                                adapterTuesday.workoutsList.removeAt(adapterTuesday.workoutsList.indexOf(workout))
                                adapterTuesday.notifyItemRangeRemoved(0, prsize);
                                adapterTuesday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterWednesday.setOnItemClickListener(object:WednesdayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterWednesday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterWednesday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterWednesday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterWednesday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterWednesday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterWednesday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterWednesday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterWednesday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterWednesday.workoutsList.size

                                adapterWednesday.workoutsList.removeAt(adapterTuesday.workoutsList.indexOf(workout))
                                adapterWednesday.notifyItemRangeRemoved(0, prsize);
                                adapterWednesday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterThursday.setOnItemClickListener(object:ThursdayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterThursday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterThursday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterThursday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterThursday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterThursday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterThursday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterThursday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterThursday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterThursday.workoutsList.size

                                adapterThursday.workoutsList.removeAt(adapterThursday.workoutsList.indexOf(workout))
                                adapterThursday.notifyItemRangeRemoved(0, prsize);
                                adapterThursday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterFriday.setOnItemClickListener(object:FridayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterFriday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterFriday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterFriday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterFriday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterFriday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterFriday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterFriday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterFriday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterFriday.workoutsList.size

                                adapterFriday.workoutsList.removeAt(adapterFriday.workoutsList.indexOf(workout))
                                adapterFriday.notifyItemRangeRemoved(0, prsize);
                                adapterFriday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterSaturday.setOnItemClickListener(object:SaturdayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterSaturday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterSaturday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterSaturday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterSaturday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterSaturday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterSaturday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterSaturday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterSaturday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterSaturday.workoutsList.size

                                adapterSaturday.workoutsList.removeAt(adapterSaturday.workoutsList.indexOf(workout))
                                adapterSaturday.notifyItemRangeRemoved(0, prsize);
                                adapterSaturday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })
        adapterSunday.setOnItemClickListener(object:SundayAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterSunday.apply {
                                                    if(workoutsList.indexOf(workout) != 0){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) - 1]
                                                        workoutsList[tempPos - 1] = temp
                                                        adapterSunday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterSunday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterSunday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterSunday.apply {
                                                    if(workoutsList.indexOf(workout) != workoutsList.size - 1){
                                                        val temp = workout
                                                        val tempPos = workoutsList.indexOf(temp)
                                                        workoutsList[workoutsList.indexOf(temp)] = workoutsList[workoutsList.indexOf(temp) + 1]
                                                        workoutsList[tempPos + 1] = temp
                                                        adapterSunday.notifyItemRangeRemoved(0, workoutsList.size);
                                                        adapterSunday.notifyItemRangeInserted(0, workoutsList.size);
                                                        adapterSunday.notifyItemRangeChanged(0, workoutsList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@ScheduleCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@ScheduleCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterSunday.workoutsList.size

                                adapterSunday.workoutsList.removeAt(adapterSunday.workoutsList.indexOf(workout))
                                adapterSunday.notifyItemRangeRemoved(0, prsize);
                                adapterSunday.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@ScheduleCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }

        })


        adapterWorkouts.setOnItemClickListener(object: WorkoutAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                if(this@ScheduleCreator::tempItem.isInitialized){
                    tempItem.findViewById<CardView>(R.id.cardViewItem).setBackgroundColor(
                        Color.parseColor("#ffffff"))}
                item.findViewById<CardView>(R.id.cardViewItem).setBackgroundColor(
                    Color.parseColor("#4D718BC3"))
                tempItem = item
                binding.workoutsRcView.scrollToPosition(adapterWorkouts.workoutsList.indexOf(workout))

                binding.placeWorkoutCW.isVisible = true

                binding.mondayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.fullScroll(View.FOCUS_LEFT)}

                        adapterMonday.workoutsList.add(workout)
                        adapterMonday.notifyItemRangeRemoved(0, adapterMonday.workoutsList.size)
                        adapterMonday.notifyItemRangeInserted(0, adapterMonday.workoutsList.size)
                        mondayRcView.post{mondayRcView.scrollToPosition(adapterMonday.workoutsList.size - 1)}
                    }
                }
                binding.tuesdayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.scrollTo(570, daysScrlView.y.toInt())}

                       adapterTuesday.workoutsList.add(workout)
                        adapterTuesday.notifyItemRangeRemoved(0, adapterTuesday.workoutsList.size)
                        adapterTuesday.notifyItemRangeInserted(0, adapterTuesday.workoutsList.size)
                       tuesdayRcView.post{tuesdayRcView.scrollToPosition(adapterTuesday.workoutsList.size - 1)}
                    }
                }
                binding.wednesdayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.scrollTo(1140, daysScrlView.y.toInt())}

                        adapterWednesday.workoutsList.add(workout)
                        adapterWednesday.notifyItemRangeRemoved(0, adapterWednesday.workoutsList.size)
                        adapterWednesday.notifyItemRangeInserted(0, adapterWednesday.workoutsList.size)
                        wednesdayRcView.post{wednesdayRcView.scrollToPosition(adapterWednesday.workoutsList.size - 1)}
                    }
                }
                binding.thursdayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.scrollTo(1710, daysScrlView.y.toInt())}

                        adapterThursday.workoutsList.add(workout)
                        adapterThursday.notifyItemRangeRemoved(0, adapterThursday.workoutsList.size)
                        adapterThursday.notifyItemRangeInserted(0, adapterThursday.workoutsList.size)
                        thursdayRcView.post{thursdayRcView.scrollToPosition(adapterThursday.workoutsList.size - 1)}
                    }
                }
                binding.fridayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.scrollTo(2280, daysScrlView.y.toInt())}

                        adapterFriday.workoutsList.add(workout)
                        adapterFriday.notifyItemRangeRemoved(0, adapterFriday.workoutsList.size)
                        adapterFriday.notifyItemRangeInserted(0, adapterFriday.workoutsList.size)
                        fridayRcView.post{fridayRcView.scrollToPosition(adapterFriday.workoutsList.size - 1)}
                    }
                }
                binding.saturdayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.scrollTo(2850, daysScrlView.y.toInt())}

                        adapterSaturday.workoutsList.add(workout)
                        adapterSaturday.notifyItemRangeRemoved(0, adapterSaturday.workoutsList.size)
                        adapterSaturday.notifyItemRangeInserted(0, adapterSaturday.workoutsList.size)
                        saturdayRcView.post{saturdayRcView.scrollToPosition(adapterSaturday.workoutsList.size - 1)}
                    }
                }
                binding.sundayBtn.setOnClickListener {
                    binding.apply {
                        daysScrlView.post{daysScrlView.fullScroll(View.FOCUS_RIGHT)}

                        adapterSunday.workoutsList.add(workout)
                        adapterSunday.notifyItemRangeRemoved(0, adapterSunday.workoutsList.size)
                        adapterSunday.notifyItemRangeInserted(0, adapterSunday.workoutsList.size)
                        sundayRcView.post{sundayRcView.scrollToPosition(adapterSunday.workoutsList.size - 1)}
                    }
                }
            }

        })

    }
    private fun showProgressBar(){
        dialog = Dialog(this@ScheduleCreator)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        if (!this@ScheduleCreator.isFinishing) {
            dialog.show()
        }

    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}