package com.nb.trackerapp.models

import java.io.Serializable

data class User(
    val token:String,
    val type:String
) : Serializable