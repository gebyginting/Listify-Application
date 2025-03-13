package com.geby.listifyapplication.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (
    val name: String = "Guest"
) : Parcelable