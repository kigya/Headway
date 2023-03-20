package com.kigya.headway.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceDomainModel(
    val id: String? = null,
    val name: String? = null,
) : Parcelable