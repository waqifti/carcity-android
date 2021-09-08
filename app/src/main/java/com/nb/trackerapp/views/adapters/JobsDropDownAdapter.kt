package com.nb.trackerapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.nb.trackerapp.R
import com.nb.trackerapp.models.Job

class JobsDropDownAdapter(val context: Context,private val jobIdList:ArrayList<Job>) :
    RecyclerView.Adapter<JobsDropDownAdapter.JobViewHolder>() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    class JobViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val jobIdTv = itemView.findViewById<AppCompatTextView>(R.id.single_job_idTv)
        private val checkBox = itemView.findViewById<AppCompatCheckBox>(R.id.single_job_idCheckBox)

        fun bindData(job: Job){
            jobIdTv.text = job.id
            checkBox.isChecked = job.isChecked
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(inflater.inflate(R.layout.layout_single_job_id_dropdown,parent,false))
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bindData(jobIdList[position])
    }

    override fun getItemCount(): Int {
        return jobIdList.size
    }
}