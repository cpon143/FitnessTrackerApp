package com.example.fitnesstrackerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstrackerapp.database.Workout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WorkoutHistoryAdapter : ListAdapter<Workout, WorkoutHistoryAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_history, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = getItem(position)
        holder.bind(workout)
    }

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesTextView)

        fun bind(workout: Workout) {
            // First, parse the date from the string to a Date object
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = try {
                dateFormat.parse(workout.startTime) // This will parse the string into Date
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }

            // If date is successfully parsed, format it for display
            if (date != null) {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
                dateTextView.text = formattedDate
            } else {
                dateTextView.text = "Invalid Date"
            }

            // Set other workout details to the corresponding TextViews
            durationTextView.text = "Duration: ${workout.duration} sec"
            distanceTextView.text = "Distance: %.2f km".format(workout.distance)
            caloriesTextView.text = "Calories: %.2f kcal".format(workout.calories)
        }

    }

    // DiffUtil class to optimize updates to the list
    class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.id == newItem.id  // Assuming Workout has a unique `id`
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem  // Check if the content is the same
        }
    }
}
