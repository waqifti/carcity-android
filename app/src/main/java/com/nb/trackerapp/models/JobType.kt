package com.nb.trackerapp.models

data class JobType(
    val type:String,
    val isChecked:Boolean = false
){
    companion object{
        var selectedJobList:ArrayList<String> = ArrayList()
    }
}