package com.example.fitnesstrackerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(private val activities: List<ActivityData>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvActivityType: TextView = view.findViewById(R.id.tvActivityType)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvCaloriesBurned: TextView = view.findViewById(R.id.tvCaloriesBurned)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.tvActivityType.text = "Activity: ${activity.activityType}"
        holder.tvDate.text = "Date: ${activity.date}"
        holder.tvDuration.text = "Duration: ${activity.duration / 1000} seconds"
        holder.tvCaloriesBurned.text = "Calories: ${activity.caloriesBurned} kcal"
    }

    override fun getItemCount(): Int = activities.size
}
