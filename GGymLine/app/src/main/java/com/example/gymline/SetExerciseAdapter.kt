package com.example.gymline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymline.databinding.ExerciseSetItemBinding

class SetExerciseAdapter: RecyclerView.Adapter<SetExerciseAdapter.SetExerciseHolder>() {
    val setExerciseList = ArrayList<ExerciseSet>()

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(exerciseSet: ExerciseSet, item: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class SetExerciseHolder(item: View, listener: OnItemClickListener): RecyclerView.ViewHolder(item) {
        val binding =  ExerciseSetItemBinding.bind(item)
        lateinit var exerciseSet1: ExerciseSet

        fun bind(exerciseSet: ExerciseSet) = with(binding){
            exerciseSetTitle.text = exerciseSet.exercise.exTitle
            exerciseSetRepeats.text = exerciseSet.repeats
            imgViewSet.setImageBitmap(exerciseSet.exercise.exImg)
            exerciseSet1 = exerciseSet
        }

        init{
            item.setOnClickListener{
                listener.onItemClick(exerciseSet1, item)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetExerciseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_set_item, parent, false)
        return SetExerciseHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SetExerciseHolder, position: Int) {
        holder.bind(setExerciseList[position])
    }

    override fun getItemCount(): Int {
        return setExerciseList.size
    }

    fun addSet(exerciseSet: ExerciseSet) {
        setExerciseList.add(exerciseSet)
        notifyDataSetChanged()
    }
    fun clear() {
        val size = setExerciseList.size
        setExerciseList.clear()
        notifyItemRangeRemoved(0, size)
    }


}