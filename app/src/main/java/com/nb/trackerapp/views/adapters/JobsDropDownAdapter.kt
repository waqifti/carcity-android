package com.nb.trackerapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.nb.trackerapp.R
import com.nb.trackerapp.models.JobType

class JobsDropDownAdapter(val context: Context,private val jobTypeList:ArrayList<JobType>) :
    RecyclerView.Adapter<JobsDropDownAdapter.JobViewHolder>() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    class JobViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val jobTypeTv = itemView.findViewById<AppCompatTextView>(R.id.single_job_typeTv)
        private val checkBox = itemView.findViewById<AppCompatCheckBox>(R.id.single_job_idCheckBox)

        fun bindData(jobType: JobType){
            jobTypeTv.text = jobType.type
            checkBox.isChecked = jobType.isChecked

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                addSelectedJob(jobType.type,isChecked)
            }
        }

        private fun addSelectedJob(jobType:String,isChecked:Boolean){
            if(isChecked){ JobType.selectedJobList.add(jobType) }
            else{
                if(!JobType.selectedJobList.isNullOrEmpty() && JobType.selectedJobList.size != 0) {
                    val findItem = JobType.selectedJobList.find { it == jobType }
                    findItem?.let { JobType.selectedJobList.remove(it) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(inflater.inflate(R.layout.layout_single_job_id_dropdown,parent,false))
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bindData(jobTypeList[position])
    }

    override fun getItemCount(): Int {
        return jobTypeList.size
    }
}