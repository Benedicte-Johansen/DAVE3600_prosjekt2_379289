package com.example.dave3600_prosjekt2_379289.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Friend(
    val id: Long = 0,
    val name: String,
    val birthDate: String,
    val phone: String
) : Parcelable
