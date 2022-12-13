package com.example.gymline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymline.databinding.CourseItemBinding
import com.example.gymline.databinding.ExerciseItemBinding
import kotlin.system.exitProcess

class ExerciseAdapter: RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder>() {
    val exerciseList = ArrayList<Exercise>()

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(exercise:Exercise, item: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class ExerciseHolder(item: View, listener: OnItemClickListener): RecyclerView.ViewHolder(item) {
        val binding = ExerciseItemBinding.bind(item)
        lateinit var exercise1: Exercise

        fun bind(exercise: Exercise) = with(binding){
            exerciseTitle.text = exercise.exTitle
            exerciseRepeats.text = "*repeats*"
            exerciseDesc.text = exercise.exDesc
            exerciseImg.setImageBitmap(exercise.exImg)
            exercise1 = exercise

        }

        init{
            item.setOnClickListener{
                listener.onItemClick(exercise1, item)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return ExerciseHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        holder.bind(exerciseList[position])
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    fun addCourse(exercise: Exercise) {
        exerciseList.add(exercise)
        notifyDataSetChanged()
    }
    fun clear() {
        val size = exerciseList.size
        exerciseList.clear()
        notifyItemRangeRemoved(0, size)
    }


}