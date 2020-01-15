package com.example.android.navigation.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var uid: String, var position:String, var username:String, var email:String):Parcelable
{
    //empty constrcutor
    constructor() : this("","","",""){
    }

}