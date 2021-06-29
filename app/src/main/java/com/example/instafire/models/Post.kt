package com.example.instafire.models

import com.google.firebase.firestore.PropertyName

data class Post (
    var description:String="",
    @get:PropertyName("image_url")@set:PropertyName("image_url") var imageurl:String="",
    @get:PropertyName("creation_time_ms")@set:PropertyName("creation_time_ms") var creationtime:Long=0,
    var user:User?=null


)