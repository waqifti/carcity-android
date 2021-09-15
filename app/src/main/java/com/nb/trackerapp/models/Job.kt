package com.nb.trackerapp.models

data class Job(
    val id:Int?,
    val dbentryat:String?,
    val state:String?,
    val description:String?,
    val notes:String?,
    val assignedto:String?,
    val managedby:String?
)