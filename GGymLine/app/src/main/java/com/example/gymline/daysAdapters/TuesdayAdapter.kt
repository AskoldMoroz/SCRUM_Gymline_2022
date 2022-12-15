package com.example.gymline.daysAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymline.R
import com.example.gymline.Workout
import com.example.gymline.databinding.WorkoutItemBinding

class TuesdayAdapter: RecyclerView.Adapter<TuesdayAdapter.TuesdayHolder>() {
    val workoutsList = ArrayList<Workout>()

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(workout: Workout, item: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class TuesdayHolder(item: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(item) {
        val binding = WorkoutItemBinding.bind(item)
        lateinit var workout1: Workout

        fun bind(workout: Workout) = with(binding) {
            workoutTitle.text = workout.name
            exercisesCount.text = "Exercises: ${workout.setsList.size}"
            workout1 = workout
        }

        init {
            item.setOnClickListener {
                listener.onItemClick(workout1, item)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TuesdayHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_item, parent, false)
        return TuesdayHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: TuesdayHolder, position: Int) {
        holder.bind(workoutsList[position])
    }

    override fun getItemCount(): Int {
        return workoutsList.size
    }

    fun addWorkout(workout: Workout) {
        workoutsList.add(workout)
        notifyDataSetChanged()
    }

    fun clear() {
        val size = workoutsList.size
        workoutsList.clear()
        notifyItemRangeRemoved(0, size)
    }
}

