package com.example.gymline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymline.databinding.ExerciseSetItemBinding

class SetForViewAdapter: RecyclerView.Adapter<SetForViewAdapter.SetForViewHolder>() {
    val setExerciseList = ArrayList<ExerciseSet>()

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(exerciseSet: ExerciseSet, item: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    class SetForViewHolder(item: View, listener: OnItemClickListener): RecyclerView.ViewHolder(item) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetForViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_set_item, parent, false)
        return SetForViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SetForViewHolder, position: Int) {
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