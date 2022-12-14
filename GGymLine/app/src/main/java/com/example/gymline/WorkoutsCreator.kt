package com.example.gymline

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymline.databinding.ActivityExerciseCreatorBinding
import com.example.gymline.databinding.ActivityWorkoutsCreatorBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class WorkoutsCreator : AppCompatActivity() {

    private val adapterWorkouts = WorkoutAdapter()
    private val adapterSets = SetExerciseAdapter()
    private val adapterEx = ExerciseAdapter()

    private val adapterSetForView = SetForViewAdapter()

    private lateinit var dialog: Dialog

    lateinit var binding : ActivityWorkoutsCreatorBinding

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var tempItem: View

    private var exerciseSetForDBList = ArrayList<ExerciseSetForDB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutsCreatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcViewWorkoutsSets.adapter = adapterWorkouts

        initEx()
        initSet()
        initWorkout()

        val spinner: Spinner = binding.spinnerRepMin
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.repormin,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        binding.inputRepeatText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.inputRepeatText.windowToken, 0)
                true
            } else false
        }
    }
    private fun initEx(){

        val tempId = intent.getStringExtra("currCourseId")
        var tempTitle = intent.getStringExtra("currCourseName").toString()
        if (intent.getStringExtra("currCourseName").toString().length > 15) {
            tempTitle = tempTitle.subSequence(0, 15).toString().plus("...")
        }

        adapterEx.exerciseList.clear()
        binding.apply {
            rcViewEx.layoutManager = LinearLayoutManager(this@WorkoutsCreator)
            rcViewEx.adapter = adapterEx

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

                                adapterEx.addCourse(exercise)

                            }


                        }
                    }
                }
            })

        }


        adapterEx.setOnItemClickListener(object: ExerciseAdapter.OnItemClickListener{
            override fun onItemClick(exercise: Exercise, item: View) {
                binding.apply {
                    if(this@WorkoutsCreator::tempItem.isInitialized){
                    tempItem.findViewById<CardView>(R.id.cardViewItem).setBackgroundColor(
                        Color.parseColor("#ffffff"))}
                    item.findViewById<CardView>(R.id.cardViewItem).setBackgroundColor(
                        Color.parseColor("#4D718BC3"))
                    tempItem = item

                    repsCW.visibility = View.VISIBLE
                    addSetBtn.setOnClickListener{
                        if(inputRepeatText.text.toString().trim().isNotEmpty()){
                            val exerciseSet = ExerciseSet(exercise, inputRepeatText.text.toString().trim().plus(" ").plus(spinnerRepMin.selectedItem.toString()))
                            adapterSets.addSet(exerciseSet)
                            rcViewWorkoutsSets.post{rcViewWorkoutsSets.scrollToPosition(adapterSets.setExerciseList.size - 1)}
                        }
                        else{
                            Toast.makeText(this@WorkoutsCreator,
                                "Enter the amount", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                binding.scrViewEx
                binding.scrViewEx.post{binding.scrViewEx.fullScroll(View.FOCUS_DOWN)}
            }

        })

        binding.openAddWorkoutBtn.setOnClickListener {
            if(binding.openAddWorkoutBtn.text == "Add New Workout"){
                binding.apply {
                    existingCW.visibility = View.VISIBLE
                    exRcViewCW.visibility = View.VISIBLE
                    repsCW.visibility = View.VISIBLE
                    binding.cardView4.visibility = View.VISIBLE
                    textViewExWork.text = "Sets in workout:"

                    rcViewWorkoutsSets.layoutManager = LinearLayoutManager(this@WorkoutsCreator)
                    rcViewWorkoutsSets.adapter = adapterSets

                    binding.openAddWorkoutBtn.text = "Cancel"

                    workoutDeleteBtn.visibility = View.GONE

                    rcViewEx.adapter = adapterEx
                }
            }
            else{
                binding.apply {
                    existingCW.visibility = View.GONE
                    exRcViewCW.visibility = View.GONE
                    repsCW.visibility = View.GONE
                    binding.cardView4.visibility = View.GONE
                    textViewExWork.text = "Existing workouts:"

                    rcViewWorkoutsSets.layoutManager = LinearLayoutManager(this@WorkoutsCreator)
                    rcViewWorkoutsSets.adapter = adapterWorkouts

                    binding.openAddWorkoutBtn.text = "Add New Workout"
                }
            }

        }
        binding.saveWorkoutBtn.setOnClickListener {
            if(adapterSets.setExerciseList.size > 0){
                if(binding.inputWorkoutName.text.toString().trim().isNotEmpty()){
                    var i = 0
                    while(adapterWorkouts.workoutsList.size > i){
                        if(adapterWorkouts.workoutsList[i].name == binding.inputWorkoutName.text.toString().trim()){
                            break
                        }
                        i++
                    }
                    if(i == adapterWorkouts.workoutsList.size){
                        var index = 0
                        while(adapterSets.setExerciseList.size > index){
                            val exerciseSetForDb = ExerciseSetForDB(adapterSets.setExerciseList[index].exercise.exTitle.toString(), adapterSets.setExerciseList[index].repeats)
                            databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Workouts/${binding.inputWorkoutName.text.toString().trim()}")
                            databaseReference.child("Set$index").setValue(exerciseSetForDb).addOnCompleteListener{
                                if(it.isSuccessful){


                                }
                                else{
                                    Toast.makeText(this@WorkoutsCreator,
                                        "Please try again", Toast.LENGTH_SHORT).show()

                                }
                            }
                            index++
                        }
                        Toast.makeText(this@WorkoutsCreator,
                            "Success", Toast.LENGTH_SHORT).show()
                        initWorkout()
                        binding.apply {
                            textViewExWork.text = "Existing workouts:"
                            existingCW.visibility = View.GONE
                            exRcViewCW.visibility = View.GONE
                            repsCW.visibility = View.GONE
                            cardView4.visibility = View.GONE
                            openAddWorkoutBtn.isEnabled = true
                            openAddWorkoutBtn.text = "Add New Workout"
                            adapterSets.setExerciseList.clear()
                        }

                    }
                    else{
                        Toast.makeText(this@WorkoutsCreator,
                            "Workout with this name is already existed", Toast.LENGTH_SHORT).show()
                    }


                }
                else{
                    Toast.makeText(this@WorkoutsCreator,
                        "Enter the workout name", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@WorkoutsCreator,
                    "Add at least one workout", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextBtn.setOnClickListener {
            val i = Intent(this@WorkoutsCreator, ScheduleCreator::class.java)
            i.putExtra("currCourseId", tempId)
            i.putExtra("currCourseName", tempTitle)
            startActivity(i)
            finish()
        }
    }

    private fun initSet(){

        adapterSets.setOnItemClickListener(object: SetExerciseAdapter.OnItemClickListener{
            override fun onItemClick(exerciseSet: ExerciseSet, item: View) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val dialogClickListener1 =
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> {
                                                adapterSets.apply {
                                                    if(setExerciseList.indexOf(exerciseSet) != 0){
                                                        val temp = exerciseSet
                                                        val tempPos = setExerciseList.indexOf(temp)
                                                        setExerciseList[setExerciseList.indexOf(temp)] = setExerciseList[setExerciseList.indexOf(temp) - 1]
                                                        setExerciseList[tempPos - 1] = temp
                                                        adapterSets.notifyItemRangeRemoved(0, setExerciseList.size);
                                                        adapterSets.notifyItemRangeInserted(0, setExerciseList.size);
                                                        adapterSets.notifyItemRangeChanged(0, setExerciseList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@WorkoutsCreator,
                                                            "It's top element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                                adapterSets.apply {
                                                    if(setExerciseList.indexOf(exerciseSet) != setExerciseList.size - 1){
                                                        val temp = exerciseSet
                                                        val tempPos = setExerciseList.indexOf(temp)
                                                        setExerciseList[setExerciseList.indexOf(temp)] = setExerciseList[setExerciseList.indexOf(temp) + 1]
                                                        setExerciseList[tempPos + 1] = temp
                                                        adapterSets.notifyItemRangeRemoved(0, setExerciseList.size);
                                                        adapterSets.notifyItemRangeInserted(0, setExerciseList.size);
                                                        adapterSets.notifyItemRangeChanged(0, setExerciseList.size);
                                                    }
                                                    else{
                                                        Toast.makeText(this@WorkoutsCreator,
                                                            "It's bottom element", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                            }
                                        }
                                    }

                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@WorkoutsCreator)
                                builder.setMessage("Choose Action")
                                    .setPositiveButton("Move Up", dialogClickListener1)
                                    .setNegativeButton(
                                        "Move Down",
                                        dialogClickListener1
                                    ).show()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                val prsize = adapterSets.setExerciseList.size

                                adapterSets.setExerciseList.removeAt(adapterSets.setExerciseList.indexOf(exerciseSet))
                                adapterSets.notifyItemRangeRemoved(0, prsize);
                                adapterSets.notifyItemRangeInserted(0, prsize - 1);
                            }
                        }
                    }

                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(this@WorkoutsCreator)
                builder.setMessage("Choose Action")
                    .setPositiveButton("Change position", dialogClickListener)
                    .setNegativeButton(
                        "Remove",
                        dialogClickListener
                    ).show()
            }



        })


    }
    private fun initWorkout(){

        val tempId = intent.getStringExtra("currCourseId")
        var tempTitle = intent.getStringExtra("currCourseName").toString()
        if (intent.getStringExtra("currCourseName").toString().length > 15) {
            tempTitle = tempTitle.subSequence(0, 15).toString().plus("...")
        }

        adapterWorkouts.workoutsList.clear()
        binding.apply {
            rcViewWorkoutsSets.layoutManager = LinearLayoutManager(this@WorkoutsCreator)
            rcViewWorkoutsSets.adapter = adapterWorkouts

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

        adapterSetForView.setOnItemClickListener(object: SetForViewAdapter.OnItemClickListener{
            override fun onItemClick(exerciseSet: ExerciseSet, item: View) {
            }

        })

        adapterWorkouts.setOnItemClickListener(object: WorkoutAdapter.OnItemClickListener{
            override fun onItemClick(workout: Workout, item: View) {
                adapterSetForView.setExerciseList.clear()
                exerciseSetForDBList.clear()
                showProgressBar()
                binding.apply {
                    rcViewEx.layoutManager = LinearLayoutManager(this@WorkoutsCreator)
                    rcViewEx.adapter = adapterSetForView

                    exRcViewCW.visibility = View.VISIBLE

                    databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/${tempId}/Workouts/${workout.name}")
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val set = snapshot!!.children
                                set.forEach {
                                    val exerciseSetForDB = it.getValue(ExerciseSetForDB::class.java)!!
                                    exerciseSetForDBList.add(exerciseSetForDB)
                                }
                                for (i in exerciseSetForDBList.size - 1 downTo  0 step 1) {

                                    databaseReference = FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/${tempId}/Exercises/${exerciseSetForDBList[i].exerciseName}")
                                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(snapshotError: DatabaseError) {
                                        }
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.exists()){
                                                val exerciseShort = snapshot.getValue(ExerciseShort::class.java)!!

                                                val id = snapshot.key!!.toString()

                                                storageReference = FirebaseStorage.getInstance().reference.child("UnfinishedCourses/${tempId}/Exercises/${id}.jpg")
                                                val localFile = File.createTempFile("tempImage", "jpg")
                                                storageReference.getFile(localFile).addOnSuccessListener {
                                                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)


                                                    val exercise =
                                                        Exercise(exerciseShort!!.exTitleShort, bitmap, exerciseShort.exDescShort, id)
                                                    adapterSetForView.setExerciseList.add(ExerciseSet(exercise, exerciseSetForDBList[i].repeats.toString()))
                                                }


                                            }
                                        }

                                    })
                                }
                                Handler().postDelayed(Runnable {

                                    adapterSetForView.notifyItemRangeRemoved(0, adapterSetForView.setExerciseList.size)
                                    adapterSetForView.notifyItemRangeInserted(0, adapterSetForView.setExerciseList.size)
                                    hideProgressBar()

                                }, 2000)
                            }
                        }

                    })



                }

                binding.workoutDeleteBtn.visibility = View.VISIBLE
                binding.workoutDeleteBtn.setOnClickListener {
                    FirebaseDatabase.getInstance("https://gymline-33603-default-rtdb.europe-west1.firebasedatabase.app").getReference("UnfinishedCourses/$tempId/Workouts").child(workout.name.toString()).removeValue()

                    val i = Intent(this@WorkoutsCreator, WorkoutsCreator::class.java)
                    i.putExtra("currCourseId", tempId)
                    i.putExtra("currCourseName", tempTitle)

                    startActivity(i)
                    finish()
                }
            }

        })




        binding.backBtn.setOnClickListener {
            val i = Intent(this@WorkoutsCreator, ExerciseCreator::class.java)
            i.putExtra("currCourseId", tempId)
            i.putExtra("currCourseName", tempTitle)
            startActivity(i)
            finish()
        }

    }

    private fun showProgressBar(){
        dialog = Dialog(this@WorkoutsCreator)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        if (!this@WorkoutsCreator.isFinishing) {
            dialog.show()
        }

    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }



}