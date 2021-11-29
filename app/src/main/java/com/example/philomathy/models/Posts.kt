package com.example.philomathy.models

import com.google.firebase.database.PropertyName

class Posts(

    var description:String = "",
    @get:PropertyName("imageurl") @set:PropertyName("imageurl") var imageurl: String = "",
    @get:PropertyName("creationtime") @set:PropertyName("creationtime") var creationtime: Long = 0,
    var user: Users? = null,
    var title: String = "",

    )