package com.example.gymline

class ExerciseSet(var exercise: Exercise, var repeats: String) {

}
class ExerciseSetForDB(var exerciseName: String? = null, var repeats: String? = null)
class Workout(var name: String, var setsList: ArrayList<ExerciseSetForDB>)

class WorkoutForDB(val setsList: ArrayList<ExerciseSetForDB>)